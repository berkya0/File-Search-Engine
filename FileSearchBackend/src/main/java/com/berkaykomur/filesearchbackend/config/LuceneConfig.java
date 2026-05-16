package com.berkaykomur.filesearchbackend.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class LuceneConfig {
    private final String indexPath=System.getProperty("user.home")+ File.separator+"fileSearchIndex";

    @Bean
    public Directory luceneDirectory() throws IOException {
        return FSDirectory.open(new File(indexPath).toPath());
    }
    @Bean
    public Analyzer analyzer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new StandardTokenizer();
                TokenStream filter = new LowerCaseFilter(tokenizer);
                return new TokenStreamComponents(tokenizer, filter);
            }
        };
    }
    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, config);
    }

    @Bean
    public SearcherManager searcherManager(IndexWriter writer) throws IOException {
        return new SearcherManager(writer, true, false, null);
    }
}
