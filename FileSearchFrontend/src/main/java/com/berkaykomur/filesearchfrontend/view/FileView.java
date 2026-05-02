package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.enums.FileCategory;
import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import com.berkaykomur.filesearchfrontend.util.FileViewUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class FileView {
    @FXML private TextField searchField;
    @FXML private TableView<FileDto> searchListView;
    @FXML private TableColumn<FileDto, String> colFileName;
    @FXML private TableColumn<FileDto, String> colPath;
    @FXML private TableColumn<FileDto, String> colSize;
    @FXML private TableColumn<FileDto, String> colLastmodified;

    @FXML private ToggleGroup filterGroup;

    private final PauseTransition searchDebounce = new PauseTransition(Duration.millis(500));
    private final FileSearchService  fileSearchService = new FileSearchService();

    @FXML
    public void initialize() throws JsonProcessingException {
        log.info("Frontend hazırlanıyor...");
        FileViewUtil.setupTableColumns(colFileName,colPath,colSize,colLastmodified);

        filterGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ToggleButton selectedBtn = (ToggleButton) newValue;
                log.info("Kategori değişti: {}, Yeni arama başlatılıyor...", selectedBtn.getText());

                String searchTerm = searchField.getText();

                Set<String> extensions = getSelectedExtensions();
                try {
                    fileSearchService.startNewSearch(searchTerm, extensions);
                } catch (JsonProcessingException e) {
                    log.error("Json dönüştürme hatası: {}",e.getMessage());
                }
            } else {
                oldValue.setSelected(true);
            }

        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebounce.setOnFinished(event -> {
                try {
                    fileSearchService.startNewSearch(newValue,getSelectedExtensions());
                } catch (JsonProcessingException e) {
                    log.error("Json dönüştürme hatası: {}",e.getMessage());
                }
            });
            searchDebounce.playFromStart();
        });

        searchListView.setItems(fileSearchService.getMasterData());
        fileSearchService.startNewSearch("",getSelectedExtensions());

        FileViewUtil.setupInfiniteScroll(searchListView,fileSearchService,this::getSelectedExtensions);
    }

    private Set<String> getSelectedExtensions() {
        ToggleButton selected = (ToggleButton) filterGroup.getSelectedToggle();
        if (selected == null || selected.getId().equals("btnAll")) {
            return null;
        }
       Set<String> extensions= FileCategory.getExtensionsById(selected.getId());
        log.info("Uzantılar: {}",extensions);
        return extensions;

    }

}
