package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoBorradoUtil;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoTablaUtil;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    private Button botonGuardar, botonEliminar, botonEditar;
    @FXML
    private TableView<EmpleadoDTO> tablaEmpleados;
    @FXML
    private TableColumn<EmpleadoDTO, String> columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado;

    private ObservableList<EmpleadoDTO> altaRapidaEmpleados = FXCollections.observableArrayList();

    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService = new ControlHorarioEmpleadorService();

    private EmpleadoBorradoUtil empleadoBorradoUtil;

    private final ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService = new ControlHorarioEmpleadorEmpleadoService();

    @FXML
    public void initialize() throws Exception {
        empleadoBorradoUtil = new EmpleadoBorradoUtil(controlHorarioEmpleadorService, controlHorarioEmpleadorEmpleadoService);

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

        EmpleadoTablaUtil.tablaEmpleado(tablaEmpleados, SessionData.getInstance().getEmpleados(), columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado);

   /*     botonEditar.setOnAction(e -> {
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
        });*/
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

            //Para que muestre departamento en el 'placeholder'
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
            EmpleadoDTO resultado = controlHorarioEmpleadorService.altaRapidaEmpleado(altaRapidaEmpleadoDTO);
            if (resultado != null) {
                resultado.setDepartamento(departamentoDTOComboBox.getValue().getNombre());
                resultado.setRol(rolDTOComboBox.getValue().getNombre());
                resultado.setFechaAlta(datePickerFechaAlta.getValue());
                resultado.setEstado("ACTIVO");
                resultado.setDni(txtDni.getText());
              /*  resultado.setDepartamentoDTO(departamentoDTOComboBox.getValue());
                resultado.setRolDTO(rolDTOComboBox.getValue());*/



               // altaRapidaEmpleados.add(resultado);
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

        Stage formularioEdicion = new Stage();
        formularioEdicion.setTitle("Editar empleado");
        formularioEdicion.initModality(Modality.WINDOW_MODAL);
        formularioEdicion.initOwner(tablaEmpleados.getScene().getWindow());

        TextField nombreTextField = new TextField(empleadoDTO.getNombre());
        TextField apellidosTextField = new TextField(empleadoDTO.getApellidos());
        TextField correoTextField = new TextField(empleadoDTO.getEmail());
        TextField direccionTextField = new TextField(empleadoDTO.getDireccion());
        TextField telefonoTextField = new TextField(empleadoDTO.getTelefono());
        TextField dniTextField = new TextField(empleadoDTO.getDni());
        TextField estadoTextField = new TextField(empleadoDTO.getEstado());

        DatePicker fechaAltaDatePicker = new DatePicker(empleadoDTO.getFechaAlta());
        DatePicker fechaNacimientoDatePicker = new DatePicker(empleadoDTO.getFechaNacimiento());

        ComboBox<DepartamentoDTO> departamentoDTOComboBoxForm = new ComboBox<>();
        ComboBox<RolDTO> rolDTOComboBoxForm = new ComboBox<>();
        departamentoDTOComboBoxForm.getItems().setAll(controlHorarioEmpleadorService.getAllDepartamentos());
        rolDTOComboBoxForm.getItems().setAll(controlHorarioEmpleadorService.getAllRoles());

        //Mostrando los nombres en los comboBox
        departamentoDTOComboBoxForm.setCellFactory(cbd -> new ListCell<>() {
            @Override
            protected void updateItem(DepartamentoDTO departamentoDTO, boolean estaVacio) {
                super.updateItem(departamentoDTO,estaVacio);
                setText(estaVacio || departamentoDTO == null ? null : departamentoDTO.getNombre());
            }
        });

        departamentoDTOComboBoxForm.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DepartamentoDTO departamentoDTO, boolean estaVacio) {
                super.updateItem(departamentoDTO,estaVacio);
                setText(departamentoDTO == null ? "Departamento" : departamentoDTO.getNombre());
            }
        });

        rolDTOComboBoxForm.setCellFactory(cbd -> new ListCell<>() {
            @Override
            protected void updateItem(RolDTO rolDTO, boolean estaVacio) {
                super.updateItem(rolDTO,estaVacio);
                setText(estaVacio || rolDTO == null ? null : rolDTO.getNombre());
            }
        });

        rolDTOComboBoxForm.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(RolDTO rolDTO, boolean estaVacio) {
                super.updateItem(rolDTO,estaVacio);
                setText(rolDTO == null ? "Departamento" : rolDTO.getNombre());
            }
        });

        departamentoDTOComboBoxForm.setValue(empleadoDTO.getDepartamentoDTO());
        rolDTOComboBoxForm.setValue(empleadoDTO.getRolDTO());

        Label nombreLabel = new Label("Nombre: ");
        Label apellidosLabel = new Label("Apellidos: ");
        Label correoLabel = new Label("Correo electrónico: ");
        Label direccionLabel = new Label("Dirección: ");
        Label telefonoLabel = new Label("Teléfono: ");
        Label departamentoLabel = new Label("Departamento: ");
        Label rolLabel = new Label("Rol: ");
        Label dniLabel = new Label("Dni: ");
        Label fechaAltaLabel = new Label("Fecha de Alta: ");
        Label fechaNacimientoLabel = new Label("Fecha de Nacimiento: ");
        Label estadoLabel = new Label("Estado: ");

        Button botonGuardarStage = new Button("Guardar");
        Button botonCancelarStage = new Button("Cancelar");


        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(20));

        gridPane.add(nombreLabel, 0, 0);
        gridPane.add(nombreTextField, 1, 0);
        gridPane.add(apellidosLabel, 0, 1);
        gridPane.add(apellidosTextField, 1, 1);
        gridPane.add(correoLabel, 0, 2);
        gridPane.add(correoTextField, 1, 2);
        gridPane.add(direccionLabel, 0, 3);
        gridPane.add(direccionTextField, 1, 3);
        gridPane.add(telefonoLabel, 0, 4);
        gridPane.add(telefonoTextField, 1, 4);
        gridPane.add(departamentoLabel, 0, 5);
        gridPane.add(departamentoDTOComboBoxForm, 1, 5);
        gridPane.add(rolLabel, 0, 6);
        gridPane.add(rolDTOComboBoxForm, 1, 6);
        gridPane.add(dniLabel, 0, 7);
        gridPane.add(dniTextField, 1, 7);
        gridPane.add(fechaAltaLabel, 0, 8);
        gridPane.add(fechaAltaDatePicker, 1, 8);
        gridPane.add(fechaNacimientoLabel, 0, 9);
        gridPane.add(fechaNacimientoDatePicker, 1, 9);
        gridPane.add(estadoLabel, 0, 10);
        gridPane.add(estadoTextField, 1, 10);

        HBox botones = new HBox(10, botonGuardarStage, botonCancelarStage);
        botones.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(botones, 1, 11);


        Scene scene = new Scene(gridPane);
        formularioEdicion.setScene(scene);
        formularioEdicion.show();

        botonCancelarStage.setOnAction(c -> formularioEdicion.close());

        botonGuardarStage.setOnAction(g -> {
            if(nombreTextField.getText().isEmpty()) {
                AlertUtils.mostrarAdvertencia("Advertencia", "El nombre no puede estar vacío");
                return;
            }

            empleadoDTO.setNombre(nombreTextField.getText());
            empleadoDTO.setApellidos(apellidosTextField.getText());
            empleadoDTO.setEmail(correoTextField.getText());
            empleadoDTO.setDireccion(direccionTextField.getText());
            empleadoDTO.setTelefono(telefonoTextField.getText());
            empleadoDTO.setDepartamentoDTO(departamentoDTOComboBoxForm.getValue());
            empleadoDTO.setRolDTO(rolDTOComboBoxForm.getValue());
            empleadoDTO.setDni(dniTextField.getText());
            empleadoDTO.setFechaAlta(fechaAltaDatePicker.getValue());
            empleadoDTO.setFechaNacimiento(fechaNacimientoDatePicker.getValue());
            empleadoDTO.setEstado(estadoTextField.getText());

            new Thread(() -> {
                try {
                    actualizarEmpleado(empleadoDTO);
                    Platform.runLater(() -> formularioEdicion.close());

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        AlertUtils.mostrarError("Error", "No se puedo actualizar el empleado");
                    });
                }
            }).start();

        });
    }

    private void actualizarEmpleado(EmpleadoDTO empleadoDTO) throws IOException, InterruptedException {

       AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO = new AltaRapidaEmpleadoDTO();
       altaRapidaEmpleadoDTO.setNombre(empleadoDTO.getNombre());
       altaRapidaEmpleadoDTO.setApellidos(empleadoDTO.getApellidos());
       altaRapidaEmpleadoDTO.setEmail(empleadoDTO.getEmail());
       altaRapidaEmpleadoDTO.setDireccion(empleadoDTO.getDireccion());
       altaRapidaEmpleadoDTO.setTelefono(empleadoDTO.getTelefono());
       altaRapidaEmpleadoDTO.setDni(empleadoDTO.getDni());
       altaRapidaEmpleadoDTO.setFechaAlta(empleadoDTO.getFechaAlta());
       altaRapidaEmpleadoDTO.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
       altaRapidaEmpleadoDTO.setEstado(empleadoDTO.getEstado());


       EmpleadoDTO empleado = controlHorarioEmpleadorService.editarEmpleado(empleadoDTO.getIdEmpleado(),altaRapidaEmpleadoDTO);
       Platform.runLater(() -> {
           if(empleado != null) {
               AlertUtils.mostrarConfirmacion("Confirmación", "Empleado editado correctamente");
               tablaEmpleados.refresh();
           } else AlertUtils.mostrarError("Error", "No se ha podido dar de alta al empleado");
       });

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
