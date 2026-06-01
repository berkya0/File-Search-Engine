package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.util.FileViewUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MainLayoutView implements Initializable {
    @FXML
    private Button homeButton;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button settingsButton;

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView("/com/berkaykomur/filesearchfrontend/home-view.fxml");
    }

    @FXML
    public void handleHomeButton() {
       updateButtonStyles(homeButton);
        log.info("Arama ekranına geçiş yapılıyor...");
        loadView("/com/berkaykomur/filesearchfrontend/home-view.fxml");
    }

    @FXML
    private void handleSettingsButton() {
       updateButtonStyles(settingsButton);
        log.info("Ayarlar ekranına geçiş yapılıyor...");
        loadView("/com/berkaykomur/filesearchfrontend/settings-view.fxml");
    }
    @FXML
    private void handleDashboardButton() {
        updateButtonStyles(dashboardButton);
        log.info("Dashboard ekranına geçiş yapılıyor...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/berkaykomur/filesearchfrontend/dashboard-view.fxml"));
            Parent view = loader.load();

            DashboardView controller = loader.getController();
            controller.setOnFolderSelected(path ->
                    loadViewWithData("/com/berkaykomur/filesearchfrontend/home-view.fxml", path)
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            log.error("Dashboard yüklenirken hata oluştu!", e);
        }
    }

    public void loadViewWithData(String fxmlPath, String initialPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof FileView) {
                ((FileView) controller).setFolderPath(initialPath);
                ((FileView) controller).focusAndMoveCaret();
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            log.error("Hata!", e);
        }
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            log.error("{} yüklenirken hata oluştu!", fxmlPath, e);
        }
    }
    private void updateButtonStyles(Button selectedButton) {
        Button[] allButtons = {homeButton, dashboardButton, settingsButton};
        for (Button btn : allButtons) {
            if (btn == selectedButton) {
                btn.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 12; -fx-cursor: hand;");
                if (btn.getGraphic() != null) {
                    ((SVGPath) btn.getGraphic()).setFill(javafx.scene.paint.Color.WHITE);
                }
            } else {

                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                if (btn.getGraphic() != null) {
                    ((SVGPath) btn.getGraphic()).setFill(javafx.scene.paint.Color.valueOf("#94A3B8"));
                }
            }
        }
    }

}