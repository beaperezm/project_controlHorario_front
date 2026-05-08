package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controlador para el Dashboard Visual simplificado destinado a usuarios
 * estándar.
 */
public class UserDashboardController {

    @FXML
    private Label clockLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label footerLabel;

    @FXML
    public void initialize() {
        initClock();
        updateUserInfo();
    }

    private void initClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES"));

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(now.format(timeFormatter));
            // Capitalizar la primera letra del día
            String dateText = now.format(dateFormatter);
            dateLabel.setText(dateText.substring(0, 1).toUpperCase() + dateText.substring(1));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateUserInfo() {
        var user = SessionData.getInstance().getUsuarioLogueado();
        if (user != null) {
            footerLabel.setText("Sesión iniciada como: " + user.getNombre() + " " + user.getApellidos());
        }
    }

    @FXML
    private void handleQuiosco() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleado.fxml");
    }

    @FXML
    private void handleDocumentacion() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/nominas-view.fxml");
    }

    @FXML
    private void handleVacaciones() {
        ServicioNavegacion.getInstance()
                .navigateTo("/com/proyectodam/fichappclient/views/vacaciones/vacaciones-empleado-view.fxml");
    }

    @FXML
    private void handleConfiguracion() {
        ServicioNavegacion.getInstance().navigateTo("/com/proyectodam/fichappclient/views/configuracion-usuario.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionData.getInstance().clearSession();
        ServicioNavegacion.getInstance().logout();
    }
}
