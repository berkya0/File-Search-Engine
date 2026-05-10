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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
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

    //bu yere sonradan tekrar bak
    public void updateIndexPath(String indexPath) throws IOException {
    }

    public void buildIndex(List<String> filePaths) {

        log.info("İndeksleme işlemi başlatıldı Toplam {} dosya işlenecek.", filePaths.size());

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE) // Hata yerine '?' koyar, çökmez
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) continue;

            Document document = new Document();
            document.add(new StringField("path", filePath, Field.Store.YES));

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(path), decoder))) {
                document.add(new TextField("content", reader));

                writer.updateDocument(new Term("path", filePath), document);
            } catch (IOException e) {
                log.warn("Dosya okunurken hata: {} - {}", filePath, e.getMessage());
            }
        }

    }
    public void completeIndexing() {
        try {
            writer.commit();
            searcherManager.maybeRefresh();
            log.info("Tüm tarama bitti: İndeksler tek seferde kaydedildi ve tazelendi.");
        } catch (IOException e) {
            log.error("Final commit hatası!", e);
        }
    }

}
