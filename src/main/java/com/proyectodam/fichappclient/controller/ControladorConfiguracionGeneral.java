package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import javafx.event.ActionEvent;
import com.proyectodam.fichappclient.config.ConfiguracionSistema;
import com.proyectodam.fichappclient.enums.ModoConexion;
import com.proyectodam.fichappclient.util.AppConfig;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorConfiguracionGeneral {

    @FXML
    private ComboBox<String> comboEntorno;
    @FXML
    private VBox panelLocal;
    @FXML
    private GridPane panelServidor;
    @FXML
    private VBox panelSupabase;
    @FXML
    private VBox panelSeguridad;

    @FXML
    private TextField txtServerHost;
    @FXML
    private TextField txtServerPuerto;
    @FXML
    private TextField txtServerCatalogo;
    @FXML
    private TextField txtServerUsuario;
    @FXML
    private PasswordField txtServerClave;

    @FXML
    private TextField txtSupaUrl;
    @FXML
    private PasswordField txtSupaKey;
    @FXML
    private PasswordField txtSupaDbPass;
    @FXML
    private TextField txtSupaDbHost;
    @FXML
    private ProgressIndicator progressMigration;

    @FXML
    private Label lblEstadoConexion;
    @FXML
    private Button btnGuardar;

    @FXML
    private TextField txtEmpresaNombre;
    @FXML
    private TextField txtEmpresaDireccion;
    @FXML
    private TextField txtEmpresaTelefono;
    @FXML
    private TextField txtEmpresaCorreo;
    @FXML
    private ImageView imgLogoEmpresa;
    @FXML
    private Label lblEstadoEmpresa;

    @FXML
    private PasswordField txtJwtSecret;

    private ConfiguracionSistema config;
    private final com.proyectodam.fichappclient.service.EmpresaService empresaService = new com.proyectodam.fichappclient.service.EmpresaService();

    @FXML
    public void initialize() {
        config = AppConfig.getInstance().getConfiguracionSistema();

        // 1. Carga desde Backend (API) de forma asíncrona
        lblEstadoEmpresa.setText("Cargando datos de empresa...");
        new Thread(() -> {
            try {
                com.proyectodam.fichappclient.model.EmpresaDTO emp = empresaService.obtenerConfiguracion();
                Platform.runLater(() -> {
                    txtEmpresaNombre.setText(emp.getNombre() != null ? emp.getNombre() : "");
                    txtEmpresaDireccion.setText(emp.getDireccion() != null ? emp.getDireccion() : "");
                    txtEmpresaTelefono.setText(emp.getTelefono() != null ? emp.getTelefono() : "");
                    txtEmpresaCorreo.setText(emp.getEmailContacto() != null ? emp.getEmailContacto() : "");
                    // Cargamos siempre desde el endpoint del servidor para que funcione en cualquier equipo
                    String logoUrl = AppConfig.getInstance().getBaseUrl() + "/empresa/logo?t=" + System.currentTimeMillis();
                    Image logo = new Image(logoUrl, true);
                    logo.errorProperty().addListener((obs, old, hasError) -> {
                        if (hasError) imgLogoEmpresa.setImage(null);
                    });
                    imgLogoEmpresa.setImage(logo);
                    lblEstadoEmpresa.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEstadoEmpresa.setText("Error al cargar la empresa desde el servidor");
                    lblEstadoEmpresa.setStyle("-fx-text-fill: red;");
                });
            }
        }).start();

        inicializarSeccionConectividad();
    }

    private void inicializarSeccionConectividad() {
        comboEntorno.setItems(FXCollections.observableArrayList(
                "Entorno Local",
                "Servidor Propio (Remoto/VPS)",
                "Supabase (Cloud)"));

        // Listener
        comboEntorno.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isLocal = "Entorno Local".equals(newVal);
            boolean isServer = "Servidor Propio (Remoto/VPS)".equals(newVal);
            boolean isSupa = "Supabase (Cloud)".equals(newVal);

            panelLocal.setVisible(isLocal);
            panelLocal.setManaged(isLocal);

            panelServidor.setVisible(isServer);
            panelServidor.setManaged(isServer);

            panelSupabase.setVisible(isSupa);
            panelSupabase.setManaged(isSupa);

            // El panel de seguridad se muestra si NO es local (Servidor o Supabase)
            panelSeguridad.setVisible(!isLocal);
            panelSeguridad.setManaged(!isLocal);

            limpiarMensajes();
        });

        // Cargar estado UI de Conectividad
        txtServerHost.setText(config.getServerHost());
        txtServerPuerto.setText(config.getServerPort());
        txtServerCatalogo.setText(config.getServerDb());
        txtServerUsuario.setText(config.getServerUser());
        txtServerClave.setText(config.getServerPass());

        txtSupaUrl.setText(config.getSupaUrl());
        txtSupaKey.setText(config.getSupaKey());
        txtSupaDbPass.setText(config.getSupaDbPass());
        txtSupaDbHost.setText(config.getSupaDbHost());
        txtJwtSecret.setText(config.getJwtSecret());

        ModoConexion modoActual = config.getModo();
        if (modoActual == ModoConexion.SERVIDOR) {
            comboEntorno.getSelectionModel().select("Servidor Propio (Remoto/VPS)");
        } else if (modoActual == ModoConexion.SUPABASE) {
            comboEntorno.getSelectionModel().select("Supabase (Cloud)");
        } else {
            comboEntorno.getSelectionModel().select("Entorno Local");
        }

        // Estado inicial del panel de seguridad
        boolean nonLocal = modoActual != ModoConexion.LOCAL;
        panelSeguridad.setVisible(nonLocal);
        panelSeguridad.setManaged(nonLocal);
    }

    @FXML
    private void handleGuardarConexion(ActionEvent event) {
        String seleccion = comboEntorno.getValue();
        ModoConexion modoAnterior = config.getModo();
        ModoConexion nuevoModo = ModoConexion.LOCAL;
        String nuevaUrl = null;

        // Guardar valores en DTO
        config.setServerHost(txtServerHost.getText().trim());
        config.setServerPort(txtServerPuerto.getText().trim());
        config.setServerDb(txtServerCatalogo.getText().trim());
        config.setServerUser(txtServerUsuario.getText().trim());
        config.setServerPass(txtServerClave.getText().trim());
        config.setSupaUrl(txtSupaUrl.getText().trim());
        config.setSupaKey(txtSupaKey.getText().trim());
        config.setSupaDbPass(txtSupaDbPass.getText().trim());
        config.setSupaDbHost(txtSupaDbHost.getText().trim());
        config.setJwtSecret(txtJwtSecret.getText().trim());

        if ("Servidor Propio (Remoto/VPS)".equals(seleccion)) {
            nuevoModo = ModoConexion.SERVIDOR;
            String host = config.getServerHost();
            String port = config.getServerPort();
            if (!host.isEmpty()) {
                nuevaUrl = "http://" + host + (port.isEmpty() ? "" : ":" + port);
            }
        } else if ("Supabase (Cloud)".equals(seleccion)) {
            nuevoModo = ModoConexion.SUPABASE;
            // La URL de Supabase es para la DB interna del backend, no para la API.
            // Mantenemos la URL por defecto (localhost) a menos que se especifique otra.
            nuevaUrl = null;
        }

        // Actualizar AppConfig (esto también guarda en archivo general)
        AppConfig.getInstance().actualizarConfiguracion(nuevoModo, nuevaUrl);

        // Notificar al backend del cambio
        notificarBackend(nuevoModo, nuevaUrl);

        lblEstadoConexion.setText("Configuración guardada correctamente.");
        lblEstadoConexion.setStyle("-fx-text-fill: #2E7D32;");

        // Mostrar alerta de reinicio si el modo de conexión ha cambiado
        if (modoAnterior != nuevoModo) {
            Alert reinicioAlert = new Alert(Alert.AlertType.WARNING);
            reinicioAlert.setTitle("Reinicio Necesario");
            reinicioAlert.setHeaderText("Se requiere reiniciar la aplicación");
            reinicioAlert.setContentText(
                    "Has cambiado el modo de conexión de \"" + modoAnterior.name() + "\" a \"" + nuevoModo.name()
                            + "\".\n\n" +
                            "Reinicia tanto el backend como el frontend para que los cambios se hagan efectivos.\n\n" +
                            "Si no reinicia, la aplicación seguirá usando la base de datos anterior.");
            reinicioAlert.showAndWait();
        }
    }

    @FXML
    private void handleInicializarDB(ActionEvent event) {
        if (txtSupaUrl.getText().isEmpty() || txtSupaDbPass.getText().isEmpty()) {
            lblEstadoConexion.setText("❌ Se requiere URL y Contraseña de DB de Supabase");
            lblEstadoConexion.setStyle("-fx-text-fill: #c0392b;");
            return;
        }

        progressMigration.setVisible(true);
        lblEstadoConexion.setText("⏳ Inicializando base de datos en Supabase...");
        lblEstadoConexion.setStyle("-fx-text-fill: #3498db;");

        new Thread(() -> {
            try {
                com.proyectodam.fichappclient.util.ApiClient apiClient = new com.proyectodam.fichappclient.util.ApiClient();
                apiClient.post("/configuracion/inicializar-db", "");

                Platform.runLater(() -> {
                    progressMigration.setVisible(false);
                    lblEstadoConexion.setText("✅ Base de datos inicializada con éxito.");
                    lblEstadoConexion.setStyle("-fx-text-fill: #27ae60;");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText("Inicialización Completada");
                    alert.setContentText(
                            "La estructura de la base de datos se ha creado correctamente en su proyecto de Supabase.");
                    alert.showAndWait();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressMigration.setVisible(false);
                    lblEstadoConexion.setText("❌ Error: " + e.getMessage());
                    lblEstadoConexion.setStyle("-fx-text-fill: #e74c3c;");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Inicialización");
                    alert.setHeaderText("Fallo al inicializar la base de datos");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }

    private void limpiarMensajes() {
        if (lblEstadoConexion != null)
            lblEstadoConexion.setText("");
        if (lblEstadoEmpresa != null)
            lblEstadoEmpresa.setText("");
    }

    private void notificarBackend(ModoConexion modo, String urlPersonalizada) {
        new Thread(() -> {
            try {
                com.proyectodam.fichappclient.util.ApiClient apiClient = new com.proyectodam.fichappclient.util.ApiClient();

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, String> payload = new java.util.HashMap<>();
                payload.put("modo", modo.name());
                payload.put("urlPersonalizada", urlPersonalizada != null ? urlPersonalizada : "");
                payload.put("supaUrl", config.getSupaUrl() != null ? config.getSupaUrl() : "");
                payload.put("supaKey", config.getSupaKey() != null ? config.getSupaKey() : "");
                payload.put("supaDbPass", config.getSupaDbPass() != null ? config.getSupaDbPass() : "");
                payload.put("supaDbUser", config.getSupaDbUser() != null ? config.getSupaDbUser() : "postgres");
                payload.put("supaDbName", config.getSupaDbName() != null ? config.getSupaDbName() : "postgres");
                payload.put("supaDbHost", config.getSupaDbHost() != null ? config.getSupaDbHost() : "");
                payload.put("jwtSecret", config.getJwtSecret() != null ? config.getJwtSecret() : "");
                
                String jsonRequest = mapper.writeValueAsString(payload);

                apiClient.put("/configuracion/conexion", jsonRequest);
            } catch (Exception e) {
                // Falla silenciosa para no bloquear al usuario
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleSeleccionarLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Logo de la Empresa");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            // Preview inmediato con la imagen local mientras se sube
            imgLogoEmpresa.setImage(new Image(file.toURI().toString()));
            lblEstadoEmpresa.setText("Subiendo logo al servidor...");
            lblEstadoEmpresa.setStyle("-fx-text-fill: #F57F17;");

            new Thread(() -> {
                try {
                    empresaService.subirLogo(file);
                    Platform.runLater(() -> {
                        // Recargar desde el servidor para confirmar la subida
                        String logoUrl = AppConfig.getInstance().getBaseUrl() + "/empresa/logo?t=" + System.currentTimeMillis();
                        Image logo = new Image(logoUrl, true);
                        logo.errorProperty().addListener((obs, old, hasError) -> {
                            if (hasError) imgLogoEmpresa.setImage(null);
                        });
                        imgLogoEmpresa.setImage(logo);
                        lblEstadoEmpresa.setText("Logo actualizado correctamente.");
                        lblEstadoEmpresa.setStyle("-fx-text-fill: #2E7D32;");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        lblEstadoEmpresa.setText("Error al subir el logo: " + e.getMessage());
                        lblEstadoEmpresa.setStyle("-fx-text-fill: #e74c3c;");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleGuardarDatosEmpresa(ActionEvent event) {
        lblEstadoEmpresa.setText("Guardando datos en el servidor...");
        lblEstadoEmpresa.setStyle("-fx-text-fill: #F57F17;");

        com.proyectodam.fichappclient.model.EmpresaDTO dto = new com.proyectodam.fichappclient.model.EmpresaDTO();
        dto.setNombre(txtEmpresaNombre.getText());
        dto.setDireccion(txtEmpresaDireccion.getText());
        dto.setTelefono(txtEmpresaTelefono.getText());
        dto.setEmailContacto(txtEmpresaCorreo.getText());
        // El logo se gestiona por separado con POST /empresa/logo, no se incluye aquí

        new Thread(() -> {
            try {
                empresaService.actualizarConfiguracion(dto);

                // Opcional: mantener backup en config si se quiere usar offline
                config.setEmpresaNombre(dto.getNombre());
                config.setEmpresaDireccion(dto.getDireccion());
                config.setEmpresaTelefono(dto.getTelefono());
                config.setEmpresaCorreo(dto.getEmailContacto());
                config.guardarEnArchivo();

                Platform.runLater(() -> {
                    lblEstadoEmpresa.setText("Datos de la empresa guardados correctamente en la base de datos.");
                    lblEstadoEmpresa.setStyle("-fx-text-fill: #2E7D32;");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEstadoEmpresa.setText("Error al guardar en el servidor: " + e.getMessage());
                    lblEstadoEmpresa.setStyle("-fx-text-fill: #e74c3c;");
                });
            }
        }).start();
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }
}
