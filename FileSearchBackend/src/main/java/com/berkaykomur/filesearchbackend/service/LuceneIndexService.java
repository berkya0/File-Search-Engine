package com.berkaykomur.filesearchbackend.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LuceneIndexService {

    @Getter
    private final Directory directory;
    private final IndexWriter writer;
    private final SearcherManager searcherManager;

    public void buildIndex(List<String> filePaths) {
        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) continue;
            try {
                if (Files.size(path) > 10 * 1024 * 1024) {
                    log.warn("Dosya çok büyük, atlandı: {}", filePath);
                    continue;
                }
                String content = Files.readString(path, StandardCharsets.UTF_8);
                Document document = new Document();
                document.add(new StringField("path", filePath, Field.Store.YES));
                document.add(new TextField("content", content, Field.Store.YES));
                writer.updateDocument(new Term("path", filePath), document);

            } catch (IOException e) {
                log.warn("Dosya okunurken veya indekslenirken hata: {} - {}", filePath, e.getMessage());
            }
        }
    }
    public void deleteFromIndex(String filePath) {
        try {
            writer.deleteDocuments(new Term("path", filePath));
            log.debug("Index'ten silindi: {}", filePath);
        } catch (IOException e) {
            log.error("Index'ten silme hatası – {}: {}", filePath, e.getMessage());
        }
    }
    public void completeIndexing() {
        try {
            writer.commit();
            searcherManager.maybeRefresh();
          //  log.info("İndeksler tek seferde kaydedildi ve tazelendi.");
        } catch (IOException e) {
            log.error("Final commit hatası!", e);
        }
    }

}
