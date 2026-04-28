package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FileViewUtil {

    public static void setupTableColumns(
            TableColumn<FileDto, String> colFileName,
            TableColumn<FileDto, String> colPath,
            TableColumn<FileDto, String> colSize,
            TableColumn<FileDto, String> colLastModified) {

        colFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPath.setCellValueFactory(new PropertyValueFactory<>("path"));

        colSize.setCellValueFactory(cellData -> {
            long sizeLong = cellData.getValue().getSize();
            String readableSize = FileUtil.getReadableSize(sizeLong);
            return new ReadOnlyStringWrapper(readableSize);
        });

        colLastModified.setCellValueFactory(cellData -> {
            long lastModified = cellData.getValue().getLastModified();
            String readableFormat = FileUtil.formatMillis(lastModified);
            return new ReadOnlyStringWrapper(readableFormat);
        });

    }

    public static void setupInfiniteScroll(TableView<FileDto> searchListView, FileSearchService fileSearchService) {
        Platform.runLater(() -> {
            ScrollBar verticalBar = (ScrollBar) searchListView.lookup(".scroll-bar:vertical");
            if (verticalBar != null) {
                verticalBar.valueProperty().addListener((obs, oldValue, newValue) -> {
                    // Scrollbar %80 veya %90 aşağı indiğinde yeni veriyi çek
                    if (newValue.doubleValue() >= verticalBar.getMax() * 0.9) {
                        fileSearchService.loadNextPage();
                    }
                });
            }
        });
    }
}
