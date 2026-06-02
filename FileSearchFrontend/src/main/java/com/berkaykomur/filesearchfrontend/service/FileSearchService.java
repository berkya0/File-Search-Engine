package com.berkaykomur.filesearchfrontend.service;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j

public class FileSearchService {
    private final ApiSearchService apiService = new ApiSearchService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Getter
    private final ObservableList<FileDto> masterData = FXCollections.observableArrayList();
    @Getter
    private long totalElements = 0;

    private int currentPage = 0;
    private boolean isLoading = false;
    private String lastQuery = "";
    private boolean searchInContentMode = false;
    private CompletableFuture<String> activeFuture;

    public void setSearchMode(boolean inContent) {
        this.searchInContentMode = inContent;
    }

    public void startNewSearch(String query, Set<String> extensions) throws JsonProcessingException {
        this.lastQuery = query;
        this.currentPage = 0;
        this.masterData.clear();
        loadPage(query,extensions, currentPage);
    }

    public void loadNextPage( Set<String> extensions) throws JsonProcessingException {
        if (isLoading) return;

        this.currentPage++;
        log.info("Sonraki sayfa yükleniyor: {}", currentPage);
        loadPage(lastQuery,extensions, currentPage);
    }

    private void loadPage(String query, Set<String> extensions, int page) throws JsonProcessingException {

        if (activeFuture != null && !activeFuture.isDone()) {
            activeFuture.cancel(true);
        }

        isLoading = true;

        if (searchInContentMode) {
            activeFuture = apiService.searchInContent(query, page);
        } else {
            activeFuture = apiService.searchFiles(query, extensions, page);
        }

        activeFuture.thenAccept(jsonResponse -> {
            if (Thread.currentThread().isInterrupted()) return;

            try {
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode contentNode = rootNode.get("content");

                if (contentNode != null && contentNode.isArray()) {
                    List<FileDto> files = objectMapper.readerForListOf(FileDto.class).readValue(contentNode);
                    long total = rootNode.path("page").path("totalElements").asLong(0);

                    Platform.runLater(() -> {
                        // Sayfa 0 ise temizle, değilse üstüne ekle (Infinite scroll mantığı)
                        if (page == 0) masterData.clear();
                        masterData.addAll(files);
                        totalElements = total;
                        isLoading = false;
                        log.info("Sayfa {} yüklendi, toplam {}.", page, files.size());
                    });
                } else {
                    isLoading = false;
                }
            } catch (Exception e) {
                log.error("JSON işleme hatası: {}", e.getMessage());
                isLoading = false;
            }
        }).exceptionally(ex -> {
           
            if (!(ex instanceof java.util.concurrent.CancellationException)) {
                log.error("API Hatası: {}", ex.getMessage());
            }
            isLoading = false;
            return null;
        });
    }

    public ObservableList<FileDto> getMasterData() {
        return masterData;
    }

}
