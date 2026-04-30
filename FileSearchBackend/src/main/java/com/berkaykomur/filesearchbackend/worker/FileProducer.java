package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileProducer {
    private final BlockingQueue<FileEntity> fileQueue;
    private final BlockingQueue<Path> indexQueue;

    private static final Path POISON_PATH = Path.of("__POSION__");
    public static final String POISON_PILL_NAME = "___STOP_PROCESS___"; // Name="POISON" gibi setlenebilir
    private static final Set<String> TEXT_EXTENSIONS = Set.of("txt", "java", "log", "md");

    public void scanAndSaveAllFiles(Path root,int dbWorkerCount, int indexWorkerCount) throws IOException, InterruptedException {
        log.info("Dosyaları tarama ve veri tabanına yazma işlemleri başlıyor");
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!attrs.isRegularFile()) {
                    return FileVisitResult.CONTINUE;
                }
                try {
                    FileEntity fileEntity = FileMapper.fromPathToFile(file,attrs);
                    if(fileEntity!=null){
                        fileQueue.put(fileEntity);
                    }
                    if (TEXT_EXTENSIONS.contains(FileUtil.getExtension(file))) {
                        indexQueue.put(file);
                    }
                } catch (Exception e) {
                    log.error("Beklenemdik bir hata meydana geldi: {}",e);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.warn("Dosya okunamadığı için atlandı: {}",file);
                return FileVisitResult.CONTINUE;
            }
        });
        log.info("Tarama işlemi tamamlandı bitiş sinyalleri için zehirli haplar gönderiliyor..");

        for (int i = 0; i < indexWorkerCount; i++) {
            indexQueue.put(POISON_PATH);
        }

        for (int i = 0; i < dbWorkerCount; i++) {
            FileEntity poisonPill = new FileEntity();
            poisonPill.setName(POISON_PILL_NAME);
            fileQueue.put(poisonPill);
        }
    }



}
