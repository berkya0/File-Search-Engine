package com.berkaykomur.filesearchfrontend.controller;
import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.service.ApiService;
import com.berkaykomur.filesearchfrontend.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
public class MainController {
    @FXML private TextField searchField;
    @FXML private TableView<FileDto> searchListView;
    @FXML private TableColumn<FileDto, String> colFileName;
    @FXML private TableColumn<FileDto, String> colPath;
    @FXML private TableColumn<FileDto, String> colSize;
    @FXML private TableColumn<FileDto, String> colLastmodified;

    private final ApiService apiService = new ApiService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObservableList<FileDto> masterData = FXCollections.observableArrayList();

    private int currentPage = 0;

    @FXML
    public void initialize() {

        colFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPath.setCellValueFactory(new PropertyValueFactory<>("path"));
        colSize.setCellValueFactory(cellData -> {
            long sizeLong = cellData.getValue().getSize();
            String readableSize = FileUtil.getReadableSize(sizeLong);
            return new ReadOnlyStringWrapper(readableSize);
        });
        colLastmodified.setCellValueFactory(cellData->{
            long lastModified = cellData.getValue().getLastModified();
            String readableFormat = FileUtil.formatMillis(lastModified);
            return new ReadOnlyStringWrapper(readableFormat);
        });

        searchListView.setItems(masterData);
        fetchData("");
    }

    @FXML
    private void onSearchButtonClick() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        // Yeni aramada listeyi ve sayfa numarasını sıfırla
        masterData.clear();
        currentPage = 0;

        fetchData(query);
    }

    private void fetchData(String query) {
        apiService.searchFiles(query, currentPage).thenAccept(jsonResponse -> {
            try {
                // 1. JSON metnini bir ağaç yapısına (Tree) çeviriyoruz
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // 2. Spring Page yapısında asıl liste "content" düğümü altındadır
                JsonNode contentNode = rootNode.get("content");

                if (contentNode != null && contentNode.isArray()) {
                    // 3. JSON dizisini List<FileDto> nesnesine dönüştürüyoruz
                    List<FileDto> files = objectMapper.readerForListOf(FileDto.class).readValue(contentNode);

                    // 4. UI güncellemelerini ana thread (Platform.runLater) içinde yapıyoruz
                    Platform.runLater(() -> {
                        masterData.addAll(files);
                        System.out.println(files.size() + " adet dosya eklendi.");
                    });
                }
            } catch (Exception e) {
                System.err.println("JSON Ayrıştırma Hatası: " + e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            System.err.println("API İsteği Hatası: " + ex.getMessage());
            return null;
        });
    }

}
