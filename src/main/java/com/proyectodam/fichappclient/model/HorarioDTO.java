package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HorarioDTO {

    private int idHorario;
    private String nombre;

    public HorarioDTO(int idHorario, String nombre) {
        this.idHorario = idHorario;
        this.nombre = nombre;
    }

    public HorarioDTO() {
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
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
