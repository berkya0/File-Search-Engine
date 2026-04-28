package com.berkaykomur.filesearchbackend.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class LuceneIndexService {

    private String indexPath=System.getProperty("user.home")+ File.separator+"fileSearchIndex";
    private Directory directory;
    private StandardAnalyzer analyzer;
    private IndexWriter indexWriter;

    public void updateIndexPath(String indexPath) throws IOException {
        this.indexPath=indexPath;
        init();
        log.info("index dosyası değiştirildi yeni dosya yolu:{}",indexPath);
    }

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(indexPath);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
        this.directory = FSDirectory.open(path);
        this.analyzer = new StandardAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        this.indexWriter = new IndexWriter(directory, config);
        log.info("Lucene IndexWriter başarıyla başlatıldı ve kilit alındı.");
    }

    public void buildIndex(List<String> filePaths) {
        log.info("{} adet dosya indeksleniyor...", filePaths.size());

        // Karakter hatalarını önlemek için decoder
        CharsetDecoder decoder = Charset.forName("Windows-1254").newDecoder()
                .onMalformedInput(CodingErrorAction.IGNORE)
                .onUnmappableCharacter(CodingErrorAction.IGNORE);

        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) continue;

            Document document = new Document();
            document.add(new StringField("path", filePath, Field.Store.YES));

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(path), decoder))) {

                document.add(new TextField("content", reader));

                // Birden fazla thread aynı anda buraya döküman gönderebilir.
                indexWriter.updateDocument(new Term("path", filePath), document);

            } catch (IOException e) {
                log.warn("Dosya okunurken hata oluştu (atlandı): {} - Hata: {}", filePath, e.getMessage());
            }
        }
        try {
            indexWriter.commit();
        } catch (IOException e) {
            log.error("Lucene commit hatası!", e);
        }
    }
    @PreDestroy
    public void close() throws IOException {
        if (indexWriter != null && indexWriter.isOpen()) {
            indexWriter.close();
            log.info("Lucene IndexWriter güvenli bir şekilde kapatıldı.");
        }
    }


}
