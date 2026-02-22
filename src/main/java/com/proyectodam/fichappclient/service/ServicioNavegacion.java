package com.proyectodam.fichappclient.service;

import com.proyectodam.fichappclient.controller.EstructuraPrincipalController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Servicio Singleton que gestiona la navegación entre vistas de la aplicación.
 * Controla el cambio de escenas y la inyección de contenido en el área
 * principal.
 */
public class ServicioNavegacion {

    private static ServicioNavegacion instance;
    private StackPane contentArea;
    private Stage primaryStage;

    private ServicioNavegacion() {
    }

    public static ServicioNavegacion getInstance() {
        if (instance == null) {
            instance = new ServicioNavegacion();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/home-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass().getResource("/com/proyectodam/fichappclient/styles/main.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToMainLayout(String initialView) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass().getResource("/com/proyectodam/fichappclient/styles/main.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

            // Obtenemos el controlador de la vista principal para configurar la vista inicial
            EstructuraPrincipalController controller = loader.getController();

            if (initialView != null && !initialView.isEmpty()) {
                switch (initialView) {
                    case "dashboard":
                        controller.showDashboard();
                        break;
                    case "history":
                        controller.showHistory();
                        break;
                    case "vacaciones":
                        controller.showVacations();
                        break;
                    case "documents":
                        controller.toggleDocuments();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            } else {
                System.err.println("ContentArea is null. Cannot navigate to: " + fxmlPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error navigating to: " + fxmlPath);
        }
    }

    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
