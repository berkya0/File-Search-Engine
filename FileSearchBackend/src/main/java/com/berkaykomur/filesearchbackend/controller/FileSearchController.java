package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.dto.SearchRequest;
import com.berkaykomur.filesearchbackend.service.FileSearchService;
import com.berkaykomur.filesearchbackend.service.LuceneSearchService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ser.jdk.JDKKeySerializers;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class FileSearchController {

    private final FileSearchService fileSearchService;
    private final LuceneSearchService luceneSearchService;

    @PostMapping
    public ResponseEntity<Page<FileDto>> fileSearch(@RequestBody SearchRequest request) {
        log.info("Arama isteği alındı -> Filtre: [Ad: '{}', Uzantılar: {}, Sayfa: {}]",
                request.getFileName(),
                request.getExtensions(),
                request.getPage());

        Page<FileDto> result = fileSearchService.searchFiles(request);
        log.info("Arama isteği başarıyla yanıtlandı. Toplam bulunan kayıt: {}", result.getTotalElements());

        return ResponseEntity.ok(result);

    }
    @GetMapping("/lucene")
    public ResponseEntity<Page<FileDto>> luceneSearch(@RequestParam String query,
                                                      @RequestParam(defaultValue = "0") int page) {
        log.info("İçerikte arama isteği atıldı query: {} page: {}", query,page);
        Page<FileDto> files=luceneSearchService.luceneSearch(query,page);
        log.info("içerikte arama işlemi başarıyla yanıtlandı.");
        return ResponseEntity.ok(files);
    }
}
