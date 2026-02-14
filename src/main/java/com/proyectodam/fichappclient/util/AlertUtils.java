package com.proyectodam.fichappclient.util;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AlertUtils {

    public static void mostrarInfo(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    public static void mostrarError(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, Alert.AlertType.ERROR);
    }

    public static void mostrarAdvertencia(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, Alert.AlertType.WARNING);
    }

    public static void mostrarConfirmacion(String titulo, String mensaje) {
        crearAlerta(titulo, mensaje, Alert.AlertType.CONFIRMATION);
    }

    public static void crearAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        alert.showAndWait();
    }
}
