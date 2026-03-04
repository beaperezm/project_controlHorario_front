package com.proyectodam.fichappclient.enums;

/**
 * Enumerado para gestionar los perfiles de conexión en el cliente.
 * Define la URL base por defecto para cada entorno (Local, Servidor, Cloud).
 */
public enum ModoConexion {
    LOCAL("Local", "http://localhost:8080/api"),
    SERVIDOR("Servidor", "https://fichapp.duckdns.org/api"),
    SUPABASE("Supabase", null);

    private final String displayName;
    private final String urlDefecto;

    ModoConexion(String displayName, String urlDefecto) {
        this.displayName = displayName;
        this.urlDefecto = urlDefecto;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrlDefecto() {
        return urlDefecto;
    }

    public static ModoConexion fromString(String str) {
        for (ModoConexion modo : values()) {
            if (modo.name().equalsIgnoreCase(str) || modo.displayName.equalsIgnoreCase(str)) {
                return modo;
            }
        }
        return LOCAL; // Por defecto
    }
}
