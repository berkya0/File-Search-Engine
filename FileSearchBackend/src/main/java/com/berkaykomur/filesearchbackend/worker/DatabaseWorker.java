package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseWorker {

    private final BlockingQueue<FileEntity> fileQueue;
    private final FileRepository fileRepository;
    private final int BATCH_SIZE = 1000;

    public void runDatabaseWorker(boolean isDeltaScan) {
        log.info("Bir thread database worker'ı çalıştırdı: {}",Thread.currentThread().getName());
        List<FileEntity> batchList = new ArrayList<>();
        try {
            while (true) {
                FileEntity entity = fileQueue.poll(2, TimeUnit.SECONDS);

                if (entity == null) {
                    if (!batchList.isEmpty()) {
                        saveInBatch(batchList, isDeltaScan);
                    }
                    continue;
                }
                if (FileProducer.DB_POISON_PILL_NAME.equals(entity.getName())) {
                    log.info("Bitiş sinyali alındı kaydetme işlemi sona erecek");
                    break;
                }
                batchList.add(entity);
                if (batchList.size() >= BATCH_SIZE) {
                  saveInBatch(batchList,isDeltaScan);
                }
            }
            if (!batchList.isEmpty()) {
                saveInBatch(batchList,isDeltaScan);
            }
            log.info("Dosyalar veri tabanına kaydedildi!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("DB Worker kesildi.");
        }
    }

   private void saveInBatch(List<FileEntity> list,boolean isDeltaScan) {
       if (list.isEmpty()) return;
       try {
           if(!isDeltaScan){
               fileRepository.saveAll(list);
               fileRepository.flush();
           }
           else{
               String[] names = list.stream().map(FileEntity::getName).toArray(String[]::new);
               String[] paths = list.stream().map(FileEntity::getPath).toArray(String[]::new);
               Long[] sizes = list.stream().map(FileEntity::getSize).toArray(Long[]::new);
               Long[] dates = list.stream().map(FileEntity::getLastModified).toArray(Long[]::new);
               String[] exts = list.stream().map(FileEntity::getExtension).toArray(String[]::new);
               Boolean[] deleted = new Boolean[list.size()]; Arrays.fill(deleted, false);
               Boolean[] favorited=new Boolean[list.size()]; Arrays.fill(favorited, false);
               Boolean[] directory=new Boolean[list.size()]; Arrays.fill(directory, false);
               Long[] lastOpens = new Long[list.size()];
               Arrays.fill(lastOpens, 0L);
               fileRepository.upsertFilesBatch(names, paths, sizes, dates, exts, deleted,favorited,directory,lastOpens);
           }

       } finally {
           list.clear();
       }
   }

}
