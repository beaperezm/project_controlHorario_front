package com.proyectodam.fichappclient.model;

public class EmpleadoDetalleDTO {

    private int idEmpleado;
    private String nombreApellidos;
    private String email;
    private String telefono;
    private String direccion;
    private String dni;
    private String departamento;
    private String rol;
    private int diasVacacionesTotales;
    private int diasVacacionesPendientes;
    private double horasExtra;
    private String horario;
    private String configuracionHorario;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreApellidos() {
        return nombreApellidos;
    }

    public void setNombreApellidos(String nombreApellidos) {
        this.nombreApellidos = nombreApellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getDiasVacacionesTotales() {
        return diasVacacionesTotales;
    }

    public void setDiasVacacionesTotales(int diasVacacionesTotales) {
        this.diasVacacionesTotales = diasVacacionesTotales;
    }

    public int getDiasVacacionesPendientes() {
        return diasVacacionesPendientes;
    }

    public void setDiasVacacionesPendientes(int diasVacacionesPendientes) {
        this.diasVacacionesPendientes = diasVacacionesPendientes;
    }

    public double getHorasExtra() {
        return horasExtra;
    }

    public void setHorasExtra(double horasExtra) {
        this.horasExtra = horasExtra;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getConfiguracionHorario() {
        return configuracionHorario;
    }

    public void setConfiguracionHorario(String configuracionHorario) {
        this.configuracionHorario = configuracionHorario;
    }
}
