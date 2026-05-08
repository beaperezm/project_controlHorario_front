package com.proyectodam.fichappclient.util;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SessionData {

    private static SessionData sessionData;
    private EmpleadoDTO usuarioLogueado;
    private int idEmpresa;
    private final ObservableList<EmpleadoDTO> empleados = FXCollections.observableArrayList();

    private SessionData() {
    }

    public static SessionData getInstance() {
        if (sessionData == null) {
            sessionData = new SessionData();
        }
        return sessionData;
    }

    public ObservableList<EmpleadoDTO> getEmpleados() {
        return empleados;
    }

    public EmpleadoDTO getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(EmpleadoDTO usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    // Alias para compatibilidad con controladores que usan "EmpleadoLogueado"
    public EmpleadoDTO getEmpleadoLogueado() {
        return usuarioLogueado;
    }

    public void setEmpleadoLogueado(EmpleadoDTO empleadoDTO) {
        this.usuarioLogueado = empleadoDTO;
    }

    public void clearSession() {
        empleados.clear();
        usuarioLogueado = null;
        idEmpresa = 0;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public static String getRolActual() {
        if (getInstance().getUsuarioLogueado() != null) {
            return getInstance().getUsuarioLogueado().getRol();
        }
        return null;
    }

    public static EmpleadoDTO getEmpleadoActual() {
        return getInstance().getUsuarioLogueado();
    }
}
