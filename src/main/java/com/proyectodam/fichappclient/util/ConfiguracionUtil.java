package com.proyectodam.fichappclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase de utilidad para gestionar la apertura de ventanas de configuración.
 * Encarga de cargar el FXML correspondiente e inicializar el escenario (Stage).
 */
public class ConfiguracionUtil {

    /**
     * Abre la ventana de configuración en modo modal
     * 
     * @param parentStage La ventana padre desde donde se abre
     */
    public static void abrirVentanaConfiguracion(Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfiguracionUtil.class.getResource(
                            "/com/proyectodam/fichappclient/views/configuracion-view.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Configuración de Conexión");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(parentStage);

            Scene scene = new Scene(root, 600, 550);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al abrir configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana de configuración sin ventana padre
     */
    public static void abrirVentanaConfiguracion() {
        abrirVentanaConfiguracion(null);
    }
}
