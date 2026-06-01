package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.manager.FavoriteManager;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import static com.berkaykomur.filesearchfrontend.util.ContextMenuUtil.fileGeneralService;

@Slf4j
public class TableConfigurator {

    public static void setupFileNameColumn(TableColumn<FileDto, String> colFileName){

        colFileName.setCellFactory(column -> new TableCell<FileDto, String>() {
            @Override
            protected void updateItem(String fileName, boolean empty) {
                super.updateItem(fileName, empty);

                if (empty || fileName == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    FileDto file = getTableView().getItems().get(getIndex());
                    log.info("Dosya Adı: {}, Uzantısı: {}", file.getName(), file.getExtension());

                    setGraphic(IconProvider.getIconForExtension(file));
                    setText(" " + fileName);
                }
            }
        });

    }

    public static void setupFavoriteColumn(TableColumn<FileDto, Boolean> colFavorite, FavoriteManager favoriteManager,
                                           Runnable refreshTable){
        colFavorite.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isFavorite())
        );

        colFavorite.setCellFactory(column -> new TableCell<FileDto, Boolean>() {
            private final Label starLabel = new Label();
            {
                starLabel.setStyle("-fx-cursor: hand; -fx-font-size: 18px;");
                starLabel.setOnMouseClicked(event -> {
                    FileDto file = getTableView().getItems().get(getIndex());
                    favoriteManager.toggleFavorite(file,refreshTable);
                });
            }

            @Override
            protected void updateItem(Boolean isFavorite, boolean empty) {
                super.updateItem(isFavorite, empty);
                if (empty || isFavorite == null) {
                    setGraphic(null);
                } else {
                    starLabel.setText(isFavorite ? "★" : "☆");
                    starLabel.setStyle(isFavorite ? "-fx-text-fill: #F59E0B; -fx-cursor: hand; -fx-font-size: 18px;"
                            : "-fx-text-fill: #94A3B8; -fx-cursor: hand; -fx-font-size: 18px;");
                    setGraphic(starLabel);
                }
            }
        });
    }

    public static void setupRow(TableView<FileDto> searchListView){
        searchListView.setRowFactory(tv -> {
            TableRow<FileDto> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ContextMenuUtil.openFile(row.getItem());
                    fileGeneralService.setLastOpen(row.getItem().getPath(), System.currentTimeMillis());
                }
            });

            row.itemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    row.setContextMenu(ContextMenuUtil.createContextMenu(newVal, () -> {
                        searchListView.refresh();
                    }));
                }
            });

            return row;
        });
    }

}
