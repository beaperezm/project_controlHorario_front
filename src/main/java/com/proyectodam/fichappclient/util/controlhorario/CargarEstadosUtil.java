package com.proyectodam.fichappclient.util.controlhorario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Proporciona la lista fija de estados posibles de un empleado para rellenar ComboBox. */
public class CargarEstadosUtil {

    public ObservableList<String> cargarEstados() {
        return FXCollections.observableArrayList(
                "ACTIVO",
                "BAJA_MEDICA",
                "EXCEDENCIA",
                "INACTIVO"
        );
    }
}
