package com.proyectodam.fichappclient.model;

import java.time.LocalDate;

public class VacacionesDTO {
    private int id;
    private String tipo;
    private LocalDate inicio;
    private LocalDate fin;
    private int dias;
    private String estado;
    private String motivo;

    public VacacionesDTO(int id, String tipo, LocalDate inicio, LocalDate fin, int dias, String estado, String motivo) {
        this.id = id;
        this.tipo = tipo;
        this.inicio = inicio;
        this.fin = fin;
        this.dias = dias;
        this.estado = estado;
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public int getDias() {
        return dias;
    }

    public String getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
