package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class DashboardUIutil {

    public static VBox createFolderBox(FileDto folder, Consumer<String> onAction) {
        VBox vBox = new VBox(8.0);
        vBox.setPrefWidth(230.0);
        vBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 14;");

        ImageView icon = new ImageView(new Image(DashboardUIutil.class.getResourceAsStream("/icons/folder.png")));
        icon.setFitWidth(16);
        icon.setFitHeight(16);

        Label title = new Label(folder.getName());
        title.setGraphic(icon);
        title.setGraphicTextGap(8);
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        title.setFont(new Font(13.0));

        Label path = new Label(folder.getPath());
        path.setStyle("-fx-text-fill: #64748B;");
        path.setFont(new Font(11.0));
        path.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btn = new Button("Burada Ara");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #4299E1; -fx-text-fill: #F8FAFC; -fx-background-radius: 6; -fx-cursor: hand;");

        btn.setOnMouseClicked(event -> {

            String pathWithSlash = folder.getPath().endsWith("\\") ? folder.getPath() : folder.getPath() + "\\";

            //pathWithSlash gönderiyoruz
            onAction.accept(pathWithSlash);
        });

        vBox.getChildren().addAll(title, path, spacer, btn);
        return vBox;
    }

    public static VBox createAddButton(Runnable onAction) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(140, 105);
        vBox.setStyle("-fx-border-color: #CBD5E1; -fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        vBox.getChildren().addAll(new Label("➕"), new Label("Yeni Ekle"));

        vBox.setOnMouseClicked(event -> onAction.run()

        );
        return vBox;
    }
}
