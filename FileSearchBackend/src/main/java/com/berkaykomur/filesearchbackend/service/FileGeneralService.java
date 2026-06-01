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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileGeneralService {

    private final FileRepository fileRepository;

    @Transactional
    public FileDto setLastOpen(String path,long lastOpenTime)
    {
        FileEntity fileEntity =fileRepository.findByPath(path).
                orElseThrow(()-> new FileOrDirectoryNotFound("Son açılma tarihi güncelenecek dosya/klasör bulunamadı:"+path));
        fileEntity.setLastOpen(lastOpenTime);
        fileRepository.save(fileEntity);
        log.info("Dosya/Klasörün son açılma tarihi güncellendi");
        return FileMapper.toDTO(fileEntity);
    }
    public List<FileDto> getTop10Files(){
        List<FileEntity> fileEntities=fileRepository.findTop10RecentFiles();
        log.info("Son kullandığın dosyalar listeleniyorr");
        return fileEntities.stream().map(FileMapper::toDTO).toList();
    }
}
