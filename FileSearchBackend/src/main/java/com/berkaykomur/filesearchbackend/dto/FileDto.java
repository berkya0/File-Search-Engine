package com.berkaykomur.filesearchbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private String name;
    private String path;
    private String extension;
    private long size;
    private long lastModified;


}
