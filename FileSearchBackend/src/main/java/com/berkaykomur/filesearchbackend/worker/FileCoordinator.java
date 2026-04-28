package com.berkaykomur.filesearchbackend.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileCoordinator {
    private final FileProducer fileProducer;
    private final DatabaseWorker databaseWorker;
    private final IndexWorker indexWorker;

    private static final int DB_WORKER_THREAD_COUNT = 3;
    private static final int INDEX_WORKER_THREAD_COUNT = 5;

    public void startFullProcess(String rootPath) {
        log.info("İşlem başlatılıyor. Hedef: {}", rootPath);

        for (int i = 0; i < DB_WORKER_THREAD_COUNT; i++) {
            databaseWorker.runDatabaseWorker();
        }

        for (int i = 0; i < INDEX_WORKER_THREAD_COUNT; i++) {
            indexWorker.runIndex();
        }

        try {
            fileProducer.scanAndSaveAllFiles(
                    Path.of(rootPath),
                    DB_WORKER_THREAD_COUNT,
                    INDEX_WORKER_THREAD_COUNT
            );
        } catch (IOException | InterruptedException e) {
            log.error("Kritik koordinasyon hatası: ", e);
            Thread.currentThread().interrupt();
        }
    }

}
