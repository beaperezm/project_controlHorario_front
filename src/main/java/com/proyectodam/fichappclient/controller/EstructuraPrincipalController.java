package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EstructuraPrincipalController {

    @FXML
    private StackPane centerStackPane;

    @FXML
    private ToggleButton btnControlHorario;
    @FXML
    private Button btnControlHorarioEmpleados;

    @FXML
    private VBox subControlHorarioMenu;

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
         ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/dashboard-view.fxml");
    }

 /*   @FXML
    public void toggleControlHorario() {
        boolean isVisible = controlHorarioSubmenu.isVisible();
        controlHorarioSubmenu.setVisible(!isVisible);
        controlHorarioSubmenu.setManaged(!isVisible);

        // Navegar a la vista de control horario al abrir la sección
        if (!isVisible) {
            ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/control-horario.fxml");
        }
    }*/

    @FXML
    private void handleControlHorarioMenu() {
        //Para alternar la visibilidad de los sub-botones
        boolean isVisible = btnControlHorario.isSelected();
        subControlHorarioMenu.setVisible(isVisible);
        subControlHorarioMenu.setManaged(isVisible); //con esto ajustamos el layout
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador.fxml");

    }

    @FXML
    private void handleEmpleados() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");

    }

  /*  @FXML
    public void showEmpleados() {
        ServicioNavegacion.getInstance().navigateTo(
                "/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");
    }*/

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

  /*  private void vistaCargada(String fxmlRuta)  {

        try{

            //Cargando el fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = fxmlLoader.load();

            centerStackPane.getChildren().clear();
            centerStackPane.getChildren().add(vista);

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            System.out.println("Error real al cargar el fxml");
        }


    }*/
}
