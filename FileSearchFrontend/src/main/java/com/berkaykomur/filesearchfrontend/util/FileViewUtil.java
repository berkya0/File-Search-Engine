package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
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
}
