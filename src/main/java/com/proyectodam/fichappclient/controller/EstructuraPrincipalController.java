package com.proyectodam.fichappclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EstructuraPrincipalController {

    @FXML private StackPane centerStackPane;

    @FXML private ToggleButton btnControlHorario;
    @FXML private VBox subControlHorarioMenu;

    @FXML
    private void initialize() {
        // Submenú oculto al arrancar
        if (subControlHorarioMenu != null) {
            subControlHorarioMenu.setVisible(false);
            subControlHorarioMenu.setManaged(false);
        }
        if (btnControlHorario != null) {
            btnControlHorario.setSelected(false);
        }

        // ✅ Vista segura por defecto (para que NO salga en blanco)
        vistaCargada("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-view.fxml");
    }

    @FXML
    private void handleControlHorarioMenu() {
        // ✅ SOLO despliega/oculta el submenú (NO carga control-horario.fxml)
        boolean isVisible = btnControlHorario.isSelected();
        subControlHorarioMenu.setVisible(isVisible);
        subControlHorarioMenu.setManaged(isVisible);
    }

    @FXML
    private void handleEmpleados() {
        // Aquí sí cargamos la vista (y aquí es donde puede fallar si API caída)
        vistaCargada("/com/proyectodam/fichappclient/views/control-horario.fxml");
    }

    @FXML
    private void handleVacaciones() {
        // Ocultamos el submenú si estaba abierto
        if (btnControlHorario != null) btnControlHorario.setSelected(false);
        if (subControlHorarioMenu != null) {
            subControlHorarioMenu.setVisible(false);
            subControlHorarioMenu.setManaged(false);
        }

        vistaCargada("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-view.fxml");
    }

    private void vistaCargada(String fxmlRuta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = loader.load();
            centerStackPane.getChildren().setAll(vista);
            System.out.println("✅ Vista cargada: " + fxmlRuta);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar el FXML: " + fxmlRuta);
            e.printStackTrace();
        }
    }
}