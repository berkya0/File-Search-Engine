package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.exception.FileOrDirectoryNotFound;
import com.berkaykomur.filesearchbackend.mapper.FileMapper;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateFavoriteStatusService {

    private final FileRepository fileRepository;

    @Transactional
    public FileDto updateFavoriteStatus(String path, boolean status) {
        FileEntity favStatus = fileRepository.findByPath(path)
                .orElseThrow(() -> new FileOrDirectoryNotFound("Dosya yolu hatalı, kaynak bulunamadı." + path));
        favStatus.setFavorite(status);
        fileRepository.save(favStatus);
        FileDto fileDto= FileMapper.toDTO(favStatus);
        log.info("Seçilen dosyanın {} favori durumu güncellendi. {}", path, status);
        return fileDto;
    }

    public Set<FileDto> favoriteDirectories(){
        Set<FileEntity> favFolders=fileRepository.findByIsDirectoryTrueAndIsFavoriteTrue();
        return favFolders.stream()
                .map(FileMapper::toDTO)
                .collect(Collectors.toSet());
    }

}
