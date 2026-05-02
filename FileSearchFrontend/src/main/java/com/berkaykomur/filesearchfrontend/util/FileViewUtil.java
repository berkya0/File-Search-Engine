package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.function.Supplier;

@Slf4j
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
    public static void setupInfiniteScroll(TableView<FileDto> searchListView,
                                           FileSearchService fileSearchService,
                                           Supplier<Set<String>> extensionsSupplier) {
        Platform.runLater(() -> {
            ScrollBar verticalBar = (ScrollBar) searchListView.lookup(".scroll-bar:vertical");
            if (verticalBar != null) {
                verticalBar.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue.doubleValue() >= verticalBar.getMax() * 0.9) {
                        try {
                            fileSearchService.loadNextPage(extensionsSupplier.get());
                        } catch (JsonProcessingException e) {
                            log.error("Sayfa yüklenirken JSON hatası: {}", e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
