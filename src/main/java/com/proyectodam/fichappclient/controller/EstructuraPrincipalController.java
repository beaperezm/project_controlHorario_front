package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EstructuraPrincipalController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox documentsSubmenu;

    @FXML
    private VBox controlHorarioSubmenu;

    @FXML
    public void initialize() {
        // Inicializar ServicioNavegacion con el área de contenido
        ServicioNavegacion.getInstance().setContentArea(contentArea);
    }

    @FXML
    public void showDashboard() {
        // ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/dashboard-view.fxml");
    }

    @FXML
    public void toggleControlHorario() {
        boolean isVisible = controlHorarioSubmenu.isVisible();
        controlHorarioSubmenu.setVisible(!isVisible);
        controlHorarioSubmenu.setManaged(!isVisible);

        // Navegar a la vista de control horario al abrir la sección
        if (!isVisible) {
            ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/control-horario.fxml");
        }
    }

    @FXML
    public void showEmpleados() {
        ServicioNavegacion.getInstance().navigateTo(
                "/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");
    }

    @FXML
    public void showHistory() {
        // ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/history-view.fxml");
    }

    @FXML
    public void showVacations() {
        ServicioNavegacion.getInstance()
                .navigateTo("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-view.fxml");
    }

    @FXML
    public void toggleDocuments() {
        boolean isVisible = documentsSubmenu.isVisible();
        documentsSubmenu.setVisible(!isVisible);
        documentsSubmenu.setManaged(!isVisible);

        // Navegar a la vista general de documentos al abrir la sección
        if (!isVisible) {
            ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/documents-view.fxml");
        }
    }

    @FXML
    public void showNominas() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/nominas-view.fxml");
    }

    @FXML
    public void showCalendario() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/calendario.fxml");
    }

    @FXML
    private void handleLogout() {
        ServicioNavegacion.getInstance().logout();
    }

    @FXML
    private VBox configuracionSubmenu;

    @FXML
    public void toggleConfiguracion() {
        boolean isVisible = configuracionSubmenu.isVisible();
        configuracionSubmenu.setVisible(!isVisible);
        configuracionSubmenu.setManaged(!isVisible);

        if (!isVisible) {
            showConfigGeneral();
        }
    }

    @FXML
    public void showConfigGeneral() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion-general.fxml");
    }

    @FXML
    public void showConfigEmpleados() {
        // Marcador de posición
    }

    @FXML
    public void showConfigAsistencias() {
        // Marcador de posición
    }
}
