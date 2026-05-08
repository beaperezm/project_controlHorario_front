package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDetalleDTO;
import com.proyectodam.fichappclient.service.AuthService;
import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ConfiguracionUsuarioController {

    @FXML private Label lblNombre;
    @FXML private Label lblApellidos;
    @FXML private Label lblEmail;
    @FXML private Label lblDni;
    @FXML private Label lblDepartamento;
    @FXML private Label lblRol;

    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMensajeSeguridad;
    @FXML private Button btnUpdatePassword;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Cargar datos
        cargarDatosUsuario();
        configurarValidaciones();
    }

    private void cargarDatosUsuario() {
        EmpleadoDTO usuarioSesion = SessionData.getInstance().getUsuarioLogueado();
        if (usuarioSesion == null) return;

        // Mostrar datos básicos de la sesión inmediatamente
        lblNombre.setText(usuarioSesion.getNombre());
        lblApellidos.setText(usuarioSesion.getApellidos());
        lblEmail.setText(usuarioSesion.getEmail());
        lblDni.setText(usuarioSesion.getDni() != null && !usuarioSesion.getDni().isEmpty() ? usuarioSesion.getDni() : "No disponible");
        lblRol.setText(usuarioSesion.getRol() != null ? usuarioSesion.getRol().toUpperCase() : "USUARIO");

        // Cargar datos extendidos desde la API de forma asíncrona
        new Thread(() -> {
            try {
                // Intentamos obtener el detalle completo
                EmpleadoDetalleDTO detalle = authService.getEmpleadoDetalle(usuarioSesion.getIdEmpleado());
                
                Platform.runLater(() -> {
                    if (detalle != null) {
                        if (detalle.getDni() != null && !detalle.getDni().isEmpty()) {
                            lblDni.setText(detalle.getDni());
                        }
                        lblDepartamento.setText(detalle.getDepartamento() != null ? detalle.getDepartamento() : "Sin asignar");
                        // Si el nombre completo viene en un solo campo, podemos usar el de sesión que ya es correcto
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblDni.setText("Error al cargar");
                    lblDepartamento.setText("Error al cargar");
                });
            }
        }).start();
    }

    private void configurarValidaciones() {
        // Listener para comprobar coincidencia de contraseñas mientras se escribe
        txtConfirmPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                ocultarMensaje();
            } else if (!newVal.equals(txtNewPassword.getText())) {
                mostrarMensaje("Las contraseñas no coinciden", "#e74c3c");
            } else {
                mostrarMensaje("Las contraseñas coinciden", "#27ae60");
            }
        });
    }

    @FXML
    private void handleUpdatePassword() {
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (newPass.isEmpty()) {
            mostrarMensaje("La contraseña no puede estar vacía", "#e74c3c");
            return;
        }

        if (newPass.length() < 6) {
            mostrarMensaje("Mínimo 6 caracteres requeridos", "#e74c3c");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            mostrarMensaje("Las contraseñas no coinciden", "#e74c3c");
            return;
        }

        // Feedback visual de carga
        btnUpdatePassword.setDisable(true);
        btnUpdatePassword.setText("Actualizando...");
        mostrarMensaje("Procesando...", "#3498db");

        new Thread(() -> {
            try {
                int idEmpleado = SessionData.getInstance().getUsuarioLogueado().getIdEmpleado();
                authService.cambiarPassword(idEmpleado, newPass);
                
                Platform.runLater(() -> {
                    mostrarMensaje("¡Contraseña actualizada con éxito!", "#27ae60");
                    txtNewPassword.clear();
                    txtConfirmPassword.clear();
                    btnUpdatePassword.setDisable(false);
                    btnUpdatePassword.setText("Actualizar Contraseña");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarMensaje("Error: No se pudo conectar con el servidor", "#e74c3c");
                    btnUpdatePassword.setDisable(false);
                    btnUpdatePassword.setText("Actualizar Contraseña");
                });
            }
        }).start();
    }

    private void mostrarMensaje(String texto, String colorHex) {
        lblMensajeSeguridad.setText(texto);
        lblMensajeSeguridad.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold;");
        lblMensajeSeguridad.setVisible(true);
        lblMensajeSeguridad.setManaged(true);
    }

    private void ocultarMensaje() {
        lblMensajeSeguridad.setVisible(false);
        lblMensajeSeguridad.setManaged(false);
    }

    @FXML
    private void handleBack() {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }
}
