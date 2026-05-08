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

    // Claves de empresa
    private static final String EMPRESA_NOMBRE_KEY = "empresa.nombre";
    private static final String EMPRESA_DIRECCION_KEY = "empresa.direccion";
    private static final String EMPRESA_TELEFONO_KEY = "empresa.telefono";
    private static final String EMPRESA_CORREO_KEY = "empresa.correo";
    private static final String EMPRESA_LOGO_KEY = "empresa.logo";

    // Claves de servidor y Supabase
    private static final String SERVER_HOST_KEY = "server.host";
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String SERVER_DB_KEY = "server.db";
    private static final String SERVER_USER_KEY = "server.user";
    private static final String SERVER_PASS_KEY = "server.pass";
    private static final String SUPA_URL_KEY = "supa.url";
    private static final String SUPA_KEY_KEY = "supa.key";
    private static final String SUPA_DB_PASS_KEY = "supa.db.pass";
    private static final String SUPA_DB_USER_KEY = "supa.db.user";
    private static final String SUPA_DB_NAME_KEY = "supa.db.name";
    private static final String SUPA_DB_HOST_KEY = "supa.db.host";
    private static final String JWT_SECRET_KEY = "jwt.secret";

    private ModoConexion modo;
    private String urlPersonalizada;

    // Campos de empresa
    private String empresaNombre;
    private String empresaDireccion;
    private String empresaTelefono;
    private String empresaCorreo;
    private String empresaLogo;

    // Campos de conectividad
    private String serverHost;
    private String serverPort;
    private String serverDb;
    private String serverUser;
    private String serverPass;
    private String supaUrl;
    private String supaKey;
    private String supaDbPass;
    private String supaDbUser;
    private String supaDbName;
    private String supaDbHost;
    private String jwtSecret;

    public ConfiguracionSistema() {
        this.modo = ModoConexion.LOCAL; // Por defecto
        this.urlPersonalizada = "";
        this.empresaNombre = "Mi Empresa S.L.";
        this.empresaDireccion = "Calle Ejemplo 123";
        this.empresaTelefono = "912 345 678";
        this.empresaCorreo = "contacto@miempresa.com";
        this.empresaLogo = "";

        this.serverHost = "";
        this.serverPort = "8081";
        this.serverDb = "fichapp_db";
        this.serverUser = "admin";
        this.serverPass = "";
        this.supaUrl = "";
        this.supaKey = "";
        this.supaDbPass = "";
        this.supaDbUser = "postgres";
        this.supaDbName = "postgres";
        this.supaDbHost = "";
        this.jwtSecret = "";
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

            this.empresaNombre = props.getProperty(EMPRESA_NOMBRE_KEY, "Mi Empresa S.L.");
            this.empresaDireccion = props.getProperty(EMPRESA_DIRECCION_KEY, "Calle Ejemplo 123");
            this.empresaTelefono = props.getProperty(EMPRESA_TELEFONO_KEY, "912 345 678");
            this.empresaCorreo = props.getProperty(EMPRESA_CORREO_KEY, "contacto@miempresa.com");
            this.empresaLogo = props.getProperty(EMPRESA_LOGO_KEY, "");

            this.serverHost = props.getProperty(SERVER_HOST_KEY, "");
            this.serverPort = props.getProperty(SERVER_PORT_KEY, "8081");
            this.serverDb = props.getProperty(SERVER_DB_KEY, "fichapp_db");
            this.serverUser = props.getProperty(SERVER_USER_KEY, "admin");
            this.serverPass = props.getProperty(SERVER_PASS_KEY, "");
            this.supaUrl = props.getProperty(SUPA_URL_KEY, "");
            this.supaKey = props.getProperty(SUPA_KEY_KEY, "");
            this.supaDbPass = props.getProperty(SUPA_DB_PASS_KEY, "");
            this.supaDbUser = props.getProperty(SUPA_DB_USER_KEY, "postgres");
            this.supaDbName = props.getProperty(SUPA_DB_NAME_KEY, "postgres");
            this.supaDbHost = props.getProperty(SUPA_DB_HOST_KEY, "");
            this.jwtSecret = props.getProperty(JWT_SECRET_KEY, "");

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

            props.setProperty(EMPRESA_NOMBRE_KEY, this.empresaNombre != null ? this.empresaNombre : "");
            props.setProperty(EMPRESA_DIRECCION_KEY, this.empresaDireccion != null ? this.empresaDireccion : "");
            props.setProperty(EMPRESA_TELEFONO_KEY, this.empresaTelefono != null ? this.empresaTelefono : "");
            props.setProperty(EMPRESA_CORREO_KEY, this.empresaCorreo != null ? this.empresaCorreo : "");
            props.setProperty(EMPRESA_LOGO_KEY, this.empresaLogo != null ? this.empresaLogo : "");

            props.setProperty(SERVER_HOST_KEY, this.serverHost != null ? this.serverHost : "");
            props.setProperty(SERVER_PORT_KEY, this.serverPort != null ? this.serverPort : "");
            props.setProperty(SERVER_DB_KEY, this.serverDb != null ? this.serverDb : "");
            props.setProperty(SERVER_USER_KEY, this.serverUser != null ? this.serverUser : "");
            props.setProperty(SERVER_PASS_KEY, this.serverPass != null ? this.serverPass : "");
            props.setProperty(SUPA_URL_KEY, this.supaUrl != null ? this.supaUrl : "");
            props.setProperty(SUPA_KEY_KEY, this.supaKey != null ? this.supaKey : "");
            props.setProperty(SUPA_DB_PASS_KEY, this.supaDbPass != null ? this.supaDbPass : "");
            props.setProperty(SUPA_DB_USER_KEY, this.supaDbUser != null ? this.supaDbUser : "postgres");
            props.setProperty(SUPA_DB_NAME_KEY, this.supaDbName != null ? this.supaDbName : "postgres");
            props.setProperty(SUPA_DB_HOST_KEY, this.supaDbHost != null ? this.supaDbHost : "");
            props.setProperty(JWT_SECRET_KEY,
                    this.jwtSecret != null ? this.jwtSecret : "");

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
        this.jwtSecret = "";
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

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getEmpresaDireccion() {
        return empresaDireccion;
    }

    public void setEmpresaDireccion(String empresaDireccion) {
        this.empresaDireccion = empresaDireccion;
    }

    public String getEmpresaTelefono() {
        return empresaTelefono;
    }

    public void setEmpresaTelefono(String empresaTelefono) {
        this.empresaTelefono = empresaTelefono;
    }

    public String getEmpresaCorreo() {
        return empresaCorreo;
    }

    public void setEmpresaCorreo(String empresaCorreo) {
        this.empresaCorreo = empresaCorreo;
    }

    public String getEmpresaLogo() {
        return empresaLogo;
    }

    public void setEmpresaLogo(String empresaLogo) {
        this.empresaLogo = empresaLogo;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerDb() {
        return serverDb;
    }

    public void setServerDb(String serverDb) {
        this.serverDb = serverDb;
    }

    public String getServerUser() {
        return serverUser;
    }

    public void setServerUser(String serverUser) {
        this.serverUser = serverUser;
    }

    public String getServerPass() {
        return serverPass;
    }

    public void setServerPass(String serverPass) {
        this.serverPass = serverPass;
    }

    public String getSupaUrl() {
        return supaUrl;
    }

    public void setSupaUrl(String supaUrl) {
        this.supaUrl = supaUrl;
    }

    public String getSupaKey() {
        return supaKey;
    }

    public void setSupaKey(String supaKey) {
        this.supaKey = supaKey;
    }

    public String getSupaDbPass() {
        return supaDbPass;
    }

    public void setSupaDbPass(String supaDbPass) {
        this.supaDbPass = supaDbPass;
    }

    public String getSupaDbUser() {
        return supaDbUser;
    }

    public void setSupaDbUser(String supaDbUser) {
        this.supaDbUser = supaDbUser;
    }

    public String getSupaDbName() {
        return supaDbName;
    }

    public void setSupaDbName(String supaDbName) {
        this.supaDbName = supaDbName;
    }

    public String getSupaDbHost() {
        return supaDbHost;
    }

    public void setSupaDbHost(String supaDbHost) {
        this.supaDbHost = supaDbHost;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
}
