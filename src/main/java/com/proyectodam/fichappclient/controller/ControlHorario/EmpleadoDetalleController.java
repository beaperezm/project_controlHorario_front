package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.EmpleadoDetalleDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.util.Objects;

public class EmpleadoDetalleController {

    @FXML
    private ImageView imagenPerfil;
    @FXML
    private Label nombreApellidos, email, direccion, telefono, departamento, rol, vacacionesTotales, vacacionesPendientes, horasExtra, horario, configuracionHorario;

    public void setEmpleadoDetalle(EmpleadoDetalleDTO empleadoDetalleDTO) {

        imagenPerfil.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/proyectodam/fichappclient/img/img_gneral.png"))));

        nombreApellidos.setText(empleadoDetalleDTO.getNombreApellidos());
        email.setText(empleadoDetalleDTO.getEmail());
        telefono.setText(empleadoDetalleDTO.getTelefono());
        direccion.setText(empleadoDetalleDTO.getDireccion());
        departamento.setText(empleadoDetalleDTO.getDepartamento());
        rol.setText(empleadoDetalleDTO.getRol());
        vacacionesTotales.setText(valor(empleadoDetalleDTO.getDiasVacacionesTotales()));
        vacacionesPendientes.setText(valor(empleadoDetalleDTO.getDiasVacacionesPendientes()));
        horasExtra.setText(valor(empleadoDetalleDTO.getHorasExtra()));
        horario.setText(valor(empleadoDetalleDTO.getHorario()));
        configuracionHorario.setText(valor(empleadoDetalleDTO.getConfiguracionHorario()));
    }

    private String valor(Object valor) {
        return valor == null ? "--" : valor.toString();
    }
}
