package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.service.FileSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class FileSearchController {

    private final FileSearchService fileSearchService;

    @GetMapping
    public ResponseEntity<Page<FileDto>> fileSearch(@RequestParam String fileName,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "25") int pageSize) {

        return ResponseEntity.ok(fileSearchService.searchFiles(fileName,page,pageSize));

    }
}
