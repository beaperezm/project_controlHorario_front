package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.DepartamentoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.EstadoDTO;
import com.proyectodam.fichappclient.model.RolDTO;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.SessionData;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoBorradoUtil;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoTablaUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ControlHorarioEmpleadorEmpleadoController {

    @FXML
    private TextField buscarPorNombre;
    @FXML
    private ComboBox<DepartamentoDTO> filtrarDepartamento;
    @FXML
    private ComboBox<RolDTO> filtrarRol;
    @FXML
    private ComboBox<EstadoDTO> filtrarEstado;
    @FXML
    private TableView<EmpleadoDTO> tablaEmpleados;
    @FXML
    private TableColumn<EmpleadoDTO, String> columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado;
    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService = new ControlHorarioEmpleadorService();
    private final ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService = new ControlHorarioEmpleadorEmpleadoService();

    private final ObservableList<EmpleadoDTO> listadoEmpleados = FXCollections.observableArrayList();

    private EmpleadoBorradoUtil empleadoBorradoUtil;

    @FXML
    public void initialize() {
        empleadoBorradoUtil = new EmpleadoBorradoUtil(controlHorarioEmpleadorService, controlHorarioEmpleadorEmpleadoService);
        cargarDepartamentos();
        cargarRoles();

        EmpleadoTablaUtil.tablaEmpleado(tablaEmpleados, listadoEmpleados ,columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta,columnaParaFechaNacimiento, columnaParaEstado);

        cargarEmpleados();

    }

    private void cargarDepartamentos()  {
        try {
            filtrarDepartamento.getItems().setAll(controlHorarioEmpleadorService.getAllDepartamentos());

            //Para que muestre el nombre (no el objeto)
            filtrarDepartamento.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(DepartamentoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre departamento en el 'placeholder'
            filtrarDepartamento.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DepartamentoDTO departamentoDTO, boolean estaVacio) {
                    super.updateItem(departamentoDTO, estaVacio);
                    setText((departamentoDTO == null) ? "Departamento" : departamentoDTO.getNombre());
                }
            });

            filtrarDepartamento.getSelectionModel().clearSelection();
            filtrarDepartamento.setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los departamentos");
        }

    }

    private void cargarRoles() {
        try {
            filtrarRol.getItems().setAll(controlHorarioEmpleadorService.getAllRoles());

            //Para que muestre el nombre (no el objeto)
            filtrarRol.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(RolDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre departamento en el 'placeholder'
            filtrarRol.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(RolDTO rolDTO, boolean estaVacio) {
                    super.updateItem(rolDTO, estaVacio);
                    setText((rolDTO == null) ? "Rol" : rolDTO.getNombre());
                }
            });


            filtrarRol.getSelectionModel().clearSelection();
            filtrarRol.setEditable(false);


        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los roles");
        }
    }

    private void cargarEmpleados() {

        new Thread(() -> {
            try {
                List<EmpleadoDTO> listaEmpleados = controlHorarioEmpleadorEmpleadoService.getAllEmpleados();

                Platform.runLater(() ->
                    listadoEmpleados.setAll(listaEmpleados)
                );
            } catch (Exception e) {

                Platform.runLater(() -> {
                    AlertUtils.mostrarError("Error", "No se pueden cargar los empleados");
                });

                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    private void botonEliminarEmpleado(ActionEvent event) {

        EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();
        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        empleadoBorradoUtil.borradoLogicoEmpleado(empleadoDTO);

        cargarEmpleados();


    }
}
