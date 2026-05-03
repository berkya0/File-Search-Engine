package com.berkaykomur.filesearchfrontend.service;

import com.berkaykomur.filesearchfrontend.dto.SearchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/search";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<String> searchFiles(String fileName, Set<String>extensions,int page) throws JsonProcessingException {

        log.info("Arama isteği hazırlanıyor... Filtre: [Kelimeler: '{}', Uzantılar: {}]",
                fileName, extensions);
        SearchRequest requestBody = new SearchRequest();
        requestBody.setFileName(fileName);
        requestBody.setExtensions(extensions);
        requestBody.setPage(page);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        long startTime = System.currentTimeMillis();

        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    long duration = System.currentTimeMillis() - startTime;

                    if (response.statusCode() == 200) {
                        log.info("Backend yanıtı başarılı (200 OK). Süre: {}ms", duration);
                    } else {
                        log.warn("Backend hata döndürdü! Kod: {}, Süre: {}ms", response.statusCode(), duration);
                    }
                    return response.body();
                })
                .exceptionally(ex -> {

                    log.error("Sunucuya bağlanırken kritik hata oluştu: {}", ex.getMessage());
                    return null;
                });
    }

    public CompletableFuture<String> searchInContent(String query, int page) {
        log.info("İçerikte arama isteği hazırlanıyor... Query: {}, Sayfa: {}", query, page);

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = BASE_URL + "/lucene?query=" + encodedQuery + "&page=" + page;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        log.warn("Lucene araması hata döndürdü! Kod: {}", response.statusCode());
                        return null;
                    }
                })
                .exceptionally(ex -> {
                    log.error("Lucene araması sırasında hata: {}", ex.getMessage());
                    return null;
                });
    }
}
