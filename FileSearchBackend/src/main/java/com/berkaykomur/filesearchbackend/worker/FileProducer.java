package com.berkaykomur.filesearchbackend.worker;

import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import lombok.Getter;
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
@Getter
public class FileProducer {
    private final BlockingQueue<FileEntity> fileQueue;
    private final BlockingQueue<Path> indexQueue;

    public static final Path IX_POISON = Path.of("__POSION__");
    public static final String DB_POISON_PILL_NAME = "___STOP_PROCESS___";
    public static final Set<String> TEXT_EXTENSIONS = Set.of("txt", "java", "log", "md");

    public void scanAndSaveAllFiles(Path root) throws IOException, InterruptedException {
        log.info("Dosyaları tarama ve veri tabanına yazma işlemleri başlıyor");
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                Path fileNamePath = dir.getFileName();
                String folderName = (fileNamePath != null) ? fileNamePath.toString().toLowerCase() : dir.toString().toLowerCase();
                if (folderName.equals("node_modules")
                        || folderName.equals(".git")
                        || folderName.equals("target")
                        || folderName.equals("build")
                        || folderName.equals("appdata")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                try {
                    FileEntity folderEntity = FileMapper.fromPathToFile(dir, attrs);
                    if (folderEntity != null) {
                        fileQueue.put(folderEntity);
                    }
                } catch (Exception e) {
                    log.error("Klasör kuyruğa eklenirken hata: {}", e.getMessage());
                }

                return FileVisitResult.CONTINUE;
            }
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
                    String extension=FileUtil.getExtension(file.getFileName().toString());
                    if (extension!=null&& TEXT_EXTENSIONS.contains(FileUtil.getExtension(file.toString()))) {
                        indexQueue.put(file);
                    }
                } catch (Exception e) {
                    log.error("Beklenemdik bir hata meydana geldi: {}",e.getMessage());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.warn("Dosya okunamadığı için atlandı: {}",file);
                return FileVisitResult.CONTINUE;
            }
        });

    }
    public void endThreads(){
        log.info("Tarama işlemi tamamlandı bitiş sinyalleri için zehirli haplar gönderiliyor..");
        try{
            for (int i = 0; i < FileCoordinator.DB_WORKER_THREAD_COUNT; i++) {
                FileEntity poisonPill = new FileEntity();
                poisonPill.setName(DB_POISON_PILL_NAME);
                fileQueue.put(poisonPill);
            }
            for (int i = 0; i < FileCoordinator.INDEX_WORKER_THREAD_COUNT; i++) {
                indexQueue.put(IX_POISON);
            }
        } catch (InterruptedException e) {
            log.error("Zehirli haplar gönderilirken bir hata oluştu: {}",e.getMessage());
        }
        log.info("Zehirli hapların hepsi gönderildi.");

    }
}