package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.ApiFavoriteStatusService;
import com.berkaykomur.filesearchfrontend.service.ApiFileGeneralService;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ContextMenuUtil {

    private static final ApiFavoriteStatusService favoriteStatusService=new ApiFavoriteStatusService();
    static final ApiFileGeneralService  fileGeneralService=new ApiFileGeneralService();


    public static ContextMenu createContextMenu(FileDto file, Runnable onRefreshCallback) {
        ContextMenu menu = new ContextMenu();

        MenuItem openItem = new MenuItem("Aç");
        openItem.setOnAction(e -> {
            openFile(file);
            fileGeneralService.setLastOpen(file.getPath(), System.currentTimeMillis());
        });

        MenuItem copyPathItem = new MenuItem("Yolu Kopyala");
        copyPathItem.setOnAction(e -> copyToClipboard(file.getPath()));

        MenuItem favItem = new MenuItem(file.isFavorite() ? "Favoriden Çıkar" : "Favorilere Ekle");
        favItem.setOnAction(e -> {
            boolean nextStatus = !file.isFavorite();


            favoriteStatusService.updateFavoriteStatus(file.getPath(), nextStatus)
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            file.setFavorite(nextStatus); // DTO'yu güncelle
                            if (onRefreshCallback != null) onRefreshCallback.run();

                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        });

        menu.getItems().addAll(openItem, copyPathItem, favItem);
        return menu;
    }

    public static void openFile(FileDto file) {
        try {
            Desktop.getDesktop().open(new File(file.getPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
