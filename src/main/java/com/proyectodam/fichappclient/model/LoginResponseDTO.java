package com.proyectodam.fichappclient.model;

/** Respuesta del endpoint POST /auth/login. Contiene los tokens JWT y los datos básicos del usuario autenticado. */
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String email;
    private int idEmpleado;
    private String role;
    private String nombre;
    private String apellidos;
    private int idEmpresa;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, String refreshToken, String email, int idEmpleado,
                             String role, String nombre, String apellidos, int idEmpresa) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.idEmpleado = idEmpleado;
        this.role = role;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.idEmpresa = idEmpresa;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
}
