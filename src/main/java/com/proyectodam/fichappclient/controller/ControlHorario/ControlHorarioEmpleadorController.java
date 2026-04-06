package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoBorradoUtil;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoTablaUtil;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class ControlHorarioEmpleadorController {

    @FXML
    private TextField txtNombre, txtApellidos, txtEmail, txtDireccion, txtTelefono, txtDni;
    @FXML
    private DatePicker datePickerFechaAlta, datePickerFechaNacimiento;
    @FXML
    private ComboBox<DepartamentoDTO> departamentoDTOComboBox;
    @FXML
    private ComboBox<RolDTO> rolDTOComboBox;
    @FXML
    private ComboBox<HorarioDTO> horarioDTOComboBox;
    @FXML
    private Button botonGuardar, botonEliminar, botonEditar;
    @FXML
    private TableView<EmpleadoDTO> tablaEmpleados;
    @FXML
    private TableColumn<EmpleadoDTO, String> columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaHorario, columnaParaRol, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado;

    private ObservableList<EmpleadoDTO> altaRapidaEmpleados = FXCollections.observableArrayList();

    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService = new ControlHorarioEmpleadorService();

    private EmpleadoBorradoUtil empleadoBorradoUtil;

    private final ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService = new ControlHorarioEmpleadorEmpleadoService();

    @FXML
    public void initialize() throws Exception {
        empleadoBorradoUtil = new EmpleadoBorradoUtil(controlHorarioEmpleadorService, controlHorarioEmpleadorEmpleadoService);

        cargarDepartamentos();
        cargarRoles();
        cargarHorarios();
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

       botonEditar.setOnAction(e -> {
            EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();
            if(empleadoDTO == null) {
                AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
                return;
            }
            try {
                formularioEditarEmpleado(empleadoDTO);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        EmpleadoTablaUtil.tablaEmpleado(tablaEmpleados, SessionData.getInstance().getEmpleados(), columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaHorario, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado);

    }

    private void cargarDepartamentos()  {
        try {
           departamentoDTOComboBox.getItems().setAll(controlHorarioEmpleadorService.getAllDepartamentos());

            //Para que muestre el nombre (no el objeto)
            departamentoDTOComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(DepartamentoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                   setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre departamento en el 'placeholder'
            departamentoDTOComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DepartamentoDTO departamentoDTO, boolean estaVacio) {
                    super.updateItem(departamentoDTO, estaVacio);
                    setText((departamentoDTO == null) ? "Departamento" : departamentoDTO.getNombre());
                }
            });

            departamentoDTOComboBox.getSelectionModel().clearSelection();
            departamentoDTOComboBox.setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los departamentos");
        }
    }

    private void cargarRoles() {
        try {
          rolDTOComboBox.getItems().setAll(controlHorarioEmpleadorService.getAllRoles());

            //Para que muestre el nombre (no el objeto)
            rolDTOComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void  updateItem(RolDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre rol en el 'placeholder'
            rolDTOComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(RolDTO rolDTO, boolean estaVacio) {
                    super.updateItem(rolDTO, estaVacio);
                    setText((rolDTO == null) ? "Rol" : rolDTO.getNombre());
                }
            });


            rolDTOComboBox.getSelectionModel().clearSelection();
            rolDTOComboBox.setEditable(false);


        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los roles");
        }

    }

    private void cargarHorarios()  {
        try {
            horarioDTOComboBox.getItems().setAll(controlHorarioEmpleadorService.getAllHorarios());

            //Para que muestre el nombre (no el objeto)
            horarioDTOComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(HorarioDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre horario en el 'placeholder'
            horarioDTOComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(HorarioDTO horarioDTO, boolean estaVacio) {
                    super.updateItem(horarioDTO, estaVacio);
                    setText((horarioDTO == null) ? "Horario" : horarioDTO.getNombre());
                }
            });

            horarioDTOComboBox.getSelectionModel().clearSelection();
            horarioDTOComboBox.setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los horarios");
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

        //Validación ComboBox Horario
        if(horarioDTOComboBox.getValue() == null) {
            AlertUtils.mostrarAdvertencia("Validación", "Selecciona un horario");
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

        if (horarioDTOComboBox.getValue() != null) {
            altaRapidaEmpleadoDTO.setIdHorario(horarioDTOComboBox.getValue().getIdHorario());
        }

       altaRapidaEmpleadoDTO.setIdEmpresa(2);


        // Llamada al servicio
        try {
            EmpleadoDTO resultado = controlHorarioEmpleadorService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
            if (resultado != null) {
                resultado.setDepartamento(departamentoDTOComboBox.getValue().getNombre());
                resultado.setRol(rolDTOComboBox.getValue().getNombre());
                resultado.setHorario(horarioDTOComboBox.getValue().getNombre());
                resultado.setFechaAlta(datePickerFechaAlta.getValue());
                resultado.setEstado("ACTIVO");
                resultado.setDni(txtDni.getText());

                SessionData.getInstance().getEmpleados().add(resultado);
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
        rolDTOComboBox.getSelectionModel().clearSelection();
        horarioDTOComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void botonEliminarEmpleado(ActionEvent event) {

        EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();
        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        empleadoBorradoUtil.borradoFisicoEmpleado(empleadoDTO, SessionData.getInstance().getEmpleados());

    }

    private void formularioEditarEmpleado(EmpleadoDTO empleadoDTO) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/controlhorario/formulario_editar_empleado.fxml"));
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Editar empleado");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tablaEmpleados.getScene().getWindow());

        EmpleadoFormularioEditarController empleadoFormularioEditarController = fxmlLoader.getController();

        empleadoFormularioEditarController.initData(empleadoDTO, controlHorarioEmpleadorEmpleadoService, controlHorarioEmpleadorService, FXCollections.observableArrayList("ACTIVO"), stage, tablaEmpleados);

        stage.setScene(new Scene(parent));
        stage.showAndWait();

        tablaEmpleados.refresh();
    }

    @FXML
    private void botonEditarEmpleado(ActionEvent actionEvent) throws Exception {
        EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();
        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        formularioEditarEmpleado(empleadoDTO);
    }

}
