package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.ApiFavoriteStatusService;
import com.berkaykomur.filesearchfrontend.service.ApiFileGeneralService;
import com.berkaykomur.filesearchfrontend.util.ContextMenuUtil;
import com.berkaykomur.filesearchfrontend.util.DashboardUIutil;
import com.berkaykomur.filesearchfrontend.util.IconProvider;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.function.Consumer;

@Slf4j
public class DashboardView {
    @FXML private FlowPane favFoldersContainer;
    private final ApiFavoriteStatusService favoriteService = new ApiFavoriteStatusService();
    @FXML private ListView<FileDto> frequentSearchesListView;
    private final ApiFileGeneralService fileGeneralService = new ApiFileGeneralService();
    @Getter
    @Setter
    private static String folderPath="";
    @Setter
    private Consumer<String> onFolderSelected;

    @FXML
    public void initialize() {
        loadFavoriteFolders();
        loadRecentFiles();
    }

    public void loadFavoriteFolders() {
        favoriteService.getFavoriteFolders().thenAccept(folders -> {
            javafx.application.Platform.runLater(() -> {
                favFoldersContainer.getChildren().clear();

                for (FileDto folder : folders) {
                    favFoldersContainer.getChildren().add(DashboardUIutil.createFolderBox(folder, (path) -> {
                       if(onFolderSelected != null) {
                           onFolderSelected.accept(path);
                       }
                    }));
                }
                favFoldersContainer.getChildren().add(DashboardUIutil.createAddButton(this::addNewFolder));
            });
        });
    }

    private void addNewFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        File selected = dc.showDialog(null);
        if (selected != null) {
            favoriteService.updateFavoriteStatus(selected.getAbsolutePath(), true)
                    .thenRun(() -> javafx.application.Platform.runLater(this::loadFavoriteFolders));
        }
    }
    private void loadRecentFiles() {
        fileGeneralService.getTop10Files().thenAccept(files -> {
            javafx.application.Platform.runLater(() -> {
                frequentSearchesListView.getItems().setAll(files);
                frequentSearchesListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(FileDto file, boolean empty) {
                        super.updateItem(file, empty);
                        if (empty || file == null) {
                            setGraphic(null);
                            setContextMenu(null);
                        } else {
                            HBox row = new HBox(10);
                            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                            ImageView icon = IconProvider.getIconForExtension(file);

                            Label name = new Label(file.getName());
                            name.setStyle("-fx-text-fill: #1E293B;");

                            Label path = new Label(file.getPath());
                            path.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11;");

                            Label favStar = new Label(file.isFavorite() ? "★" : "☆");
                            favStar.setStyle(file.isFavorite()
                                    ? "-fx-text-fill: #F59E0B; -fx-cursor: hand; -fx-font-size: 16px;"
                                    : "-fx-text-fill: #94A3B8; -fx-cursor: hand; -fx-font-size: 16px;");

                            favStar.setOnMouseClicked(e -> {
                                boolean nextStatus = !file.isFavorite();
                                favoriteService.updateFavoriteStatus(file.getPath(), nextStatus)
                                        .thenRun(() -> javafx.application.Platform.runLater(() -> {
                                            file.setFavorite(nextStatus);
                                            loadRecentFiles();
                                        }));
                            });
                            loadFavoriteFolders();

                            Region spacer = new Region();
                            HBox.setHgrow(spacer, Priority.ALWAYS);

                            row.getChildren().addAll(favStar, icon, name, spacer, path);
                            setGraphic(row);
                            setContextMenu(ContextMenuUtil.createContextMenu(file, () -> loadRecentFiles()));
                        }
                    }
                });
            });
        });
    }
}
