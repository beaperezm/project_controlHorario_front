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

    private LocalDate fechaAlta;
    private LocalDate fechaNacimiento;
    private DepartamentoDTO departamentoDTO;
    private RolDTO rolDTO;
    private HorarioDTO horarioDTO;
    private String departamento;
    private String rol;
    private String horario;

    private String imgPerfil;

    public String getImgPerfil() {
        return imgPerfil;
    }

    public void setImgPerfil(String imgPerfil) {
        this.imgPerfil = imgPerfil;
    }

  /*  private String departamento;
    private String rol;
    private LocalDate fechaAlta;
    private LocalDate fechaAltaSistema;
    private LocalDate fechaNacimiento;*/


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

 /*   public LocalDate getFechaAltaSistema() {
        return fechaAltaSistema;
    }

    public void setFechaAltaSistema(LocalDate fechaAltaSistema) {
        this.fechaAltaSistema = fechaAltaSistema;
    }*/

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

    public HorarioDTO getHorarioDTO() {
        return horarioDTO;
    }

    public void setHorarioDTO(HorarioDTO horarioDTO) {
        this.horarioDTO = horarioDTO;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

   public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

  /*  @Override
    public String toString() {
        if (idEmpleado == -1)
            return nombre != null ? nombre : "Todos";
        String n = nombre != null ? nombre : "";
        String a = apellidos != null ? apellidos : "";
        return (n + " " + a).trim();
        }*/
}
