package com.berkaykomur.filesearchfrontend.service;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ApiScanService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/scan";

    public CompletableFuture<HttpResponse<String>> quickStart() {
        log.info("Hızlı başlangıç için backende istek gönderildi");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quick"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> fullScan(String rootPath) {
        log.info("Tarama için backende istek gönderildi");

        String encodedPath = URLEncoder.encode(rootPath, StandardCharsets.UTF_8);
        String url = BASE_URL + "/start?rootPath="+encodedPath;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public boolean checkOnboardingStatus() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/scan/is-onboarded"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return Boolean.parseBoolean(response.body());
            }
        } catch (Exception e) {
            log.info("Backend kontrolü sırasında hata:{} ", e.getMessage());
        }
        return false;
    }

    public CompletableFuture<HttpResponse<String>> startDirectoryScan(String rootPath,boolean includeSubFolders) {
        log.info("Seçilen klasörde tarama isteği isteği gönderiliyor");

        String encodedPath = URLEncoder.encode(rootPath, StandardCharsets.UTF_8);
        String url=BASE_URL+"/directory?rootPath="+encodedPath+ "&includeSubFolders=" + includeSubFolders;
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
