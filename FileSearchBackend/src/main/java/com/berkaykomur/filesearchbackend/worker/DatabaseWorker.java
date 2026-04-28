package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseWorker {

    private final BlockingQueue<FileEntity> fileQueue;
    private final FileRepository fileRepository;
    private final int BATCH_SIZE = 1000;

    @Async("taskExecutor")
    public void runDatabaseWorker() {
        log.info("Bir thread database worker'ı çalıştırdı: {}",Thread.currentThread().getName());
        List<FileEntity> batchList = new ArrayList<>();
        try {
            while (true) {
                FileEntity fileEntity = fileQueue.take();

                if (FileProducer.POISON_PILL_NAME.equals(fileEntity.getName())) {
                    log.info("Bitiş sinyali alındı kaydetme işlemi sona erecek");
                    fileQueue.put(fileEntity);
                    break;
                }

                batchList.add(fileEntity);

                if (batchList.size() >= BATCH_SIZE) {
                  saveInBatch(batchList);
                }
            }
            if (!batchList.isEmpty()) {
                saveInBatch(batchList);
            }
            log.info("Dosyalar veri tabanına kaydedildi!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("DB Worker kesildi.");
        }
    }

   private void saveInBatch(List<FileEntity> list) {
       if (list.isEmpty()) return;
       try {
           fileRepository.saveAll(list);
           fileRepository.flush();
       } finally {
           list.clear();
       }
   }

}
