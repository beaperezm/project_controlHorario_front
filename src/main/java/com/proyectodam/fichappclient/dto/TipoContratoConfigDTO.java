package com.proyectodam.fichappclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoContratoConfigDTO {

    private int idTipoContrato;
    private String nombre;
    private boolean indefinido;
    private Integer duracionMeses;

    public TipoContratoConfigDTO() {
    }

    public TipoContratoConfigDTO(int idTipoContrato, String nombre, boolean indefinido, Integer duracionMeses) {
        this.idTipoContrato = idTipoContrato;
        this.nombre = nombre;
        this.indefinido = indefinido;
        this.duracionMeses = duracionMeses;
    }

    public int getIdTipoContrato() {
        return idTipoContrato;
    }

    public void setIdTipoContrato(int idTipoContrato) {
        this.idTipoContrato = idTipoContrato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isIndefinido() {
        return indefinido;
    }

    public void setIndefinido(boolean indefinido) {
        this.indefinido = indefinido;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    @Override
    public String toString() {
        return nombre + (indefinido ? " (Indefinido)" : " (" + duracionMeses + " meses)");
    }
}
