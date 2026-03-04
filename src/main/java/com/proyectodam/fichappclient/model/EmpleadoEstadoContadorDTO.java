package com.proyectodam.fichappclient.model;

public class EmpleadoEstadoContadorDTO {

    private long totalEmpleados;
    private long activos;
    private long inactivos;
    private long bajaMedica;
    private long excedencia;

    public long getActivos() {
        return activos;
    }

    public void setActivos(long activos) {
        this.activos = activos;
    }

    public long getInactivos() {
        return inactivos;
    }

    public void setInactivos(long inactivos) {
        this.inactivos = inactivos;
    }

    public long getBajaMedica() {
        return bajaMedica;
    }

    public void setBajaMedica(long bajaMedica) {
        this.bajaMedica = bajaMedica;
    }

    public long getExcedencia() {
        return excedencia;
    }

    public void setExcedencia(long excedencia) {
        this.excedencia = excedencia;
    }

    public long getTotalEmpleados() {
        return totalEmpleados;
    }

    public void setTotalEmpleados(long totalEmpleados) {
        this.totalEmpleados = totalEmpleados;
    }
}
