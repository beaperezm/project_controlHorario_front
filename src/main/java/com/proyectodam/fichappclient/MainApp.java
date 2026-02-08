package com.proyectodam.fichappclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource(
                        "/com/proyectodam/fichappclient/views/vacaciones/vacaciones-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Módulo Vacaciones");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
