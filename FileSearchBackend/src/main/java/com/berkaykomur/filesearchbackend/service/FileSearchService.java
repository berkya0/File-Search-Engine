package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.dto.FileDto;
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

    public Page<FileDto> searchFiles(String searchText,int page,int pageSize){
        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("name").ascending());
        return fileRepository.findByNameContainingIgnoreCase(searchText, pageable)
                .map(entity -> new FileDto(
                        entity.getId(),
                        entity.getName(),
                        entity.getPath(),
                        entity.getSize(),
                        entity.getLastModified()
                ));
    }
}
