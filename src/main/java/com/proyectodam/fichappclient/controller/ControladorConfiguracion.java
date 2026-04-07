package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.enums.ModoConexion;
import com.proyectodam.fichappclient.util.ApiClient;
import com.proyectodam.fichappclient.util.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador de la ventana de configuración de conexión.
 * Permite al usuario seleccionar entre modo local, servidor o personalizado
 * (Supabase).
 */
public class ControladorConfiguracion {

    @FXML
    private RadioButton radioLocal;
    @FXML
    private RadioButton radioServidor;
    @FXML
    private RadioButton radioSupabase;
    @FXML
    private TextField txtUrlPersonalizada;
    @FXML
    private Text txtAyuda;
    @FXML
    private Label lblModoActual;
    @FXML
    private Label lblUrlActual;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblMensaje;

    private ToggleGroup grupoModos;

    @FXML
    public void initialize() {
        // Configurar grupo de radio buttons
        grupoModos = new ToggleGroup();
        radioLocal.setToggleGroup(grupoModos);
        radioServidor.setToggleGroup(grupoModos);
        radioSupabase.setToggleGroup(grupoModos);

        // Cargar configuración actual
        cargarConfiguracionActual();

        // Listener para actualizar ayuda según el modo seleccionado
        grupoModos.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            actualizarAyuda();
        });
    }

    private void cargarConfiguracionActual() {
        AppConfig config = AppConfig.getInstance();
        ModoConexion modoActual = config.getModoActual();
        String urlPersonalizada = config.getUrlPersonalizada();

        // Seleccionar radio button correspondiente
        switch (modoActual) {
            case LOCAL:
                radioLocal.setSelected(true);
                break;
            case SERVIDOR:
                radioServidor.setSelected(true);
                break;
            case SUPABASE:
                radioSupabase.setSelected(true);
                break;
        }

        // Mostrar URL personalizada si existe
        if (urlPersonalizada != null && !urlPersonalizada.trim().isEmpty()) {
            txtUrlPersonalizada.setText(urlPersonalizada);
        }

        // Actualizar labels informativos
        lblModoActual.setText(modoActual.getDisplayName().toUpperCase());
        lblUrlActual.setText(config.getBaseUrl());
    }

    private void actualizarAyuda() {
        Toggle seleccionado = grupoModos.getSelectedToggle();

        if (seleccionado == radioLocal) {
            txtAyuda.setText("Se conectará a http://localhost:8080/api (o URL personalizada)");
        } else if (seleccionado == radioServidor) {
            txtAyuda.setText("Se conectará a https://fichapp.duckdns.org/api (o URL personalizada)");
        } else if (seleccionado == radioSupabase) {
            txtAyuda.setText("Ingresa la URL completa de tu API Supabase");
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            // Determinar modo seleccionado
            ModoConexion modoSeleccionado = obtenerModoSeleccionado();

            if (modoSeleccionado == null) {
                mostrarMensaje("Por favor, selecciona un modo de conexión", true);
                return;
            }

            // Obtener URL personalizada
            String urlPersonalizada = txtUrlPersonalizada.getText().trim();

            // Validar que si es Supabase, debe tener URL
            if (modoSeleccionado == ModoConexion.SUPABASE &&
                    (urlPersonalizada == null || urlPersonalizada.isEmpty())) {
                mostrarMensaje("Para Supabase debes ingresar una URL personalizada", true);
                return;
            }

            // Actualizar configuración
            AppConfig config = AppConfig.getInstance();
            config.actualizarConfiguracion(modoSeleccionado, urlPersonalizada);

            // Notificar al backend del cambio (opcional, no bloquea si falla)
            notificarBackend(modoSeleccionado, urlPersonalizada);

            // Actualizar labels informativos
            lblModoActual.setText(modoSeleccionado.getDisplayName().toUpperCase());
            lblUrlActual.setText(config.getBaseUrl());

            // Mostrar mensaje de éxito
            mostrarMensaje("✓ Configuración guardada. Reinicia la aplicación para aplicar cambios.", false);

            // Opcional: Cerrar ventana después de 2 segundos
            // new Thread(() -> {
            // try {
            // Thread.sleep(2000);
            // Platform.runLater(() -> cerrarVentana());
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // }).start();

        } catch (Exception e) {
            mostrarMensaje("Error al guardar configuración: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private ModoConexion obtenerModoSeleccionado() {
        if (radioLocal.isSelected()) {
            return ModoConexion.LOCAL;
        } else if (radioServidor.isSelected()) {
            return ModoConexion.SERVIDOR;
        } else if (radioSupabase.isSelected()) {
            return ModoConexion.SUPABASE;
        }
        return null;
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " +
                (esError ? "#FF5722" : "#4CAF50"));
    }

    /**
     * Notifica al backend del cambio de configuración
     * Esto es opcional pero permite que el backend también actualice su perfil
     */
    private void notificarBackend(ModoConexion modo, String urlPersonalizada) {
        try {
            ApiClient apiClient = new ApiClient();

            // Construir JSON manualmente
            String jsonRequest = String.format(
                    "{\"modo\":\"%s\",\"urlPersonalizada\":\"%s\"}",
                    modo.name(),
                    urlPersonalizada != null ? urlPersonalizada : "");

            // Intentar notificar al backend (no bloquear si falla)
            apiClient.putControladorConfiguracion("/configuracion/conexion", jsonRequest);

        } catch (Exception e) {
            // No mostrar error al usuario, ya que la configuración local ya se guardó
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
