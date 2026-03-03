package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpleadoDTO {

    private int idEmpleado;
    private String nombre;
    private String apellidos;
    private String email;
    private String direccion;
    private String telefono;
    private String dni;
    private String estado;
    private LocalDate fechaAltaSistema;
    private LocalDate fechaNacimiento;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAltaSistema() {
        return fechaAltaSistema;
    }

    public void setFechaAltaSistema(LocalDate fechaAltaSistema) {
        this.fechaAltaSistema = fechaAltaSistema;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        if (idEmpleado == -1)
            return nombre != null ? nombre : "Todos";
        String n = nombre != null ? nombre : "";
        String a = apellidos != null ? apellidos : "";
        return (n + " " + a).trim();
    }
}
