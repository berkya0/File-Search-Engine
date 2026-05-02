package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.dto.SearchRequest;
import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileSearchService {

    private final FileRepository fileRepository;

    public Page<FileDto> searchFiles(SearchRequest searchRequest) {
        log.info("Dosya arama işlemi başlatıldı: [Kelime: {}, Kategoriler: {}]",
                searchRequest.getFileName(), searchRequest.getExtensions());

        Pageable pageable = PageRequest.of(searchRequest.getPage(),25,
                Sort.by("name").ascending());

        long start = System.currentTimeMillis();
        Page<FileDto> results=fileRepository.searchFiles
                        (searchRequest.getFileName(),searchRequest.getExtensions(),pageable)
                .map(FileMapper::toDTO);
        long executionTime = System.currentTimeMillis() - start;
        log.info("Arama başarılı. Sayfa: {}, Toplam Sonuç: {}, Süre: {}ms",
                searchRequest.getPage(), results.getTotalElements(), executionTime);

        return results;
    }
}
