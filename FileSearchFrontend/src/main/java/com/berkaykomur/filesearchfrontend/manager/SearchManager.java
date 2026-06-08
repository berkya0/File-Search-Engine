package com.berkaykomur.filesearchfrontend.manager;

import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class SearchManager {
    private final FileSearchService fileSearchService;
    private final PauseTransition searchDebounce;

    public SearchManager(FileSearchService service) {
        this.fileSearchService = service;
        this.searchDebounce = new PauseTransition(Duration.millis(500));
    }

    public void triggerSearch(String searchTerm, Set<String> extensions) {
        searchDebounce.playFromStart();
        searchDebounce.setOnFinished(event -> {
            try {
                String term = searchTerm.trim();

                if (term.length() >= 3 || term.isEmpty()) {

                    fileSearchService.startNewSearch(term, extensions);
                }
            } catch (JsonProcessingException e) {
                log.error("Arama başlatılamadı: {}", e.getMessage());
            }
        });
    }

    public void updateSearchMode(boolean isContentSearch, String currentText, Set<String> extensions) {
        fileSearchService.setSearchMode(isContentSearch);
        try {
            fileSearchService.startNewSearch(currentText, extensions);
        } catch (JsonProcessingException e) {
            log.error("Mod değişimi hatası: {}", e.getMessage());
        }
    }


}
