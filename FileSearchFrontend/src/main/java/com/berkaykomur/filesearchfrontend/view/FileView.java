package com.berkaykomur.filesearchfrontend.view;
import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.ApiService;
import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import com.berkaykomur.filesearchfrontend.util.FileUtil;
import com.berkaykomur.filesearchfrontend.util.FileViewUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class FileView {
    @FXML private TextField searchField;
    @FXML private TableView<FileDto> searchListView;
    @FXML private TableColumn<FileDto, String> colFileName;
    @FXML private TableColumn<FileDto, String> colPath;
    @FXML private TableColumn<FileDto, String> colSize;
    @FXML private TableColumn<FileDto, String> colLastmodified;

    private final PauseTransition searchDebounce = new PauseTransition(Duration.millis(500));
    private final FileSearchService  fileSearchService = new FileSearchService();
    private final ObservableList<FileDto> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        log.info("Frontend hazırlanıyor...");
        FileViewUtil.setupTableColumns(colFileName,colPath,colSize,colLastmodified);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebounce.setOnFinished(event -> fileSearchService.fetchData(newValue));
            searchDebounce.playFromStart();
        });

        searchListView.setItems(masterData);
        fileSearchService.fetchData("");
    }



}
