package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.service.LuceneIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexWorker {

    private final BlockingQueue<Path> indexQueue;
    private final LuceneIndexService luceneIndexService;
    private final Path POSION=Path.of("__POSION__");
    private final int BATCH_SIZE=1000;

    @Async("taskExecutor")
    public void runIndex() {
        List<String> indexList=new ArrayList<>();
        log.info("Yeni bir thread çalışmaya başladı: {}", Thread.currentThread().getName());

        while (true) {
            try {
                Path path=indexQueue.take();
                if(path.getFileName().toString().equals(POSION.toString())) {
                    indexQueue.put(path);
                    log.info("Dosya sonuna gelindi. {}",path.getFileName());
                    break;
                }
                if(!Files.isReadable(path) || !Files.isRegularFile(path)) {
                    log.error("Okunamıyor atlandı: {}", path);
                    continue;
                }
                if(indexList.size()>=BATCH_SIZE) {
                    luceneIndexService.buildIndex(indexList);
                    indexList.clear();
                }
                indexList.add(path.toString());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Index thread yarıda kesildi {}",e.getMessage());
            } catch (Exception e) {
                log.error("Bir hata oluştu ",e);
            }
        }
        if(!indexList.isEmpty()) {
            try {
                luceneIndexService.buildIndex(indexList);
            } catch (Exception e) {
                log.error("Batch listesi indekslenirken bir hata oluştu ",e);
            }
        }

    }
}
