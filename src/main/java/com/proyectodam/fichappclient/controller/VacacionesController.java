package com.proyectodam.fichappclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VacacionesController {

    @FXML
    private Label tituloLabel;

    @FXML
    public void initialize() {
        tituloLabel.setText("Módulo de Vacaciones");
    }
}
