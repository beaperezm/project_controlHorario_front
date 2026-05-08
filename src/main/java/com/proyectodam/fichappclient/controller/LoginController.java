package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.LoginResponseDTO;
import com.proyectodam.fichappclient.service.AuthService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.SessionData;
import com.proyectodam.fichappclient.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton, activarCuentaButton;
    @FXML private Label errorLabel;
    @FXML private ComboBox<String> environmentSelector;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        activarCuentaButton.setOnAction(e -> handleActivarCuenta());
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Email y contraseña no pueden estar vacíos");
            errorLabel.setVisible(true);
            return;
        }

        try {
            LoginResponseDTO loginResponseDTO = authService.login(email, password);

            EmpleadoDTO empleadoDTO = new EmpleadoDTO();
            empleadoDTO.setIdEmpleado(loginResponseDTO.getIdEmpleado());
            empleadoDTO.setEmail(loginResponseDTO.getEmail());
            empleadoDTO.setNombre(loginResponseDTO.getNombre());
            empleadoDTO.setApellidos(loginResponseDTO.getApellidos());
            empleadoDTO.setRol(loginResponseDTO.getRole());

            SessionData.getInstance().setEmpleadoLogueado(empleadoDTO);
            
            String tokenRecibido = loginResponseDTO.getAccessToken();
            
            SessionManager sm = SessionManager.getInstance();
            sm.setAccessToken(tokenRecibido);
            sm.setRefreshToken(loginResponseDTO.getRefreshToken());
            sm.setIdEmpleado(loginResponseDTO.getIdEmpleado());
            sm.setRole(loginResponseDTO.getRole());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            String role = loginResponseDTO.getRole();

            if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("Administrador")
                    || role.equalsIgnoreCase("MANAGER") || role.equalsIgnoreCase("RRHH")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
                Scene scene = new Scene(loader.load(), 1024, 720);
                cargarCssEnScene(scene);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } else if (role.equalsIgnoreCase("USER") || role.equalsIgnoreCase("Usuario")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/proyectodam/fichappclient/views/estructura-principal-empleado.fxml"));
                Scene scene = new Scene(loader.load(), 1024, 720);
                cargarCssEnScene(scene);
                stage.setScene(scene);
                stage.show();
            } else {
                errorLabel.setText("Rol no reconocido: " + role);
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Email o contraseña incorrectos");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleActivarCuenta() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Email y contraseña no pueden estar vacíos");
            errorLabel.setVisible(true);
            return;
        }

        try {
            LoginResponseDTO loginResponseDTO = authService.activarCuenta(email, password);

            EmpleadoDTO empleadoDTO = new EmpleadoDTO();
            empleadoDTO.setIdEmpleado(loginResponseDTO.getIdEmpleado());
            empleadoDTO.setEmail(loginResponseDTO.getEmail());
            empleadoDTO.setNombre(loginResponseDTO.getNombre());
            empleadoDTO.setApellidos(loginResponseDTO.getApellidos());
            empleadoDTO.setRol(loginResponseDTO.getRole());

            SessionData.getInstance().setEmpleadoLogueado(empleadoDTO);
            
            String tokenRecibido = loginResponseDTO.getAccessToken();
            
            SessionManager sm = SessionManager.getInstance();
            sm.setAccessToken(tokenRecibido);
            sm.setRefreshToken(loginResponseDTO.getRefreshToken());
            sm.setIdEmpleado(loginResponseDTO.getIdEmpleado());
            sm.setRole(loginResponseDTO.getRole());

            Stage stage = (Stage) activarCuentaButton.getScene().getWindow();
            String role = loginResponseDTO.getRole();

            if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("Administrador")
                    || role.equalsIgnoreCase("MANAGER") || role.equalsIgnoreCase("RRHH")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
                Scene scene = new Scene(loader.load(), 1024, 720);
                cargarCssEnScene(scene);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } else if (role.equalsIgnoreCase("USER") || role.equalsIgnoreCase("Usuario")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/proyectodam/fichappclient/views/estructura-principal-empleado.fxml"));
                Scene scene = new Scene(loader.load(), 1024, 720);
                cargarCssEnScene(scene);
                stage.setScene(scene);
                stage.show();
            } else {
                errorLabel.setText("Rol no reconocido: " + role);
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            AlertUtils.mostrarError("Error", "Error activando cuenta");
            errorLabel.setText("Error activando cuenta");
            errorLabel.setVisible(true);
        }
    }
    /**
     * Carga todos los módulos CSS en una Scene.
     * JavaFX no soporta @import en CSS, por lo que main.css no propaga sus @import.
     * Hay que cargar cada módulo explícitamente.
     */
    private void cargarCssEnScene(Scene scene) {
        String base = "/com/proyectodam/fichappclient/styles/";
        String[] modulos = {
            "variables.css", "tipografia.css", "botones.css", "navegacion.css",
            "formularios.css", "contenedores.css", "tablas.css", "calendario.css",
            "utilidades.css", "dashboard_usuario.css"
        };
        for (String mod : modulos) {
            var resource = getClass().getResource(base + mod);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        }
    }
}
