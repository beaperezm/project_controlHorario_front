package com.proyectodam.fichappclient.model;

public class ContadorDTO {

    private int idEmpleado;
    private long segundosTranscurridos;

    public ContadorDTO() {}

    public ContadorDTO(int idEmpleado, long segundosTranscurridos) {
        this.idEmpleado = idEmpleado;
        this.segundosTranscurridos = segundosTranscurridos;
    }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public long getSegundosTranscurridos() { return segundosTranscurridos; }
    public void setSegundosTranscurridos(long segundosTranscurridos) { this.segundosTranscurridos = segundosTranscurridos; }
}
