package com.proyectodam.fichappclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa la ventana principal (Stage) y carga la vista de inicio de sesión.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
            javafx.application.Platform.runLater(() ->
                com.proyectodam.fichappclient.util.AlertUtils.mostrarError(
                    "Error inesperado",
                    "Hilo: " + thread.getName() + "\n" + throwable.getMessage())
            )
        );

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApp.class.getResource("/com/proyectodam/fichappclient/views/login-view.fxml"));
        // Cargamos la vista de login sin dimensiones fijas para que se ajuste al contenido
        Scene scene = new Scene(fxmlLoader.load());
        
        // Estilo sin bordes de ventana estándar
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        
        // Añadir icono a la aplicación
        try {
            stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/com/proyectodam/fichappclient/img/favicon.png")));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }
        
        stage.setTitle("Fich-App");
        stage.setScene(scene);

        // El arrastre de la ventana ahora se gestiona directamente desde la barra superior (titleBar) en ControladorLogin.

        com.proyectodam.fichappclient.util.WindowResizeHelper.addResizeListener(stage);

        com.proyectodam.fichappclient.service.ServicioNavegacion.getInstance().setPrimaryStage(stage);

        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
