package com.berkaykomur.filesearchfrontend.service;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j

public class FileSearchService {
    private final ApiService apiService = new ApiService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObservableList<FileDto> masterData = FXCollections.observableArrayList();
    private int currentPage = 0;

    public void fetchData(String query) {
        log.info("Arama yapmak için sorgu hazırlanıyor...");
        masterData.clear();
        currentPage = 0;
        apiService.searchFiles(query, currentPage).thenAccept(jsonResponse -> {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode contentNode = rootNode.get("content");

                if (contentNode != null && contentNode.isArray()) {
                    List<FileDto> files = objectMapper.readerForListOf(FileDto.class).readValue(contentNode);

                    Platform.runLater(() -> {
                        masterData.addAll(files);
                        log.info("{} adet dosya eklendi.", files.size());
                    });
                }
            } catch (Exception e) {
                log.error("JSON Ayrıştırma Hatası: {}", e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            log.error("API İsteği Hatası: {}", ex.getMessage());
            return null;
        });
    }
}
