package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.service.ControlHorarioService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.ApiClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ControlHorarioController {

    @FXML
    private TextField txtNombre, txtApellidos, txtEmail, txtDireccion, txtTelefono, txtDni;
    @FXML
    private DatePicker datePickerFechaAlta, datePickerFechaNacimiento;
    @FXML
    private ComboBox<DepartamentoDTO> departamentoDTOComboBox;
    @FXML
    private ComboBox<RolDTO> rolDTOComboBox;
    @FXML
    private Button botonGuardar, botonEliminar;
    @FXML
    private TableView<EmpleadoDTO> tablaAltaRapidaEmpleados;
    @FXML
    private TableColumn<EmpleadoDTO, String> columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaEstado;

    private ObservableList<EmpleadoDTO> altaRapidaEmpleados = FXCollections.observableArrayList();

    private final ControlHorarioService controlHorarioService = new ControlHorarioService();

    @FXML
    public void initialize() throws Exception {
        cargarDepartamentos();
        cargarRoles();
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String nuevoTexto = change.getControlNewText();

            if(nuevoTexto.matches("\\d{0,10}")) {
                return change;
            }
            return null;
        };
        txtTelefono.setTextFormatter(new TextFormatter<>(filtro));

        Tooltip avisoEmail = new Tooltip("EL formato del email es incorrecto");
        Tooltip avisoDni = new Tooltip("EL formato del dni es incorrecto (tiene que tener 8 números y 1 letra");

        txtEmail.textProperty().addListener((valorAObservar, valorAntiguo, valorNuevo) -> {
            if(valorNuevo.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                txtEmail.setStyle("-fx-border-color: green");
            } else {
                txtEmail.setTooltip(avisoEmail);
                txtEmail.setStyle("-fx-border-color: red");
            }
        });

        txtDni.textProperty().addListener((valorAObservar, valorAntiguo, valorNuevo) -> {
            if(valorNuevo.matches("^\\d{8}[a-zA-Z]$")) {
                txtDni.setStyle("-fx-border-color: green");
            } else {
                txtDni.setTooltip(avisoDni);
                txtDni.setStyle("-fx-border-color: red");
            }
        });
        tablaAltaRapidaEmpleados.setItems(altaRapidaEmpleados);
        columnaParaNombre.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getNombre()));
        columnaParaApellido.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getApellidos()));
        columnaParaCorreo.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getEmail()));
        columnaParaEstado.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getEstado()));
    }

    private void cargarDepartamentos()  {
        try {
            departamentoDTOComboBox.getItems().setAll(controlHorarioService.getAllDepartamentos());

            //Para que muestre el nombre (no el objeto)
            departamentoDTOComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(DepartamentoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            departamentoDTOComboBox.setButtonCell(departamentoDTOComboBox.getCellFactory().call(null));

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los departamentos");
        }

    }

    private void cargarRoles() {
        try {
            rolDTOComboBox.getItems().setAll(controlHorarioService.getAllRoles());

            //Para que muestre el nombre (no el objeto)
            rolDTOComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(RolDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            rolDTOComboBox.setButtonCell(rolDTOComboBox.getCellFactory().call(null));

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los roles");
        }

    }

    @FXML
    public void botonGuardarEmpleado(ActionEvent event) {
        //Validación de los text fields
        if(txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtApellidos.getText().isEmpty() || txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty() || txtDni.getText().isEmpty()) {
            AlertUtils.mostrarError("Validación", "Por favor, rellena los campos obligatorios");
            return;
        }
        //Validación ComboBox
        if(departamentoDTOComboBox.getValue() == null || rolDTOComboBox.getValue() == null) {
            AlertUtils.mostrarAdvertencia("Validación", "Selecciona un departamento y un rol");
            return;
        }

        //Validación DatePicker
        if(datePickerFechaAlta.getValue() == null || datePickerFechaNacimiento.getValue() == null) {
            AlertUtils.mostrarAdvertencia("Validación", "Selecciona la fecha de alta y la de nacimiento");
            return;
        }

        // Usamos el DTO de Alta que espera el Backend
        AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO = new AltaRapidaEmpleadoDTO();
        altaRapidaEmpleadoDTO.setNombre(txtNombre.getText());
        altaRapidaEmpleadoDTO.setApellidos(txtApellidos.getText());
        altaRapidaEmpleadoDTO.setEmail(txtEmail.getText());
        altaRapidaEmpleadoDTO.setDireccion(txtDireccion.getText());
        altaRapidaEmpleadoDTO.setTelefono(txtTelefono.getText());
        altaRapidaEmpleadoDTO.setDni(txtDni.getText());
        altaRapidaEmpleadoDTO.setFechaAlta(datePickerFechaAlta.getValue());
        altaRapidaEmpleadoDTO.setFechaNacimiento(datePickerFechaNacimiento.getValue());

        // Obtenemos los IDs de los combos (seleccionando el objeto DTO del combo)
        if (departamentoDTOComboBox.getValue() != null) {
            altaRapidaEmpleadoDTO.setIdDepartamento(departamentoDTOComboBox.getValue().getIdDepartamento());
        }

        if (rolDTOComboBox.getValue() != null) {
            altaRapidaEmpleadoDTO.setIdRol(rolDTOComboBox.getValue().getIdRol());
        }

       altaRapidaEmpleadoDTO.setIdEmpresa(2);


        // Llamada al servicio
        try {
            EmpleadoDTO resultado = controlHorarioService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
            if (resultado != null) {
                altaRapidaEmpleados.add(resultado);
                AlertUtils.mostrarInfo("Éxito", "Empleado guardado correctamente.");
                limpiarCampos();
            }
        } catch (Exception e) {
            AlertUtils.mostrarError("Error API", "No se pudo guardar: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellidos.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtDni.clear();
        datePickerFechaAlta.setValue(null);
        datePickerFechaNacimiento.setValue(null);
        departamentoDTOComboBox.getSelectionModel().clearSelection();
        departamentoDTOComboBox.setValue(null);
        departamentoDTOComboBox.setPromptText("Departamento");
        departamentoDTOComboBox.setEditable(true);
        rolDTOComboBox.getSelectionModel().clearSelection();
        rolDTOComboBox.setValue(null);
        rolDTOComboBox.setEditable(true);
        rolDTOComboBox.setPromptText("Rol");
    }

    @FXML
    private void botonEliminarEmpleado(ActionEvent event) {

        EmpleadoDTO empleadoDTO = tablaAltaRapidaEmpleados.getSelectionModel().getSelectedItem();
        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        AlertUtils.mostrarConfirmacion("Confirmando eliminación empleado", String.format("¿Seguro que quieres eliminar al empleado seleccionado: %s?", empleadoDTO.getNombre()));

        try {
            controlHorarioService.borrarEmpleado(empleadoDTO.getIdEmpleado());
            tablaAltaRapidaEmpleados.getItems().remove(empleadoDTO);
        } catch (Exception e) {
            AlertUtils.mostrarError("Error","No se ha podido eliminar al empleado");
            throw new RuntimeException(e);
        }
    }

    private void configurarSeleccion() {
        botonEliminar.setDisable(true);

        tablaAltaRapidaEmpleados.getSelectionModel().selectedItemProperty().addListener((observable, antiguoValor, nuevoValor) ->
                botonEliminar.setDisable(nuevoValor == null));
    }
}
