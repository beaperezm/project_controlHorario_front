package com.proyectodam.fichappclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EstructuraPrincipalController {

    @FXML private StackPane contentArea;

    // Submenús (salen en tu FXML)
    @FXML private VBox documentsSubmenu;
    @FXML private VBox configuracionSubmenu;

    @FXML
    private void initialize() {
        if (documentsSubmenu != null) {
            documentsSubmenu.setVisible(false);
            documentsSubmenu.setManaged(false);
        }
        if (configuracionSubmenu != null) {
            configuracionSubmenu.setVisible(false);
            configuracionSubmenu.setManaged(false);
        }
    }

    // ============ BOTONES LATERALES (coinciden con tu FXML) ============

    @FXML
    private void showDashboard() {
        // Si no tienes dashboard aún, carga una vista “placeholder” o deja vacío
        info("Dashboard", "Aún sin implementar.");
        // vistaCargada("/com/proyectodam/fichappclient/views/dashboard-view.fxml");
    }

    @FXML
    private void showControlHorario() {
        // Cambia la ruta a la que tengas REAL en tu proyecto
        vistaCargada("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador.fxml");
    }

    @FXML
    private void showVacations() {
        vistaCargada("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-view.fxml");
    }

    @FXML
    private void toggleDocuments() {
        toggleBox(documentsSubmenu);
    }

    @FXML
    private void toggleConfiguracion() {
        toggleBox(configuracionSubmenu);
    }

    @FXML
    private void showNominas() {
        info("Nóminas", "Aún sin implementar.");
    }

    @FXML
    private void showConfigGeneral() { info("Config", "General (pendiente)."); }

    @FXML
    private void showConfigEmpleados() { info("Config", "Empleados (pendiente)."); }

    @FXML
    private void showConfigAsistencias() { info("Config", "Asistencias (pendiente)."); }

    @FXML
    private void handleLogout() {
        info("Salir", "Cerrar sesión (pendiente).");
    }

    // ============ CARGADOR DE VISTAS ============

    private void vistaCargada(String fxmlRuta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = loader.load();
            contentArea.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo cargar la vista:\n" + fxmlRuta + "\n\nRevisa ruta y fx:controller.")
                    .showAndWait();
        }
    }

    private void toggleBox(VBox box) {
        if (box == null) return;
        boolean show = !box.isVisible();
        box.setVisible(show);
        box.setManaged(show);
    }

    private void info(String t, String m) {
        // Para no petar, sin más
        System.out.println(t + ": " + m);
    }
}