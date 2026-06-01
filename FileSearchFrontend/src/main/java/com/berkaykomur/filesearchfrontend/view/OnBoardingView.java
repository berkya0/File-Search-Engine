package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.service.ApiScanService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class OnBoardingView {
    @FXML private Button btnQuickStart;
    @FXML private Button btnScanAll;
    @FXML private ProgressIndicator progressQuick;
    @FXML private ProgressIndicator progressFull;

    private final ApiScanService apiScanService = new ApiScanService();

    @FXML
    public void initialize() {
        btnQuickStart.setOnAction(event -> {
            log.info("Hızlı başlangıç modu seçildi");

            btnQuickStart.setVisible(false);
            btnQuickStart.setManaged(false);
            progressQuick.setVisible(true);
            progressQuick.setManaged(true);

            apiScanService.quickStart()
                    .thenAccept(response -> {
                        if (response.statusCode() == 200 || response.statusCode() == 202) {
                            Platform.runLater(this::openMainScreen);
                        } else {
                            log.warn("Backend hata döndürdü: {}", response.statusCode());
                            Platform.runLater(this::resetQuickStartUi);
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("Hızlı başlatma API hatası: ", ex);
                        Platform.runLater(this::resetQuickStartUi);
                        return null;
                    });
        });

        btnScanAll.setOnAction(event -> {
            log.info("Tüm dosyaları tarama modu seçildi");

            btnScanAll.setVisible(false);
            btnScanAll.setManaged(false);
            progressFull.setVisible(true);
            progressFull.setManaged(true);

            apiScanService.fullScan("C://")
                    .thenAccept(response -> {
                        if (response.statusCode() == 200 || response.statusCode() == 202) {
                            Platform.runLater(this::openMainScreen);
                        } else {
                            log.warn("Backend hata döndürdü: {}", response.statusCode());
                            Platform.runLater(this::resetScanAllUi);
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("Tümünü tara API hatası: ", ex);
                        Platform.runLater(this::resetScanAllUi);
                        return null;
                    });
        });
    }

    private void resetQuickStartUi() {
        progressQuick.setVisible(false);
        progressQuick.setManaged(false);
        btnQuickStart.setVisible(true);
        btnQuickStart.setManaged(true);
    }

    private void resetScanAllUi() {
        progressFull.setVisible(false);
        progressFull.setManaged(false);
        btnScanAll.setVisible(true);
        btnScanAll.setManaged(true);
    }

    private void openMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/berkaykomur/filesearchfrontend/file-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (btnQuickStart.getScene() != null ? btnQuickStart.getScene().getWindow() : btnScanAll.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.error("Ana arama ekranı (file-view.fxml) yüklenirken hata oluştu: ", e);
        }
    }
}