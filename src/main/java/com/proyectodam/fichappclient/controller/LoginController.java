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

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton, activarCuentaButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ComboBox<String> environmentSelector;

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

        if(email.isBlank() || password.isBlank()) {
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

            //Guardamos la sesión
            SessionManager.getInstance().setAccessToken(authService.getAccessToken());
            SessionManager.getInstance().setRefreshToken(authService.getRefreshToken());
            SessionManager.getInstance().setIdEmpleado(authService.getIdEmpleado());
            SessionManager.getInstance().setRole(authService.getRole());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            String role = loginResponseDTO.getRole();


            if (role.equalsIgnoreCase("Administrador")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
                stage.setScene(new Scene(loader.load(), 320, 240));
                stage.setMaximized(true);
                stage.show();
            } else if (role.equalsIgnoreCase("Usuario")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal-empleado.fxml"));
                stage.setScene(new Scene(loader.load(), 320, 240));
                System.out.println("Cargando vista empleado");
            } else {
                errorLabel.setText("Rol no conocido " + role);
                errorLabel.setVisible(true);
            }

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error en la autenticación");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleActivarCuenta() {
        if(usernameField == null || passwordField == null) {
            AlertUtils.mostrarError("Error", "Error interno, campos no cargados");
            errorLabel.setText("Error interno: campos no cargados");
            return;
        }
        String email = usernameField.getText();
        String password = passwordField.getText();

        if(email.isBlank() || password.isBlank()) {
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

            SessionManager.getInstance().setAccessToken(loginResponseDTO.getAccessToken());
            SessionManager.getInstance().setRefreshToken(loginResponseDTO.getRefreshToken());
            SessionManager.getInstance().setIdEmpleado(loginResponseDTO.getIdEmpleado());
            SessionManager.getInstance().setRole(loginResponseDTO.getRole());

            Stage stage = (Stage) activarCuentaButton.getScene().getWindow();
            String role = loginResponseDTO.getRole();

            if (role.equalsIgnoreCase("Administrador")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
                stage.setScene(new Scene(loader.load(), 320, 240));
            } else if (role.equalsIgnoreCase("Usuario")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal-empleado.fxml"));
                stage.setScene(new Scene(loader.load(), 320, 240));
                System.out.println("Cargando vista empleado");
            } else {
                AlertUtils.mostrarError("Error", "Rol no conocido");
                errorLabel.setText("Rol no conocido " + role);
                errorLabel.setVisible(true);
            }

        } catch (IOException e) {
            AlertUtils.mostrarError("Error", "Error activando cuenta");
            errorLabel.setText("Error activando cuenta");
            errorLabel.setVisible(true);
            throw new RuntimeException(e);
        }
    }

}
