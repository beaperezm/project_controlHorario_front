package com.proyectodam.fichappclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa la ventana principal (Stage) y carga la vista de inicio de sesión.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApp.class.getResource("/com/proyectodam/fichappclient/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 720);

        stage.setTitle("FichApp");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setMaximized(true);

        com.proyectodam.fichappclient.service.ServicioNavegacion.getInstance().setPrimaryStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
