package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichappclient.model.DepartamentoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.RolDTO;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmpleadoFormularioEditarController {

    @FXML
    private TextField nombreTextField, apellidosTextField, correoTextField, direccionTextField, telefonoTextField, dniTextField;
    @FXML
    private ComboBox<String> estadoComboBox;
    @FXML
    private ComboBox<DepartamentoDTO> departamentoDTOComboBoxForm;
    @FXML
    private ComboBox<RolDTO> rolDTOComboBoxForm;
    @FXML
    private DatePicker fechaAltaDatePicker, fechaNacimientoDatePicker;

    private EmpleadoDTO empleadoDTO;
    private DepartamentoDTO departamentoDTO;
    private ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService;
    private ControlHorarioEmpleadorService controlHorarioEmpleadorService;
    private Stage stage;
    private TableView<EmpleadoDTO> tablaEmpleados;

    public void initData(EmpleadoDTO empleadoDTO, ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService, ControlHorarioEmpleadorService controlHorarioEmpleadorService,  ObservableList<String> estadoEmpleado, Stage stage, TableView<EmpleadoDTO> tablaEmpleados) {
        this.empleadoDTO = empleadoDTO;
        this.controlHorarioEmpleadorEmpleadoService = controlHorarioEmpleadorEmpleadoService;
        this.controlHorarioEmpleadorService = controlHorarioEmpleadorService;
        this.stage = stage;
        this.tablaEmpleados = tablaEmpleados;

        estadoComboBox.setItems(estadoEmpleado);

        try {
            departamentoDTOComboBoxForm.getItems().setAll(controlHorarioEmpleadorService.getAllDepartamentos());
            rolDTOComboBoxForm.getItems().setAll(controlHorarioEmpleadorService.getAllRoles());
        } catch (Exception e) {
            AlertUtils.mostrarError("Error", "No se han podido cargar los departamentos y/o roles");
            throw new RuntimeException(e);
        }


        cargarDatos();
    }

    private void cargarDatos() {
        nombreTextField.setText(empleadoDTO.getNombre());
        apellidosTextField.setText(empleadoDTO.getApellidos());
        correoTextField.setText(empleadoDTO.getEmail());
        direccionTextField.setText(empleadoDTO.getDireccion());
        telefonoTextField.setText(empleadoDTO.getTelefono());
        dniTextField.setText(empleadoDTO.getDni());
        fechaAltaDatePicker.setValue(empleadoDTO.getFechaAlta());
        fechaNacimientoDatePicker.setValue(empleadoDTO.getFechaNacimiento());
        estadoComboBox.setValue(empleadoDTO.getEstado());

        if(empleadoDTO.getDepartamento() != null) {
            DepartamentoDTO departamentoDTOSeleccionado = departamentoDTOComboBoxForm.getItems().stream()
                    .filter(d -> d.getNombre().equalsIgnoreCase(empleadoDTO.getDepartamento()))
                    .findFirst()
                    .orElse(null);
            departamentoDTOComboBoxForm.setValue(departamentoDTOSeleccionado);
        }

        if(empleadoDTO.getRol() != null) {
            RolDTO rolDTOSeleccionado = rolDTOComboBoxForm.getItems().stream()
                    .filter(r -> r.getNombre().equalsIgnoreCase(empleadoDTO.getRol()))
                    .findFirst()
                    .orElse(null);
            rolDTOComboBoxForm.setValue(rolDTOSeleccionado);
        }
    }

    @FXML
    private void botonCancelarEmpleadoEditado() {
        stage.close();
    }

    @FXML
    private void botonGuardarEmpleadoEditado() {
        AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO = actualizarEmpleado();
        if(altaRapidaEmpleadoDTO == null) {
            return;
        }

        new Thread(() -> {
            try {
                controlHorarioEmpleadorEmpleadoService.editarEmpleado(empleadoDTO.getIdEmpleado(), altaRapidaEmpleadoDTO);
                controlHorarioEmpleadorService.editarEmpleado(empleadoDTO.getIdEmpleado(), altaRapidaEmpleadoDTO);
                Platform.runLater(() -> {
                    DepartamentoDTO departamentoDTOSeleccionado = departamentoDTOComboBoxForm.getValue();
                    RolDTO rolDTOSeleccionado = rolDTOComboBoxForm.getValue();

                    if(departamentoDTOSeleccionado != null) {
                        empleadoDTO.setDepartamento(departamentoDTOSeleccionado.getNombre());
                    }
                    if(rolDTOSeleccionado != null) {
                        empleadoDTO.setRol(rolDTOSeleccionado.getNombre());
                    }

                    empleadoDTO.setEstado(estadoComboBox.getValue());
                    empleadoDTO.setNombre(nombreTextField.getText());
                    empleadoDTO.setApellidos(apellidosTextField.getText());
                    empleadoDTO.setEmail(correoTextField.getText());
                    empleadoDTO.setDireccion(direccionTextField.getText());
                    empleadoDTO.setDni(dniTextField.getText());
                    empleadoDTO.setFechaAlta(fechaAltaDatePicker.getValue());
                    empleadoDTO.setFechaNacimiento(fechaNacimientoDatePicker.getValue());

                    tablaEmpleados.refresh();

                    AlertUtils.mostrarConfirmacion("Confirmación", "Empleado editado correctamente");

                    stage.close();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertUtils.mostrarError("Error", "No se puede actualizar el empleado");
                });
            }
        }).start();

    }

    private AltaRapidaEmpleadoDTO actualizarEmpleado() {

        DepartamentoDTO departamentoDTOSeleccionado = departamentoDTOComboBoxForm.getValue();
        if(departamentoDTOSeleccionado == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Debes seleccionar un departamento");
            return null;
        }

        RolDTO rolDTOSeleccionado = rolDTOComboBoxForm.getValue();
        if(rolDTOSeleccionado == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Debes seleccionar un rol");
            return null;
        }

        AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO = new AltaRapidaEmpleadoDTO();

        altaRapidaEmpleadoDTO.setNombre(nombreTextField.getText());
        altaRapidaEmpleadoDTO.setApellidos(apellidosTextField.getText());
        altaRapidaEmpleadoDTO.setEmail(correoTextField.getText());
        altaRapidaEmpleadoDTO.setDireccion(direccionTextField.getText());
        altaRapidaEmpleadoDTO.setTelefono(telefonoTextField.getText());
        altaRapidaEmpleadoDTO.setIdDepartamento(departamentoDTOSeleccionado.getIdDepartamento());
        altaRapidaEmpleadoDTO.setIdRol(rolDTOSeleccionado.getIdRol());
        altaRapidaEmpleadoDTO.setDni(dniTextField.getText());
        altaRapidaEmpleadoDTO.setFechaAlta(fechaAltaDatePicker.getValue());
        altaRapidaEmpleadoDTO.setFechaNacimiento(fechaNacimientoDatePicker.getValue());
        altaRapidaEmpleadoDTO.setEstado(estadoComboBox.getValue());

        return altaRapidaEmpleadoDTO;

    }
}
