package com.proyectodam.fichappclient.controller.ControlHorario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichappclient.model.EmpleadoDetalleDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmpleadoDetalleController {

    @FXML
    private ImageView imagenPerfil;
    @FXML
    private Label nombreApellidos, email, direccion, telefono, departamento, rol, vacacionesTotales, vacacionesPendientes, horasExtra, horario, configuracionHorario;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setEmpleadoDetalle(EmpleadoDetalleDTO empleadoDetalleDTO) {
        if (empleadoDetalleDTO == null) return;

        // Cargar imagen de perfil si existe, si no la genérica
        imagenPerfil.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/proyectodam/fichappclient/img/img_gneral.png"))));

        nombreApellidos.setText(empleadoDetalleDTO.getNombreApellidos());
        email.setText(empleadoDetalleDTO.getEmail());
        telefono.setText(empleadoDetalleDTO.getTelefono());
        direccion.setText(empleadoDetalleDTO.getDireccion());
        departamento.setText(empleadoDetalleDTO.getDepartamento());
        rol.setText(empleadoDetalleDTO.getRol());
        
        vacacionesTotales.setText(String.valueOf(empleadoDetalleDTO.getDiasVacacionesTotales()));
        vacacionesPendientes.setText(String.valueOf(empleadoDetalleDTO.getDiasVacacionesPendientes()));
        
        // Formatear horas extra
        horasExtra.setText(formatearHorasDecimales(empleadoDetalleDTO.getHorasExtra()));
        
        horario.setText(valor(empleadoDetalleDTO.getHorario()));
        
        // Formatear JSON de configuración de horario
        configuracionHorario.setText(formatearJsonHorario(empleadoDetalleDTO.getConfiguracionHorario()));
    }

    private String formatearHorasDecimales(double horas) {
        boolean negativo = horas < 0;
        double horasAbs = Math.abs(horas);
        long segundosTotales = Math.round(horasAbs * 3600);
        
        long hh = segundosTotales / 3600;
        long mm = (segundosTotales % 3600) / 60;
        long ss = segundosTotales % 60;
        
        String tiempo = String.format("%02d:%02d:%02d", hh, mm, ss);
        return negativo ? "-" + tiempo : tiempo;
    }

    private String formatearJsonHorario(String json) {
        if (json == null || json.isBlank() || json.equals("--")) return "--";
        try {
            List<Map<String, String>> dias = objectMapper.readValue(json, new TypeReference<>() {});
            return dias.stream()
                    .map(d -> d.get("dia") + ": " + d.get("entrada") + " - " + d.get("salida"))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return json; // Si falla el parseo, mostrar el original
        }
    }

    private String valor(Object valor) {
        return valor == null || valor.toString().isBlank() ? "--" : valor.toString();
    }
}
