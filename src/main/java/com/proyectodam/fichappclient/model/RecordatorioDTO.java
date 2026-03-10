package com.proyectodam.fichappclient.model;

import java.time.LocalDate;

public class RecordatorioDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha; // En JavaFX usamos LocalDate
    private Integer idEmpleado;

    public RecordatorioDTO() {
    }

    public RecordatorioDTO(Long id, String titulo, String descripcion, LocalDate fecha, Integer idEmpleado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idEmpleado = idEmpleado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
