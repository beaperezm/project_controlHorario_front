package com.proyectodam.fichappclient.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import com.proyectodam.fichappclient.service.ServicioNavegacion;

public class EstructuraPrincipalEmpleadoController {

    @FXML private StackPane centerStackPane;
    @FXML private Button btnControlHorario;

    @FXML
    public void initialize() {
        // Inicializar el servicio de navegación con el área central
        ServicioNavegacion.getInstance().setContentArea(centerStackPane);
        
        // Cargar el dashboard por defecto si está vacío
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }

    @FXML
    private void handleControlHorarioBtn() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleado.fxml");
    }

    @FXML
    public void showConfiguracion() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion-usuario.fxml");
    }

}
