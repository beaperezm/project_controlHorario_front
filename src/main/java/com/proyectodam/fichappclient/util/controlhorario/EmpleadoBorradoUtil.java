package com.proyectodam.fichappclient.util.controlhorario;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class EmpleadoBorradoUtil {

    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService;

    public EmpleadoBorradoUtil(ControlHorarioEmpleadorService controlHorarioEmpleadorService) {
        this.controlHorarioEmpleadorService = controlHorarioEmpleadorService;
    }


    public void borradoFisicoEmpleado (EmpleadoDTO empleadoDTO, ObservableList<EmpleadoDTO> listaEmpleados) {
        if(!confirmarBorradoEmpleado(empleadoDTO)) {
            return;
        }
        try {
            controlHorarioEmpleadorService.borrarEmpleado(empleadoDTO.getIdEmpleado());
            listaEmpleados.remove(empleadoDTO);
        } catch (Exception e) {
            AlertUtils.mostrarError("Error","No se ha podido eliminar al empleado");
            throw new RuntimeException(e);
        }
    }


    private boolean confirmarBorradoEmpleado(EmpleadoDTO empleadoDTO) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmando eliminación empleado");
        alert.setHeaderText(null);
        alert.setContentText(String.format("¿Seguro que quieres eliminar al empleado seleccionado: %s?", empleadoDTO.getNombre()));
        Optional<ButtonType> resultado = alert.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
