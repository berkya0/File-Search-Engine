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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        // Önce tüm dökümanları hazırla (IO işlemi, lock yok)
        List<Document> documents = new ArrayList<>();
        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) continue;
            try {
                // Dosyayı string olarak oku
                String content = Files.readString(path, StandardCharsets.UTF_8);
                Document document = new Document();
                document.add(new StringField("path", filePath, Field.Store.YES));
                document.add(new TextField("content", content, Field.Store.NO));
                documents.add(document);
            } catch (IOException e) {
                log.warn("Dosya okunurken hata: {} - {}", filePath, e.getMessage());
            }
        }

        // Sonra hepsini tek seferde yaz (writer'a az dokunuyoruz)
        for (Document document : documents) {
            try {
                writer.updateDocument(
                        new Term("path", document.get("path")), document);
            } catch (IOException e) {
                log.warn("Index yazma hatası: {}", e.getMessage());
            }
        }

    }
    public void completeIndexing() {
        try {
            writer.commit();
            searcherManager.maybeRefresh();
            log.info("İndeksler tek seferde kaydedildi ve tazelendi.");
        } catch (IOException e) {
            log.error("Final commit hatası!", e);
        }
    }

    
    ///HATALIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

}
