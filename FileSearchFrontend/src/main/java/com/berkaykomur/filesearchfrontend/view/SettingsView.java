package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.service.ApiScanService;
import com.berkaykomur.filesearchfrontend.util.AlertUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SettingsView {

    @FXML
    private TextField directoryPathField;

    @FXML
    private Button browseButton;
    @FXML
    private Button scanButton;
    @FXML
    private CheckBox skipHiddenFilesCheck;

    private final ApiScanService apiScanService = new ApiScanService();

    @FXML
    public void initialize() {

        scanButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> directoryPathField.getText() == null || directoryPathField.getText().trim().length() < 3,
                        directoryPathField.textProperty()
                )
        );
    }
    @FXML
    private void handleBrowseDirectory() {
        log.info("Kullanıcı taramak istediği klasörü seçiyor..");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Taramak İstediğiniz Hedef Klasörü Seçin");

        String currentPath = directoryPathField.getText();
        if (currentPath != null && !currentPath.trim().isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        }
        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            directoryPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleStartScan() {
        String selectedPathStr = directoryPathField.getText();
        Path selectedPath = Paths.get(selectedPathStr);

        if (!Files.exists(selectedPath) || !Files.isDirectory(selectedPath)) {
            log.warn("Tarama başlatılamadı: Geçersiz veya var olmayan bir klasör yolu: {}", selectedPath);

            AlertUtil.showError(
                    "Geçersiz Klasör",
                    "Klasör Bulunamadı",
                    "Lütfen bilgisayarınızda var olan geçerli bir klasör yolu girin ya da 'Göz At' butonunu kullanın."
            );
            return;
        }

        boolean isDangerous = false;
        String warningReason = "";

        if (selectedPath.getParent() == null || selectedPathStr.equalsIgnoreCase("C:\\") || selectedPathStr.equals("/")) {
            isDangerous = true;
            warningReason = "Doğrudan ana sürücüyü (Kök Dizin) seçtiniz. Bu işlem sistemdeki milyonlarca dosyayı tarayacağı için ciddi zaman alacaktır.";
        }

        else {
            String pathLower = selectedPathStr.toLowerCase();
            if (pathLower.contains("c:\\windows") || pathLower.contains("c:\\program files") || pathLower.contains("appdata")) {
                isDangerous = true;
                warningReason = "Bir sistem veya uygulama veri klasörü seçtiniz. Bu alanlarda çok fazla küçük sistem dosyası bulunduğu için tarama uzun sürebilir.";
            }

        }
        if (isDangerous) {
            boolean userWantsToContinue = AlertUtil.showConfirmation(
                    "Derin Klasör / Disk Uyarısı",
                    "Uzun Sürebilecek Tarama İşlemi",
                    warningReason + "\n\nYine de tarama işlemini başlatmak istiyor musunuz?",
                    "Yine de Devam Et"
            );

            if (!userWantsToContinue) {
                log.info("Kullanıcı tehlikeli klasör uyarısını onaylamadı, tarama iptal edildi.");
                return;
            }
        }
        boolean skipHiddenFiles = skipHiddenFilesCheck.isSelected();
        log.info("Tarama işlemi tetikleniyor. Yol: {}", selectedPath);
        apiScanService.startDirectoryScan(selectedPathStr,skipHiddenFiles)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            AlertUtil.showInfo("Başarılı", "Tarama Başlatıldı", "Klasör taranıyor.");
                        } else if (response.statusCode() == 409) {
                            AlertUtil.showError("Hata", "Klasör zaten izleniyor","Seçtiğiniz klasör zaten gözlem altında işlemlerinize güncel şekilde devam edebilirsiniz");
                        } else {
                            AlertUtil.showError("Hata", "Hata", "Kod: " + response.statusCode());
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        AlertUtil.showError("Bağlantı Hatası", "Sunucuya ulaşılamadı", ex.getMessage());
                    });
                    return null;
                });
    }
    @FXML
    private void handleOpenGitHub() {
        openWebPage("https://github.com/berkya0");
    }

    @FXML
    private void handleOpenLinkedIn() {
        openWebPage("https://www.linkedin.com/in/berkya");
    }

    private void openWebPage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}