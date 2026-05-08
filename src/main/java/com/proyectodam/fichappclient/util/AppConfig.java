package com.proyectodam.fichappclient.util;

import com.proyectodam.fichappclient.config.ConfiguracionSistema;
import com.proyectodam.fichappclient.enums.ModoConexion;

/**
 * Singleton que mantiene el estado de la configuración global de la aplicación
 * en memoria.
 * Actúa como punto central de acceso a la URL base y modo de conexión actuales.
 */
public class AppConfig {

    private static AppConfig instance;
    private String baseUrl;
    private ConfiguracionSistema configuracionSistema;

    private AppConfig() {
        // Cargar configuración desde archivo
        configuracionSistema = new ConfiguracionSistema();
        configuracionSistema.cargarDesdeArchivo();

        // Determinar URL base según el modo configurado
        this.baseUrl = determinarUrlBase();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private String determinarUrlBase() {
        ModoConexion modo = configuracionSistema.getModo();
        String urlPersonalizada = configuracionSistema.getUrlPersonalizada();

        // Si hay URL personalizada, usarla
        if (urlPersonalizada != null && !urlPersonalizada.trim().isEmpty()) {
            return urlPersonalizada;
        }

        // Si no, usar la URL por defecto del modo
        return modo.getUrlDefecto();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void actualizarConfiguracion(ModoConexion nuevoModo, String urlPersonalizada) {
        configuracionSistema.setModo(nuevoModo);
        configuracionSistema.setUrlPersonalizada(urlPersonalizada);
        configuracionSistema.guardarEnArchivo();

        // Actualizar URL base
        this.baseUrl = determinarUrlBase();
    }

    public ModoConexion getModoActual() {
        return configuracionSistema.getModo();
    }

    public String getUrlPersonalizada() {
        return configuracionSistema.getUrlPersonalizada();
    }
    
    public ConfiguracionSistema getConfiguracionSistema() {
        return configuracionSistema;
    }
}
