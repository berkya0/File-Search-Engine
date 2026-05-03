package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuceneSearchService {
    private final LuceneIndexService indexService;
    private final FileRepository fileRepository;

    public Page<FileDto> luceneSearch(String query, int page) {

        long startTime = System.currentTimeMillis();
        List<String> paths = luceneResults(query, 500);
        int pageSize = 25;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, paths.size());

        if (start >= paths.size()) {
            log.info("Lucene search hiç sonuç bulunamadı. Query: {}", query);
            return Page.empty();
        }
        List<String> pagePaths = paths.subList(start, end);
        List<FileEntity> entities =
                fileRepository.findByPathIn(pagePaths);

        Map<String, FileEntity> map =
                entities.stream()
                        .collect(Collectors.toMap(
                                FileEntity::getPath,
                                Function.identity()
                        ));

        List<FileDto> ordered = pagePaths.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(FileMapper::toDTO)
                .toList();
        log.info("Lucene araması tamamlandı. Toplam sonuç: {}, Toplam süre: {} ", ordered.size(),System.currentTimeMillis()-startTime);
        Pageable pageable = PageRequest.of(page, pageSize);
        return new PageImpl<>(ordered, pageable, paths.size());
    }

    private List<String> luceneResults(String query,int limit) {
        List<String> results = new ArrayList<>();
        try{
            Directory directory=indexService.getDirectory();
            DirectoryReader reader=DirectoryReader.open(directory);
            IndexSearcher searcher=new IndexSearcher(reader);

            QueryParser parser=new QueryParser("content",new StandardAnalyzer());
            Query luceneQuery=parser.parse(query);

            TopDocs topDocs=searcher.search(luceneQuery,limit);

            for(ScoreDoc scoreDoc:topDocs.scoreDocs){
                Document document=searcher.doc(scoreDoc.doc);
                results.add(document.get("path"));
            }
            reader.close();
        }
        catch (Exception e){
            log.error("Lucene search ile arama yaparken bir hata oluştu: {}",e.getMessage());
        }
        return results;
    }





}
