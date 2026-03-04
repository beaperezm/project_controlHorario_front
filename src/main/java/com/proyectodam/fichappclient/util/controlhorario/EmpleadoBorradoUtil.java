package com.proyectodam.fichappclient.util.controlhorario;

import com.proyectodam.fichappclient.controller.ControlHorario.ControlHorarioEmpleadorEmpleadoController;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class EmpleadoBorradoUtil {

    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService;
    private final ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService;

    public EmpleadoBorradoUtil(ControlHorarioEmpleadorService controlHorarioEmpleadorService, ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService) {
        this.controlHorarioEmpleadorService = controlHorarioEmpleadorService;
        this.controlHorarioEmpleadorEmpleadoService = controlHorarioEmpleadorEmpleadoService;
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

    public void borradoLogicoEmpleado(EmpleadoDTO empleadoDTO) {
        if(!confirmarBorradoEmpleado(empleadoDTO)) {
            return;
        }

        try {

            controlHorarioEmpleadorEmpleadoService.borradoLogicoEmpleado(empleadoDTO.getIdEmpleado());

       /*     Platform.runLater(() -> {
                actualizarTabla.run();
                AlertUtils.mostrarConfirmacion("Confirmación", "Empleado dado de baja correctamente");

            });*/
        } catch (Exception e) {
            AlertUtils.mostrarError("Error", "No se pudo cambiar el estado del empleado a inactivo");
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
