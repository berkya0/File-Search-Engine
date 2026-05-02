package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.dto.SearchRequest;
import com.berkaykomur.filesearchbackend.service.FileSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class FileSearchController {

    private final FileSearchService fileSearchService;

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
}
