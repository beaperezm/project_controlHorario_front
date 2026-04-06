package com.proyectodam.fichappclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class EstructuraPrincipalEmpleadoController {

    @FXML
    private StackPane centerStackPane;

    @FXML
    private Button btnControlHorario;


   @FXML
    private void handleControlHorarioBtn() {
        vistaCargada("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleado.fxml");
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
