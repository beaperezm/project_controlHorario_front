package com.proyectodam.fichappclient.controller;


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
    private void handleControlHorarioMenu() {
        //Para alternar la visibilidad de los sub-botones
        boolean isVisible = btnControlHorario.isSelected();
        subControlHorarioMenu.setVisible(isVisible);
        subControlHorarioMenu.setManaged(isVisible); //con esto ajustamos el layout

        vistaCargada("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador.fxml");
    }


    @FXML
    private void handleEmpleados() {
        vistaCargada("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");
    }

    private void vistaCargada(String fxmlRuta)  {

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


    }

}
