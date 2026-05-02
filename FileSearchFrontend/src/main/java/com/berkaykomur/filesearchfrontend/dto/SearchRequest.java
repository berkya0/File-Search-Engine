package com.berkaykomur.filesearchfrontend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SearchRequest {
    private String fileName;
    private Set<String> extensions;
    private int page;

}
