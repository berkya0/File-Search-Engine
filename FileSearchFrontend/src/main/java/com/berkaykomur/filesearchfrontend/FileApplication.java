package com.berkaykomur.filesearchfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FileApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FileApplication.class.getResource("file-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1020, 650);
        stage.setScene(scene);
        stage.setTitle("Dosya Arama Motoru");
        stage.show();
    }
}
