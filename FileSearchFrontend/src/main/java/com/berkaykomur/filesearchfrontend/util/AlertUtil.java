package com.berkaykomur.filesearchfrontend.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
public class AlertUtil {

    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #F8FAFC; " +
                        "-fx-background-radius: 14; " +
                        "-fx-border-radius: 14; " +
                        "-fx-border-color: #E2E8F0; " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 4);"
        );

        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle("-fx-background-color: transparent; -fx-padding: 10 5 5 5;");
        }
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #1E293B; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        Label contentLabel = (Label) dialogPane.lookup(".content");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13.5px; -fx-line-spacing: 1.4; -fx-padding: 10 5 15 5;");
        }

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("Tamam");
            okButton.setStyle(
                    "-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 8; " +
                            "-fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12.5px;"
            );

            okButton.setOnMouseEntered(e -> okButton.setStyle(
                    "-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12.5px;"
            ));
            okButton.setOnMouseExited(e -> okButton.setStyle(
                    "-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12.5px;"
            ));
        }

        if (dialogPane.lookup(".button-bar") != null) {
            dialogPane.lookup(".button-bar").setStyle("-fx-background-color: transparent; -fx-padding: 5 5 5 5;");
        }

        alert.initStyle(StageStyle.TRANSPARENT);
        alert.showAndWait();
    }
    public static boolean showConfirmation(String title, String header, String content, String confirmButtonText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType confirmButton = new ButtonType(confirmButtonText);
        ButtonType cancelButton = new ButtonType("İptal");
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #E2E8F0; -fx-padding: 10;");

        Button okBtn = (Button) dialogPane.lookupButton(confirmButton);
        okBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand;");


        Button cancelBtn = (Button) dialogPane.lookupButton(cancelButton);
        cancelBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand;");

        alert.initStyle(StageStyle.TRANSPARENT);
        return alert.showAndWait().orElse(cancelButton) == confirmButton;
    }

    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #E2E8F0; -fx-padding: 10;");

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand;");

        alert.initStyle(StageStyle.TRANSPARENT);
        alert.showAndWait();
    }
}
