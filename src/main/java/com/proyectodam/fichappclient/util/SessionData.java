package com.proyectodam.fichappclient.util;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SessionData {

    private static SessionData sessionData;

    private EmpleadoDTO empleadoLogueado;

    private final ObservableList<EmpleadoDTO> empleados = FXCollections.observableArrayList();

    private SessionData() {}

    public static SessionData getInstance() {
        if(sessionData == null) {
            sessionData = new SessionData();
        }
        return sessionData;
    }

    public EmpleadoDTO getEmpleadoLogueado() {
        return empleadoLogueado;
    }

    public void setEmpleadoLogueado(EmpleadoDTO empleadoDTO) {
        this.empleadoLogueado = empleadoDTO;
    }

    public ObservableList<EmpleadoDTO> getEmpleados() {
        return empleados;
    }

    public void clearSession() {
        empleados.clear();
        empleadoLogueado = null;
    }
}
