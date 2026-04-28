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
    private Long id;
    private String name;
    private String path;
    private long size;
    private long lastModified;


}
