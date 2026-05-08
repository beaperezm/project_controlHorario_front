package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/** DTO simplificado de una solicitud de vacaciones/ausencia, usado en la vista del empleado. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudVacacionesDTO {

    private Integer id;
    private String tipo;
    private LocalDate inicio;
    private LocalDate fin;
    private Integer dias;
    private String estado;

    public SolicitudVacacionesDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFin() { return fin; }
    public void setFin(LocalDate fin) { this.fin = fin; }

    public Integer getDias() { return dias; }
    public void setDias(Integer dias) { this.dias = dias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
