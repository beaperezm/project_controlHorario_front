package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para la gestión de roles de usuario (ADMIN, RRHH, EMPLEADO...).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RolDTO {

    private int idRol;
    private String nombre;

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
