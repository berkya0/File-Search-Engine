package com.berkaykomur.filesearchbackend.mapper;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.model.FileEntity;
import com.berkaykomur.filesearchbackend.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class FileMapper {
    public static FileDto toDTO(FileEntity file) {
        if(file==null){return null;}
        FileDto fileDto = new FileDto();
        fileDto.setSize(file.getSize());
        fileDto.setName(file.getName());
        fileDto.setPath(file.getPath());
        fileDto.setLastModified(file.getLastModified());
        fileDto.setFavorite(file.isFavorite());
        fileDto.setExtension(FileUtil.getExtension(file.getName()));
        fileDto.setDirectory(file.isDirectory());
        fileDto.setLastOpen(file.getLastOpen());
        return fileDto;
    }

    public static FileEntity fromPathToFile(Path path, BasicFileAttributes attrs) {
        try {
            if (path == null) return null;

            FileEntity fileEntity = new FileEntity();
            if (attrs.isDirectory()) {
                fileEntity.setDirectory(true);
            }
            fileEntity.setPath(path.toString());
            fileEntity.setSize(attrs.size());

            Path fileNamePath = path.getFileName();
            if (fileNamePath != null) {
                fileEntity.setName(fileNamePath.toString().toLowerCase());
                fileEntity.setExtension(FileUtil.getExtension(fileNamePath.toString()));
            } else {
                fileEntity.setName(path.toString().toLowerCase());
                fileEntity.setExtension("");
            }

            fileEntity.setLastModified(attrs.lastModifiedTime().toMillis());
            fileEntity.setFavorite(false);
            fileEntity.setLastOpen(0);
            return fileEntity;
        } catch (Exception e) {
            log.error("Dosyayı entitye çevirirken hata oluştu! Path: {}", path, e);
            return null;
        }
    }


}
