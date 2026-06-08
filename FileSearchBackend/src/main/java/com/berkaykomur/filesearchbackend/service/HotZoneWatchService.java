package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import com.berkaykomur.filesearchbackend.worker.FileCoordinator;
import com.berkaykomur.filesearchbackend.worker.FileProducer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.nio.file.StandardWatchEventKinds.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotZoneWatchService {


    private final FileRepository fileRepository;
    private ScheduledExecutorService commitScheduler;
    private final LuceneIndexService luceneIndexService;
    private final FileProducer fileProducer;

    private final FileCoordinator fileCoordinator;
    private final Map<WatchKey, Path> watchKeyMap = new HashMap<>();

    private WatchService watchService;
    private ExecutorService executor;

    public void start() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "HotZone-Watcher");
                t.setDaemon(true);
                return t;
            });

            commitScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Lucene-Commit");
                t.setDaemon(true);
                return t;
            });
            commitScheduler.scheduleAtFixedRate(
                    luceneIndexService::completeIndexing,
                    30,
                    30,
                    TimeUnit.SECONDS
            );

            fileCoordinator.startThreads(true);
            registerHotZones();
            executor.submit(this::watchLoop);
            log.info("HotZoneWatchService başlatıldı, {} klasör izleniyor.", watchKeyMap.size());

        } catch (IOException e) {
            log.error("WatchService başlatılamadı: ", e);
        }
    }
    private void registerHotZones() throws IOException {
        for (String zone : FileUtil.HOT_ZONE_NAMES) {
            if (!Files.exists(Path.of(zone))) {
                log.warn("Hot zone bulunamadı, atlanıyor: {}", zone);
                continue;
            }
            registerRecursively(Path.of(zone));
        }
    }
    private void registerRecursively(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                String folderName = dir.getFileName().toString().toLowerCase();
                if (folderName.equals("node_modules")
                        || folderName.equals(".git")
                        || folderName.equals("target")
                        || folderName.equals("build")
                        || folderName.equals("appdata")) {

                    return FileVisitResult.SKIP_SUBTREE;
                }
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.warn("Klasör okunamadı, atlanıyor: {}", file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerDirectory(Path dir) {
        try {
            WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchKeyMap.put(key, dir);
        } catch (IOException e) {
            log.warn("Klasör izlemeye alınamadı: {} – {}", dir, e.getMessage());
        }
    }
    private void watchLoop() {
        log.info("WatchService olay döngüsü başladı.");

        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("WatchService döngüsü durduruldu.");
                break;
            }

            Path watchedDir = watchKeyMap.get(key);
            if (watchedDir == null) {
                key.cancel();
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    log.warn("WatchService OVERFLOW – bazı olaylar kaçırılmış olabilir.");
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path relative = pathEvent.context();
                Path fullPath = watchedDir.resolve(relative);

                handleEvent(kind, fullPath);
            }
            boolean valid = key.reset();
            if (!valid) {
                log.info("İzlenen klasör artık erişilemez, kaldırılıyor: {}", watchedDir);
                watchKeyMap.remove(key);
            }
        }
    }

    private void handleEvent(WatchEvent.Kind<?> kind, Path path) {
        if (kind == ENTRY_CREATE) {
            handleCreate(path);
        } else if (kind == ENTRY_MODIFY) {
            handleModify(path);
        } else if (kind == ENTRY_DELETE) {
            handleDelete(path);
        }
    }

    // Değişiklik algılanan dosyaları yönetme
    private void handleCreate(Path path) {
        if (Files.isDirectory(path)) {
            log.info("Yeni klasör algılandı, izlemeye alınıyor: {}", path);
            try {
                registerRecursively(path);
            } catch (IOException e) {
                log.error("Yeni klasör izlemeye alınamadı: {}", path);
            }
        }

        log.info("[OLUŞTURULDU] {}", path);
        enqueueFile(path, false);
        enqueueIndex(path);
    }

    private void handleModify(Path path) {
        if (!Files.isRegularFile(path)) return;
        log.info("[DEĞİŞTİRİLDİ] {}", path);
        enqueueFile(path, true);
        enqueueIndex(path);
    }

    private void handleDelete(Path path) {
        log.info("[SİLİNDİ] {}", path);
        String absolutePath = path.toAbsolutePath().toString().replace("\\", "/");
        try {
            fileRepository.deleteAllByPathIn(Set.of(absolutePath));
            log.debug("DB'den silindi: {}", absolutePath);
        } catch (Exception e) {
            log.error("DB silme hatası – {}: {}", absolutePath, e.getMessage());
        }

        String extension = FileUtil.getExtension(path.getFileName().toString());
        if (extension != null && FileProducer.TEXT_EXTENSIONS.contains(extension)) {
            luceneIndexService.deleteFromIndex(absolutePath);
        }
    }

    private void enqueueFile(Path path, boolean isDeltaUpdate) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileEntity entity = FileMapper.fromPathToFile(path, attrs);
            if (entity != null) {
                fileProducer.getFileQueue().put(entity);
                log.debug("fileQueue'ya eklendi (update={}): {}", isDeltaUpdate, path);
            }
        } catch (IOException e) {
            log.warn("Dosya nitelikleri okunamadı: {} – {}", path, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("fileQueue put kesildi: {}", e.getMessage());
        }
    }

    private void enqueueIndex(Path path) {
        String extension = FileUtil.getExtension(path.getFileName().toString());
        if (extension != null && FileProducer.TEXT_EXTENSIONS.contains(extension)) {
            try {
                fileProducer.getIndexQueue().put(path);
                log.debug("indexQueue'ya eklendi: {}", path);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("indexQueue put kesildi: {}", e.getMessage());
            }
        }
    }
    public boolean isPathAlreadyWatchedByHotZone(String absolutePath) {
        Path targetPath = Paths.get(absolutePath).toAbsolutePath().normalize();
        return watchKeyMap.values().stream().anyMatch(watchedDir -> {
            Path normalizedWatched = watchedDir.toAbsolutePath().normalize();

            return targetPath.equals(normalizedWatched) || targetPath.startsWith(normalizedWatched);
        });
    }

    @PreDestroy
    public void stop() {
        log.info("HotZoneWatchService durduruluyor...");
        if (executor != null) executor.shutdownNow();
        if (commitScheduler != null) commitScheduler.shutdownNow();
        luceneIndexService.completeIndexing();
        try {
            if (watchService != null) watchService.close();
        } catch (IOException e) {
            log.error("WatchService kapatma hatası: ", e);
        }
    }
}