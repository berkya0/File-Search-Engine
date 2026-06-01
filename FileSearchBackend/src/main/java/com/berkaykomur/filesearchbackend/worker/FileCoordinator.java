package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.model.FileLastScan;
import com.berkaykomur.filesearchbackend.repository.FileLastScanRepository;
import com.berkaykomur.filesearchbackend.service.LuceneIndexService;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileCoordinator {
    private final FileProducer fileProducer;
    private final DatabaseWorker databaseWorker;
    private final IndexWorker indexWorker;
    private final FileLastScanRepository fileLastScanRepository;
    private final LuceneIndexService luceneIndexService;

    public static final int DB_WORKER_THREAD_COUNT = 3;
    public static final int INDEX_WORKER_THREAD_COUNT = 5;
    private List<Thread> threads;

    public void quickStart(){
       Set<String> quickFiles= FileUtil.HOT_ZONE_NAMES;
       log.info("Hızlı başlangıç yapılıyor klasörler: [{}]", quickFiles);
        long startTime = System.currentTimeMillis();
        threads = startThreads(false);
        try{
            for (String directory : quickFiles) {
                fileProducer.scanAndSaveAllFiles(Path.of(directory));
            }
            sendPoisonPills();
        }
        catch (IOException | InterruptedException e) {
            log.error("Kritik koordinasyon hatası: ", e);
            Thread.currentThread().interrupt();
        }
        waitThreads();
        completeProcess(startTime,"Hızlı Başlangıç tamamlandı");

    }

    public void scanSingleDirectory(String directoryPath) {
        log.info("Tek bir klasör taranıyor: {}", directoryPath);
        long startTime = System.currentTimeMillis();
        Path path = Path.of(directoryPath);
        threads = startThreads(false);
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(file -> {
                try {
                    FileEntity entity = FileMapper.fromPathToFile(file, Files.readAttributes(file, BasicFileAttributes.class));
                    fileProducer.getFileQueue().put(entity);

                    if (FileProducer.TEXT_EXTENSIONS.contains(FileUtil.getExtension(file.toString()))) {
                        fileProducer.getIndexQueue().put(file);
                    }
                } catch (Exception e) {
                    log.error("Hata: ", e);
                }
            });
        } catch (IOException e) {
            log.error("Klasör okunamadı: ", e);
        }
        sendPoisonPills();
        waitThreads();
        completeProcess(startTime,"Tek bir klasör başarıyla tarandı");

    }

    public void startFullProcess(String rootPath, boolean isDeltaScan) {
        log.info("İşlem başlatılıyor. Hedef: {}", rootPath);
        long startTime = System.currentTimeMillis();
        threads = startThreads(isDeltaScan);
        try {
            fileProducer.scanAndSaveAllFiles(Path.of(rootPath));
            sendPoisonPills();
        }
        catch (IOException | InterruptedException e) {
            log.error("Kritik koordinasyon hatası: ", e);
            Thread.currentThread().interrupt();
        }
        waitThreads();
        log.info("Tüm worker thread'lerin çalışması ve indeksleme işlemi tamamlandı.");
        completeProcess(startTime,"Tarama işlemi tamamlandı");
    }

    public List<Thread> startThreads(boolean isDeltaScan) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < DB_WORKER_THREAD_COUNT; i++) {
            Thread thread = new Thread(() -> databaseWorker.runDatabaseWorker(isDeltaScan));
            thread.setName("DB-Worker-" + i);
            thread.start();
            threads.add(thread);
        }
        for (int i = 0; i < INDEX_WORKER_THREAD_COUNT; i++) {
            Thread thread = new Thread(indexWorker::runIndex);
            thread.setName("Index-Worker-" + i);
            thread.start();
            threads.add(thread);
        }
        return threads;
    }
    private void waitThreads()  {
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            log.error("Threadler kesintiye uğradı {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    private void sendPoisonPills(){
        log.info("Tarama bitti, worker thread'leri kapatmak için zehirli haplar gönderiliyor...");

        try{
            for (int i = 0; i < DB_WORKER_THREAD_COUNT; i++) {
                FileEntity poisonPill = new FileEntity();
                poisonPill.setName(FileProducer.DB_POISON_PILL_NAME);
                fileProducer.getFileQueue().put(poisonPill);
            }
            for (int i = 0; i < INDEX_WORKER_THREAD_COUNT; i++) {
                fileProducer.getIndexQueue().put(FileProducer.IX_POISON);
            }
        } catch (InterruptedException e) {
            log.error("Threadler kesintiye uğradı, Zehirli haplar gönderilemedi {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        log.info("Tüm zehirli haplar başarıyla kuyruklara bırakıldı.");
    }

    private void completeProcess(long startTime, String logMessage) {
        luceneIndexService.completeIndexing();
        FileLastScan syncRecord = fileLastScanRepository.findById(1).orElse(new FileLastScan());
        syncRecord.setLastScanTime(System.currentTimeMillis());
        fileLastScanRepository.save(syncRecord);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("{} Süre: {} saniye", logMessage, duration / 1000);
    }
}
