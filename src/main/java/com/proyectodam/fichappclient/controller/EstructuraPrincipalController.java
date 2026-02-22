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
    public void initialize() {
        // Inicializar ServicioNavegacion con el área de contenido
        ServicioNavegacion.getInstance().setContentArea(contentArea);
    }

    @FXML
    public void showDashboard() {
        System.out.println("Mostrar Dashboard - Aún no implementado o vista faltante");
        // ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/dashboard-view.fxml");
    }

    @FXML
    public void showControlHorario() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/control-horario.fxml");
    }

    @FXML
    public void showHistory() {
        System.out.println("Mostrar Historial - Aún no implementado");
        // ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/history-view.fxml");
    }

    @FXML
    public void showVacations() {
        System.out.println("Mostrar Vacaciones - Aún no implementado");
        // ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/vacaciones-view.fxml");
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
        System.out.println("Navegar a Nóminas");
        // TODO: NavigationService.getInstance().navigateTo("...");
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
        System.out.println("Navegar a Configuración Empleados");
    }

    @FXML
    public void showConfigAsistencias() {
        // Marcador de posición
        System.out.println("Navegar a Configuración Asistencias");
    }
}
