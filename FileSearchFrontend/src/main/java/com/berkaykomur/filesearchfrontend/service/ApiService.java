package com.berkaykomur.filesearchfrontend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/search";

    public CompletableFuture<String> searchFiles(String fileName,int page){
        String url=String.format("%s?fileName=%s&page=%s&pageSize=25",BASE_URL,fileName,page);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.replace(" ","%20")))
                .GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }
}
