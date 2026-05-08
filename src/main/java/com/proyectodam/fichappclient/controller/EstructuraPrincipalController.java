package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EstructuraPrincipalController {

    @FXML
    private VBox rootContainer;

    @FXML
    private StackPane contentArea;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private BorderPane sidebar;

    @FXML
    private VBox documentsSubmenu;

    @FXML
    private VBox controlHorarioSubmenu;

    @FXML
    private HBox titleBar;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean manualMaximized = false;

    /**
     * Llamado desde ServicioNavegacion para indicar el estado de maximización manual.
     */
    public void setManualMaximized(boolean value) {
        this.manualMaximized = value;
    }

    @FXML
    public void initialize() {
        // Inicializar ServicioNavegacion con el área de contenido
        ServicioNavegacion.getInstance().setContentArea(contentArea);

        // Aplicar clip al contentArea para que su contenido no desborde
        javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle();
        clipRect.widthProperty().bind(contentArea.widthProperty());
        clipRect.heightProperty().bind(contentArea.heightProperty());
        contentArea.setClip(clipRect);

        // Ajustar vista según rol
        checkUserRoleAndAdjustView();

        // Configurar arrastre de ventana desde la barra de título
        if (titleBar != null) {
            titleBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    private void checkUserRoleAndAdjustView() {
        com.proyectodam.fichappclient.model.EmpleadoDTO u = com.proyectodam.fichappclient.util.SessionData.getInstance()
                .getUsuarioLogueado();

        if (u != null && "USER".equals(u.getRol())) {
            // Es un usuario básico: ocultar barra lateral
            if (sidebar != null) {
                mainBorderPane.setLeft(null);
            }
            ServicioNavegacion.getInstance().navigateToDashboardBase();
        } else {
            // Cargar el panel de empleados por defecto para admin
            showEmpleados();
        }
    }

    public void showDashboard() {
        com.proyectodam.fichappclient.model.EmpleadoDTO u = com.proyectodam.fichappclient.util.SessionData.getInstance()
                .getUsuarioLogueado();

        if (u != null && "USER".equals(u.getRol())) {
            ServicioNavegacion.getInstance().navigateToDashboardBase();
        } else {
            ServicioNavegacion.getInstance().navigateTo(
                    "/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");
        }
    }

    /**
     * Sincroniza el estado del sidebar (qué submenú está abierto) basado en la ruta FXML.
     * Implementa comportamiento de Acordeón.
     */
    public void syncSidebarState(String fxmlPath) {
        // Colapsar todo por defecto
        boolean showControl = false;
        boolean showDocs = false;
        boolean showConfig = false;

        // Determinar qué submenú expandir según la ruta
        if (fxmlPath.contains("/controlhorario/")) {
            showControl = true;
        } else if (fxmlPath.contains("documents-view.fxml") || fxmlPath.contains("nominas-admin-view.fxml")) {
            showDocs = true;
        } else if (fxmlPath.contains("/configuracion/") || fxmlPath.contains("configuracion-general.fxml")) {
            showConfig = true;
        }

        // Aplicar estados de visibilidad y gestión
        if (controlHorarioSubmenu != null) {
            controlHorarioSubmenu.setVisible(showControl);
            controlHorarioSubmenu.setManaged(showControl);
        }
        if (documentsSubmenu != null) {
            documentsSubmenu.setVisible(showDocs);
            documentsSubmenu.setManaged(showDocs);
        }
        if (configuracionSubmenu != null) {
            configuracionSubmenu.setVisible(showConfig);
            configuracionSubmenu.setManaged(showConfig);
        }
    }

    @FXML
    public void toggleControlHorario() {
        // Si ya está abierto, simplemente navegamos al panel principal de la sección
        // Si está cerrado, syncSidebarState se encargará de abrirlo al navegar
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador.fxml");
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
        com.proyectodam.fichappclient.model.EmpleadoDTO u = com.proyectodam.fichappclient.util.SessionData.getInstance().getUsuarioLogueado();
        if (u != null && "ADMIN".equalsIgnoreCase(u.getRol())) {
             ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-admin-view.fxml");
        } else {
             ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-empleado-view.fxml");
        }
    }

    @FXML
    public void toggleDocuments() {
        // Navegamos a la vista general. syncSidebarState se encarga de la expansión.
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/documents-view.fxml");
    }

    @FXML
    public void showNominas() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/nominas-admin-view.fxml");
    }

    @FXML
    public void showCalendario() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/calendario.fxml");
    }

    @FXML
    public void showInformes() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/informes-view.fxml");
    }

    @FXML
    private void handleLogout() {
        ServicioNavegacion.getInstance().logout();
    }

    @FXML
    private VBox configuracionSubmenu;

    @FXML
    public void toggleConfiguracion() {
        showConfigGeneral();
    }

    @FXML
    public void showConfigGeneral() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion-general.fxml");
    }

    @FXML
    public void showConfigEmpleados() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion/configuracion-empleados.fxml");
    }

    @FXML
    public void showConfigPerfil() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion/perfil-admin.fxml");
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) rootContainer.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) rootContainer.getScene().getWindow();
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        
        // Nunca usamos setMaximized nativo: causa clipping con UNDECORATED en Windows.
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }
        
        if (manualMaximized) {
            // Restaurar a tamaño ventana
            stage.setWidth(1024);
            stage.setHeight(720);
            double newX = screenBounds.getMinX() + (screenBounds.getWidth() - 1024) / 2;
            double newY = screenBounds.getMinY() + (screenBounds.getHeight() - 720) / 2;
            stage.setX(Math.max(newX, screenBounds.getMinX()));
            stage.setY(Math.max(newY, screenBounds.getMinY()));
            manualMaximized = false;
        } else {
            // Maximizar manualmente
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            manualMaximized = true;
        }
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }
}
