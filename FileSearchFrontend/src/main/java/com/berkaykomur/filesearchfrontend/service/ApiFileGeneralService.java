package com.berkaykomur.filesearchfrontend.service;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiFileGeneralService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/file/general";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<FileDto> setLastOpen(String path,long lastOpen) {
        String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
        String url = BASE_URL + "/set-lastOpen?path=" + encodedPath + "&lastOpen=" + lastOpen;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(""))
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), FileDto.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("JSON dönüştürme hatası", e);
                        }
                    } else {
                        throw new RuntimeException("Sunucu hatası: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<List<FileDto>> getTop10Files() {

        String url = BASE_URL + "/get/files";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, FileDto.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("JSON dönüştürme hatası", e);
                        }
                    } else {
                        throw new RuntimeException("Sunucu hatası: " + response.statusCode());
                    }
                });
    }
}
