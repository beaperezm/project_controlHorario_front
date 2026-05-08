package com.proyectodam.fichappclient.util;

/**
 * Singleton que almacena la sesión activa del usuario (tokens JWT, id y rol).
 * Los campos son volatile para garantizar visibilidad entre hilos de JavaFX y tareas en segundo plano.
 */
public class SessionManager {

    private static SessionManager instance;

    private volatile String accessToken;
    private volatile String refreshToken;
    private volatile int idEmpleado;
    private volatile String role;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getAccessToken() { 
        return accessToken; 
    }

    public void setAccessToken(String accessToken) { 
        this.accessToken = accessToken; 
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
