package com.berkaykomur.filesearchfrontend.view;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.enums.FileCategory;
import com.berkaykomur.filesearchfrontend.manager.SearchManager;
import com.berkaykomur.filesearchfrontend.manager.FavoriteManager;
import com.berkaykomur.filesearchfrontend.service.FileSearchService;
import com.berkaykomur.filesearchfrontend.util.FileViewUtil;
import com.berkaykomur.filesearchfrontend.util.TableConfigurator;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class FileView {
    @FXML private TextField searchField;
    @FXML private TableView<FileDto> searchListView;
    @FXML private TableColumn<FileDto, Boolean> colFavorite;
    @FXML private TableColumn<FileDto, String> colFileName;
    @FXML private TableColumn<FileDto, String> colPath;
    @FXML private TableColumn<FileDto, String> colSize;
    @FXML private TableColumn<FileDto, String> colLastmodified;
    @FXML private RadioButton rbContent;
    @FXML private ToggleGroup searchModeGroup;
    @FXML private Label resultCountLabel;

    @FXML private ToggleGroup filterGroup;

    private final FileSearchService  fileSearchService = new FileSearchService();
    private final SearchManager searchManager=new SearchManager(fileSearchService);

    private final FavoriteManager favoriteManager=new FavoriteManager();

    @FXML
    public void initialize() throws JsonProcessingException {
        log.info("Dashboard hazırlanıyor...");
        setupFolderPath();

        TableConfigurator.setupRow(searchListView);

        FileViewUtil.setupTableColumns(colFileName,colPath,colSize,colLastmodified);
        TableConfigurator.setupFileNameColumn(colFileName);
        TableConfigurator.setupFavoriteColumn(colFavorite,favoriteManager,()->{
            searchListView.refresh();
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchManager.triggerSearch(newValue, getSelectedExtensions());
        });
        searchModeGroup.selectedToggleProperty().addListener((obs, old, newV) ->
                searchManager.updateSearchMode(newV == rbContent, searchField.getText(), getSelectedExtensions()));

        filterGroup.selectedToggleProperty().addListener((obs, old, newV) -> {
            if (newV != null) {
                searchManager.triggerSearch(searchField.getText(), getSelectedExtensions());
            } else {
                old.setSelected(true);
            }
        });

        searchListView.setItems(fileSearchService.getMasterData());

        fileSearchService.getMasterData().addListener((javafx.collections.ListChangeListener<FileDto>) change -> {
            Platform.runLater(() -> updateResultCount(fileSearchService.getTotalElements()));
        });
        fileSearchService.startNewSearch("",getSelectedExtensions());

        FileViewUtil.setupInfiniteScroll(searchListView,fileSearchService,this::getSelectedExtensions);

    }

    private void setupFolderPath(){
        String path = DashboardView.getFolderPath();
        if (path != null && !path.isEmpty()) {
            searchField.setText(path);
            DashboardView.setFolderPath("");

            Platform.runLater(this::focusAndMoveCaret);
        }
    }

    private Set<String> getSelectedExtensions() {
        ToggleButton selected = (ToggleButton) filterGroup.getSelectedToggle();
        if (selected == null || selected.getId().equals("btnAll")) {
            return null;
        }
        return FileCategory.getExtensionsById(selected.getId());

    }

    public void focusAndMoveCaret() {
        Platform.runLater(() -> {
            searchField.requestFocus();
            // Uzunluğu tekrar hesaplatarak imleci en sona zorla
            int length = searchField.getText().length();
            searchField.selectRange(length, length);
        });

    }

    public void setFolderPath(String path) {
        this.searchField.setText(path);
    }
    public void updateResultCount(long count) {
        resultCountLabel.setText("Toplam " + count + " nesne bulundu");
    }


}
