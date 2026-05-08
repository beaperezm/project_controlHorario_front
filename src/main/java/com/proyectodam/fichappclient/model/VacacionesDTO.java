package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO completo de una solicitud de vacaciones, usado en la vista de administración. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacacionesDTO {

    private Integer idSolicitud;
    private EmpleadoDTO empleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasHabiles;
    private String tipo;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;
    private String comentarioGestor;
    
    private String nombreEmpleado;

    public VacacionesDTO() {}

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public EmpleadoDTO getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoDTO empleado) {
        this.empleado = empleado;
        // Desnormalización: nombreEmpleado se rellena aquí para simplificar el binding en la tabla
        if (empleado != null) {
            String n = empleado.getNombre() != null ? empleado.getNombre() : "";
            String a = empleado.getApellidos() != null ? empleado.getApellidos() : "";
            this.nombreEmpleado = (n + " " + a).trim();
        }
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getDiasHabiles() {
        return diasHabiles;
    }

    public void setDiasHabiles(Integer diasHabiles) {
        this.diasHabiles = diasHabiles;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getComentarioGestor() {
        return comentarioGestor;
    }

    public void setComentarioGestor(String comentarioGestor) {
        this.comentarioGestor = comentarioGestor;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }
}
