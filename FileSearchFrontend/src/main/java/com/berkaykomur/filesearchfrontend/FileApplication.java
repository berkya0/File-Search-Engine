package com.berkaykomur.filesearchfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // Stage'i yüklerken boyutu FXML'e bırakın veya geniş tutun
        Scene scene = new Scene(fxmlLoader.load(), 1020, 650); // Genişliği 1000, yüksekliği 650 yapın
        stage.setScene(scene);
        stage.setTitle("Dosya Arama Motoru");
        stage.show();
    }
}
