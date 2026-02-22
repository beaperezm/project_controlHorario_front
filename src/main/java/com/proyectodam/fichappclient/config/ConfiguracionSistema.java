package com.proyectodam.fichappclient.config;

import com.proyectodam.fichappclient.enums.ModoConexion;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Gestiona la carga y guardado de la configuración del sistema en un archivo de
 * propiedades local.
 * Permite guardar la URL del servidor y el modo de conexión seleccionado.
 */
public class ConfiguracionSistema {

    private static final String CONFIG_DIR = "config";
    private static final String CONFIG_FILE = "config.properties";
    private static final String MODO_KEY = "modo.conexion";
    private static final String URL_KEY = "url.personalizada";

    private ModoConexion modo;
    private String urlPersonalizada;

    public ConfiguracionSistema() {
        this.modo = ModoConexion.LOCAL; // Por defecto
        this.urlPersonalizada = "";
    }

    public void cargarDesdeArchivo() {
        try {
            Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);

            if (!Files.exists(configPath)) {
                // Archivo de configuración no encontrado, usando valores por defecto
                crearConfiguracionPorDefecto();
                return;
            }

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(configPath.toFile())) {
                props.load(fis);
            }

            String modoStr = props.getProperty(MODO_KEY, "LOCAL");
            this.modo = ModoConexion.fromString(modoStr);
            this.urlPersonalizada = props.getProperty(URL_KEY, "");

        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarEnArchivo() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            Properties props = new Properties();
            props.setProperty(MODO_KEY, this.modo.name());
            props.setProperty(URL_KEY, this.urlPersonalizada != null ? this.urlPersonalizada : "");

            Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);
            try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
                props.store(fos, "Configuración de conexión FichApp");
            }

        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearConfiguracionPorDefecto() {
        this.modo = ModoConexion.LOCAL;
        this.urlPersonalizada = "";
        guardarEnArchivo();
    }

    // Getters y Setters
    public ModoConexion getModo() {
        return modo;
    }

    public void setModo(ModoConexion modo) {
        this.modo = modo;
    }

    public String getUrlPersonalizada() {
        return urlPersonalizada;
    }

    public void setUrlPersonalizada(String urlPersonalizada) {
        this.urlPersonalizada = urlPersonalizada;
    }
}
