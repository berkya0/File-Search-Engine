package com.berkaykomur.filesearchfrontend;

import com.berkaykomur.filesearchfrontend.service.ApiScanService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FileApplication extends Application {

    private final ApiScanService apiScanService=new ApiScanService();
    @Override
    public void start(Stage stage) throws IOException {
        String fxmlPath;
        boolean checkRepository=apiScanService.checkOnboardingStatus();
        if(checkRepository){
            fxmlPath="file-view.fxml";
        }
        else{
            fxmlPath="onboarding-view.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(FileApplication.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), 1020, 650);
        stage.setScene(scene);
        stage.setTitle("Dosya Arama Gezgini");
        stage.show();
    }


}
