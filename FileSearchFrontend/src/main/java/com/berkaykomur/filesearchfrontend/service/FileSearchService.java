package com.berkaykomur.filesearchfrontend.service;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j

public class FileSearchService {
    private final ApiService apiService = new ApiService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Getter
    private final ObservableList<FileDto> masterData = FXCollections.observableArrayList();

    private int currentPage = 0;
    private boolean isLoading = false;
    private String lastQuery = "";

    public void startNewSearch(String query) {
        this.lastQuery = query;
        this.currentPage = 0;
        this.masterData.clear();
        loadPage(query, currentPage);
    }

    public void loadNextPage() {
        if (isLoading) return;

        this.currentPage++;
        log.info("Sonraki sayfa yükleniyor: {}", currentPage);
        loadPage(lastQuery, currentPage);
    }

    private void loadPage(String query, int page) {
        isLoading = true;
        apiService.searchFiles(query, page).thenAccept(jsonResponse -> {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode contentNode = rootNode.get("content");

                if (contentNode != null && contentNode.isArray()) {
                    List<FileDto> files = objectMapper.readerForListOf(FileDto.class).readValue(contentNode);

                    Platform.runLater(() -> {
                        masterData.addAll(files);
                        log.info("Sayfa {} yüklendi, {} yeni dosya eklendi.", page, files.size());
                        isLoading = false;
                    });
                } else {
                    isLoading = false;
                }
            } catch (Exception e) {
                log.error("Hata: {}", e.getMessage());
                isLoading = false;
            }
        }).exceptionally(ex -> {
            log.error("API Hatası: {}", ex.getMessage());
            isLoading = false;
            return null;
        });
    }



}
