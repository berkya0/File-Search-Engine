package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.model.FileLastScan;
import com.berkaykomur.filesearchbackend.repository.FileLastScanRepository;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import com.berkaykomur.filesearchbackend.worker.FileCoordinator;
import com.berkaykomur.filesearchbackend.worker.FileProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeltaScanService {

    private final FileLastScanRepository fileLastScanRepository;
    private final FileRepository fileRepository;
    private final BlockingQueue<FileEntity> fileQueue;
    private final BlockingQueue<Path> indexQueue;
    private final FileCoordinator fileCoordinator;
    private final LuceneIndexService luceneIndexService;
    private final FileProducer fileProducer;
    private final HotZoneWatchService  hotZoneWatchService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if(!(fileLastScanRepository.count()==0)){
            CompletableFuture.runAsync(() -> {
                try {
                    List<Thread> threads = fileCoordinator.startThreads(true);
                    deltaScan();
                    for (Thread thread : threads) {
                        thread.join();
                    }
                    updateLastScanTime();
                    luceneIndexService.completeIndexing();
                    hotZoneWatchService.start();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Delta scan kesildi: ", e);
                }
            }).exceptionally(e -> {
                log.error("Delta scan hatası: ", e);
                return null;
            });
        }
    }

    public void deltaScan() throws InterruptedException {
        log.info("Delta scan taraması başlatıldı");
        long start = System.currentTimeMillis();
        try {
            String userHome = System.getProperty("user.home");
            long lastScanTime = fileLastScanRepository.findByLastScanTime();

            for (String hotZone : FileUtil.HOT_ZONE_NAMES) {
                Path zone = Path.of(userHome, hotZone);
                Set<String> dbPaths = new HashSet<>(
                        fileRepository.findPathsByZone(zone.toAbsolutePath().toString())
                );
                Files.walkFileTree(zone, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (!attrs.isRegularFile()) {
                            return FileVisitResult.CONTINUE;
                        }
                        boolean isNewFile = !dbPaths.remove(file.toAbsolutePath().toString());

                        long fileTime = attrs.lastModifiedTime().toMillis();
                        boolean isModified=fileTime > lastScanTime;
                        if (isModified || isNewFile) {
                            try {
                                FileEntity fileEntity = FileMapper.fromPathToFile(file, attrs);
                                if (fileEntity != null) {
                                    fileQueue.put(fileEntity);
                                }
                                String extension = FileUtil.getExtension(file);
                                if (extension != null && FileProducer.TEXT_EXTENSIONS.contains(extension)) {
                                    indexQueue.put(file);
                                }
                            } catch (Exception e) {
                                log.error("Beklenmedik bir hata meydana geldi: {}", e.getMessage());
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        log.warn("Dosya okunamadığı için atlandı: {}", file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                if(!dbPaths.isEmpty()){
                    fileRepository.deleteAllByPathIn(dbPaths);
                    log.info("{} bölgesi için {} dosya silindi.", zone, dbPaths.size());
                }

            }
            fileProducer.endThreads();
            long lastTime = (System.currentTimeMillis() - start)/1000;
            log.info("Delta scan tamamlandı toplam süre: {} saniye", lastTime);

        } catch (IOException e) {
            log.error("Delta scan sırasında hata oluştu: ", e);
        }
    }
    private void updateLastScanTime() {
        var syncRecord = fileLastScanRepository.findById(1).orElse(new FileLastScan());
        syncRecord.setLastScanTime(System.currentTimeMillis());
        fileLastScanRepository.save(syncRecord);
    }

}