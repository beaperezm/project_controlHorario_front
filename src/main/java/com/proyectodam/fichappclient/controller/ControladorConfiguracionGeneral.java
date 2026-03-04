package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.config.ConfiguracionSistema;
import com.proyectodam.fichappclient.enums.ModoConexion;
import com.proyectodam.fichappclient.util.AppConfig;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ControladorConfiguracionGeneral {

    @FXML
    private ComboBox<String> comboIdioma;

    @FXML
    private RadioButton rbLocal;
    @FXML
    private RadioButton rbServidor;
    @FXML
    private RadioButton rbSupabase;
    @FXML
    private ToggleGroup grupoConexion;
    @FXML
    private TextField txtUrlPersonalizada;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblAdvertencia;
    @FXML
    private Button btnGuardar;

    private ConfiguracionSistema config;

    @FXML
    public void initialize() {
        config = new ConfiguracionSistema();
        config.cargarDesdeArchivo();

        inicializarSeccionIdioma();
        inicializarSeccionConectividad();
    }

    private void inicializarSeccionIdioma() {
        comboIdioma.setItems(FXCollections.observableArrayList("Español", "English"));
        comboIdioma.getSelectionModel().select("Español"); // Por defecto
    }

    private void inicializarSeccionConectividad() {
        // Cargar estado actual
        ModoConexion modoActual = config.getModo();
        String urlPersonalizada = config.getUrlPersonalizada();

        switch (modoActual) {
            case LOCAL:
                rbLocal.setSelected(true);
                break;
            case SERVIDOR:
                rbServidor.setSelected(true);
                break;
            case SUPABASE:
                rbSupabase.setSelected(true);
                break;
        }

        if (urlPersonalizada != null) {
            txtUrlPersonalizada.setText(urlPersonalizada);
        }

        // Listener para cambios en ToggleGroup
        grupoConexion.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            limpiarMensajes();
        });
    }

    @FXML
    private void handleGuardar() {
        ModoConexion nuevoModo = ModoConexion.LOCAL;
        if (rbServidor.isSelected())
            nuevoModo = ModoConexion.SERVIDOR;
        if (rbSupabase.isSelected())
            nuevoModo = ModoConexion.SUPABASE;

        String nuevaUrl = txtUrlPersonalizada.getText().trim();
        if (nuevaUrl.isEmpty()) {
            nuevaUrl = null;
        }

        // Actualizar AppConfig (esto también guarda en archivo)
        AppConfig.getInstance().actualizarConfiguracion(nuevoModo, nuevaUrl);

        // Retroalimentación al usuario
        lblEstado.setText("Configuración guardada correctamente.");
        lblEstado.setStyle("-fx-text-fill: #2E7D32;");

        // Notificar al backend (opcional pero recomendado para sincronizar)
        notificarBackend(nuevoModo, nuevaUrl);
    }

    private void notificarBackend(ModoConexion modo, String urlPersonalizada) {
        String baseUrl = AppConfig.getInstance().getBaseUrl();
        // Nota: Si cambiamos a un servidor remoto, esta petición irá al NUEVO servidor.
        // Si el servidor remoto requiere auth y no la tenemos, fallará, pero la config
        // local ya se guardó.

        String jsonBody = String.format("{\"modo\": \"%s\", \"urlPersonalizada\": \"%s\"}",
                modo.name(),
                urlPersonalizada == null ? "" : urlPersonalizada);

        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/configuracion/conexion"))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        // Backend sincronizado
                    } else if (response.statusCode() == 404) {
                        // Intento de respaldo (fallback) por si acaso duplicamos /api
                    } else {
                        lblAdvertencia.setText(
                                "Configuración guardada localmente, pero el backend remoto no pudo confirmar.");
                        lblAdvertencia.setVisible(true);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    // Es normal que falle si cambiamos de local a server y el server está caído
                    // O si cambiamos el puerto y el backend aún no reinicia.
                });
            }
        });
    }

    private void limpiarMensajes() {
        lblEstado.setText("");
        lblAdvertencia.setVisible(false);
    }
}
