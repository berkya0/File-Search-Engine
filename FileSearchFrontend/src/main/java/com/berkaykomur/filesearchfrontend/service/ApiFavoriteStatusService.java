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
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ApiFavoriteStatusService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/file";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<FileDto> updateFavoriteStatus(String path, boolean status) {
        String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
        String url = BASE_URL + "/favorite?path=" + encodedPath + "&status=" + status;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
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

    public CompletableFuture<Set<FileDto>> getFavoriteFolders() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/favorite/directories"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    objectMapper.getTypeFactory().constructCollectionType(Set.class, FileDto.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Veri dönüştürme hatası", e);
                        }
                    } else {
                        throw new RuntimeException("Sunucu hatası: " + response.statusCode());
                    }
                });
    }


}
