package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.model.FileLastScan;
import com.berkaykomur.filesearchbackend.repository.FileLastScanRepository;
import com.berkaykomur.filesearchbackend.service.LuceneIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    @Async
    public void startFullProcess(String rootPath, boolean isDeltaScan) {
        log.info("İşlem başlatılıyor. Hedef: {}", rootPath);
        long startTime = System.currentTimeMillis();
        List<Thread> threads = startThreads(isDeltaScan);
        try {
            fileProducer.scanAndSaveAllFiles(Path.of(rootPath),
                    DB_WORKER_THREAD_COUNT, INDEX_WORKER_THREAD_COUNT);
            log.info("Tüm tarama ve indexleme işlemi tamamlandı");

            for (Thread thread : threads) {
                thread.join();
            }
            luceneIndexService.completeIndexing();
            FileLastScan syncRecord = fileLastScanRepository.findById(1).orElse(new FileLastScan());
            syncRecord.setLastScanTime(System.currentTimeMillis());
            fileLastScanRepository.save(syncRecord);

            long endTime = System.currentTimeMillis(); // ← bitiş
            long duration = endTime - startTime;
            log.info("Tarama tamamlandı! Süre: {} saniye", duration / 1000);
        }
        catch (IOException | InterruptedException e) {
            log.error("Kritik koordinasyon hatası: ", e);
            Thread.currentThread().interrupt();
        }
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

}
