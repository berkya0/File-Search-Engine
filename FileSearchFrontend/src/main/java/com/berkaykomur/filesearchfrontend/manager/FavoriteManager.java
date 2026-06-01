package com.berkaykomur.filesearchfrontend.manager;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.ApiFavoriteStatusService;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FavoriteManager {
    private final ApiFavoriteStatusService favoriteApiService=new ApiFavoriteStatusService();

    public void toggleFavorite(FileDto file,Runnable onSuccess) {
        boolean newStatus = !file.isFavorite();

        favoriteApiService.updateFavoriteStatus(file.getPath(), newStatus)
                .thenAccept(updatedFile -> {
                    Platform.runLater(() -> {
                        file.setFavorite(updatedFile.isFavorite());
                        log.info("Favori durumu güncellendi: {}", updatedFile.getPath());
                        if(onSuccess!=null){
                            onSuccess.run();
                        }
                    });
                })
                .exceptionally(ex -> {
                    log.error("Favori güncellenemedi: {}", ex.getMessage());
                    return null;
                });
    }

}
