package com.berkaykomur.filesearchbackend.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class SearchRequest {
    private String fileName;
    private Set<String> extensions;
    private int page;

}
