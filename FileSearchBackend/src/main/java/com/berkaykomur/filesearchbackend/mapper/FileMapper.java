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
        return fileDto;
    }
    public static FileEntity toFile(FileDto fileDto) {
        if(fileDto==null){return null;}
        FileEntity file=new FileEntity();
        file.setName(fileDto.getName());
        file.setSize(fileDto.getSize());
        file.setPath(fileDto.getPath());
        file.setLastModified(fileDto.getLastModified());
        return file;
    }

    public static FileEntity fromPathToFile(Path path, BasicFileAttributes attrs) {
        try {
            if (path == null) return null;

            FileEntity fileEntity = new FileEntity();
            fileEntity.setPath(path.toString());
            fileEntity.setSize(attrs.size());
            Path fileNamePath = path.getFileName();
            if (fileNamePath != null) {
                fileEntity.setName(fileNamePath.toString());
            } else {
                fileEntity.setName(path.toString());
            }

            fileEntity.setExtension(FileUtil.getExtension(path));
            fileEntity.setLastModified(attrs.lastModifiedTime().toMillis());

            return fileEntity;
        } catch (Exception e) {
            log.error("Dosyayı entitye çevirirken hata oluştu! Path: {}", path, e);
            return null;
        }
    }

}
