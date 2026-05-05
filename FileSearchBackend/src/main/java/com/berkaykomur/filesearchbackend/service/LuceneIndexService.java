package com.berkaykomur.filesearchbackend.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
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
    @Getter
    private Directory directory;
    private StandardAnalyzer analyzer;
    private IndexWriter indexWriter;

    //bu yere sonradan tekrar bak
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
        log.info("Lucene directory hazırlandı {}",indexPath);

    }

    public void buildIndex(List<String> filePaths) {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(directory, config)) {
            log.info("İndeksleme işlemi başlatıldı Toplam {} dosya işlenecek.", filePaths.size());

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

                    writer.updateDocument(new Term("path", filePath), document);
                } catch (IOException e) {
                    log.warn("Dosya okunurken hata: {} - {}", filePath, e.getMessage());
                }
            }
            writer.commit();
            log.info("Tarama bitti, indeksler kaydedildi.");
        }
        catch (IOException e) {
            log.error("İndeksleme sırasında hata!", e);
        }
    }
}
