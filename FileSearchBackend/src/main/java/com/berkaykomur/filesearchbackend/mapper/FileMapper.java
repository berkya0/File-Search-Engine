package com.berkaykomur.filesearchbackend.mapper;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.model.FileEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileMapper {
    public static FileDto toDTO(FileEntity file) {
        if(file==null){return null;}
        FileDto fileDto = new FileDto();
        fileDto.setId(file.getId());
        fileDto.setSize(file.getSize());
        fileDto.setName(file.getName());
        fileDto.setPath(file.getPath());
        fileDto.setLastModified(file.getLastModified());
        return fileDto;
    }
    public static FileEntity toFile(FileDto fileDto) {
        if(fileDto==null){return null;}
        FileEntity file=new FileEntity();
        file.setId(fileDto.getId());
        file.setName(fileDto.getName());
        file.setSize(fileDto.getSize());
        file.setPath(fileDto.getPath());
        file.setLastModified(fileDto.getLastModified());
        return file;
    }

    public static FileEntity fromPathToFile(Path path) {
        try{
            FileEntity fileEntity=new FileEntity();
            BasicFileAttributes attrs= Files.readAttributes(path, BasicFileAttributes.class);
            fileEntity.setPath(path.toString());
            fileEntity.setSize(attrs.size());
            fileEntity.setName(path.getFileName().toString());
            fileEntity.setLastModified(attrs.lastModifiedTime().toMillis());
            return fileEntity;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
