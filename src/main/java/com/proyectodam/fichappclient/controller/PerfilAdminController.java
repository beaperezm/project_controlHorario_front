package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDetalleDTO;
import com.proyectodam.fichappclient.service.AuthService;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class PerfilAdminController {

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
        cargarDatosAdmin();
        configurarValidaciones();
    }

    private void cargarDatosAdmin() {
        EmpleadoDTO admin = SessionData.getInstance().getUsuarioLogueado();
        if (admin == null) return;

        // Datos básicos disponibles desde la sesión
        lblNombre.setText(admin.getNombre() != null ? admin.getNombre() : "-");
        lblApellidos.setText(admin.getApellidos() != null ? admin.getApellidos() : "-");
        lblEmail.setText(admin.getEmail() != null ? admin.getEmail() : "-");
        lblDni.setText(admin.getDni() != null ? admin.getDni() : "-");
        lblRol.setText(admin.getRol() != null ? admin.getRol().toUpperCase() : "ADMIN");

        // Datos extendidos (departamento) desde la API de forma asíncrona
        new Thread(() -> {
            try {
                EmpleadoDetalleDTO detalle = authService.getEmpleadoDetalle(admin.getIdEmpleado());
                Platform.runLater(() -> {
                    if (detalle != null) {
                        lblDepartamento.setText(
                            detalle.getDepartamento() != null ? detalle.getDepartamento() : "Sin asignar");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblDepartamento.setText("No disponible"));
            }
        }).start();
    }

    private void configurarValidaciones() {
        txtConfirmPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                ocultarMensaje();
            } else if (!newVal.equals(txtNewPassword.getText())) {
                mostrarMensaje("Las contraseñas no coinciden", "#e74c3c");
            } else {
                mostrarMensaje("Las contraseñas coinciden ✓", "#27ae60");
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

        btnUpdatePassword.setDisable(true);
        btnUpdatePassword.setText("Actualizando...");
        mostrarMensaje("Procesando...", "#3498db");

        new Thread(() -> {
            try {
                int idAdmin = SessionData.getInstance().getUsuarioLogueado().getIdEmpleado();
                authService.cambiarPassword(idAdmin, newPass);

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
}
