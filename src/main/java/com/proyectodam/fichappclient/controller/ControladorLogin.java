package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.util.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ControladorLogin {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> environmentSelector;

    @FXML
    public void initialize() {
        environmentSelector.getItems().addAll("Localhost", "Server");
        environmentSelector.setValue("Localhost");

        environmentSelector.setOnAction(event -> {
            String selected = environmentSelector.getValue();
            if ("Localhost".equals(selected)) {
                AppConfig.getInstance().setBaseUrl("http://localhost:8080");
            } else if ("Server".equals(selected)) {
                AppConfig.getInstance().setBaseUrl("https://fichapp.duckdns.org/api");
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, complete todos los campos.");
            errorLabel.setVisible(true);
        } else {
            try {
                // TODO: Implementar autenticación real contra la API usando ApiClient
                // Por ahora, omitir inicio de sesión y navegar al diseño principal

                // Navegar al diseño principal (Panel de control o vista inicial)
                // Usando "dashboard" como vista inicial, o dejar por defecto
                ServicioNavegacion.getInstance().navigateToMainLayout("dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error al cargar la vista de inicio.");
                errorLabel.setVisible(true);
            }
        }
    }
}
