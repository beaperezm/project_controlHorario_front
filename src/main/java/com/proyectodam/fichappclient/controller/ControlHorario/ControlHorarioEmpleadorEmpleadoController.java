package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorEmpleadoService;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.controlhorario.CargarEstadosUtil;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoBorradoUtil;
import com.proyectodam.fichappclient.util.controlhorario.EmpleadoTablaUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ControlHorarioEmpleadorEmpleadoController {

    @FXML
    private TextField buscarNombre;
    @FXML
    private ComboBox<DepartamentoDTO> filtrarDepartamento;
    @FXML
    private ComboBox<RolDTO> filtrarRol;
    @FXML
    private ComboBox<String> filtrarEstado;
    @FXML
    private TableView<EmpleadoDTO> tablaEmpleados;
    @FXML
    private TableColumn<EmpleadoDTO, String> columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta, columnaParaFechaNacimiento, columnaParaEstado;
    @FXML
    private DatePicker filtrarFechaAlta, filtrarFechaNacimiento;
    @FXML
    private Label totalEmpleadosLabel, empleadosActivosLabel, empleadosInactivosLabel, empleadosBajaMedicaLabel, empleadosExcedenciaLabel;

    private final ControlHorarioEmpleadorService controlHorarioEmpleadorService = new ControlHorarioEmpleadorService();
    private final ControlHorarioEmpleadorEmpleadoService controlHorarioEmpleadorEmpleadoService = new ControlHorarioEmpleadorEmpleadoService();

    private final ObservableList<EmpleadoDTO> listadoEmpleados = FXCollections.observableArrayList();

    private EmpleadoBorradoUtil empleadoBorradoUtil;

    private FilteredList<EmpleadoDTO> filtroEmpleados;
    private CargarEstadosUtil cargarEstadosUtil = new CargarEstadosUtil();


    @FXML
    public void initialize() throws Exception {
        empleadoBorradoUtil = new EmpleadoBorradoUtil(controlHorarioEmpleadorService, controlHorarioEmpleadorEmpleadoService);
        cargarDepartamentos();
        cargarRoles();
        cargarEstados();

        EmpleadoTablaUtil.tablaEmpleado(tablaEmpleados, listadoEmpleados ,columnaParaNombre, columnaParaApellido, columnaParaCorreo, columnaParaDireccion, columnaParaTelefono, columnaParaDepartamento, columnaParaRol, columnaParaDni, columnaParaFechaAlta,columnaParaFechaNacimiento, columnaParaEstado);

        configurarFiltro();
        cargarEmpleados();
        actualizarContadores();

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
                    setText((departamentoDTO == null || estaVacio) ? "Departamento" : departamentoDTO.getNombre());
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
                protected void updateItem(RolDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null ? null : item.getNombre()));
                }
            });

            //Para que muestre rol en el 'placeholder'
            filtrarRol.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(RolDTO rolDTO, boolean estaVacio) {
                    super.updateItem(rolDTO, estaVacio);
                    setText((rolDTO == null || estaVacio) ? "Rol" : rolDTO.getNombre());
                }
            });


                filtrarRol.getSelectionModel().clearSelection();
                filtrarRol.setEditable(false);

        }catch(Exception ex){
            ex.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron cargar los roles");
        }
    }

    private void cargarEmpleados() {

        new Thread(() -> {
            try {
                List<EmpleadoDTO> listaEmpleados = controlHorarioEmpleadorEmpleadoService.getAllEmpleados();

                // El backend ya devuelve la lista filtrada de cuentas de sistema
                Platform.runLater(() -> {
                    listadoEmpleados.setAll(listaEmpleados);
                });
            } catch (Exception e) {

                Platform.runLater(() -> {
                    AlertUtils.mostrarError("Error", "No se pueden cargar los empleados");
                });

                throw new RuntimeException(e);
            }
        }).start();
    }

    private void cargarEstados() {

        filtrarEstado.setItems(cargarEstadosUtil.cargarEstados());

        //Para que muestre estado en el 'placeholder'
        filtrarEstado.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String nombre, boolean estaVacio) {
                super.updateItem(nombre, estaVacio);
                setText((nombre == null || estaVacio) ? "Estado" : nombre);
            }
        });
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
        actualizarContadores();
    }

    private void configurarFiltro() {
        filtroEmpleados = new FilteredList<>(listadoEmpleados, p -> true);

        buscarNombre.textProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());
        filtrarDepartamento.valueProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());
        filtrarRol.valueProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());
        filtrarEstado.valueProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());
        filtrarFechaAlta.valueProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());
        filtrarFechaNacimiento.valueProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> aplicarFiltro());

        SortedList<EmpleadoDTO> dataSorted = new SortedList<>(filtroEmpleados);
        dataSorted.comparatorProperty().bind(tablaEmpleados.comparatorProperty());
        tablaEmpleados.setItems(dataSorted);
    }

    private void aplicarFiltro() {
        filtroEmpleados.setPredicate(empleadoDTO -> {

            String textoNombreApellidos = buscarNombre.getText();
            if(textoNombreApellidos != null && !textoNombreApellidos.isBlank()) {

                String filtroNombreApellidos = textoNombreApellidos.toLowerCase().trim();

                String nombre = empleadoDTO.getNombre() != null ? empleadoDTO.getNombre().toLowerCase() : "";
                String apellidos = empleadoDTO.getApellidos() != null ? empleadoDTO.getApellidos().toLowerCase() : "";

                String busquedaCompleta = (nombre + " " + apellidos);

                if(!nombre.contains(filtroNombreApellidos) && !apellidos.contains(filtroNombreApellidos) && !busquedaCompleta.contains(filtroNombreApellidos)) {
                    return false;
                }
            }

            if(filtrarDepartamento.getValue() != null) {
                if(!Objects.equals(empleadoDTO.getDepartamento(), filtrarDepartamento.getValue().getNombre())) {
                    return false;
                }
            }

            if(filtrarRol.getValue() != null) {
                if(!Objects.equals(empleadoDTO.getRol(), filtrarRol.getValue().getNombre())) {
                    return false;
                }
            }

            if(filtrarEstado.getValue() != null) {
                if(empleadoDTO.getEstado() == null || !empleadoDTO.getEstado().equalsIgnoreCase(filtrarEstado.getValue())) {
                    return false;
                }
            }

            if(filtrarFechaAlta.getValue() != null) {
                if(empleadoDTO.getFechaAlta() == null) {
                    return false;
                }
                if(!empleadoDTO.getFechaAlta().isEqual(filtrarFechaAlta.getValue())) {
                    return false;
                }
            }

            if(filtrarFechaNacimiento.getValue() != null) {
                if(empleadoDTO.getFechaNacimiento() == null) {
                    return false;
                }
                if(!empleadoDTO.getFechaNacimiento().isEqual(filtrarFechaNacimiento.getValue())) {
                    return false;
                }
            }

            return true;
        });
    }

    @FXML
    private void botonLimpiar() {
        buscarNombre.clear();
        filtrarFechaNacimiento.setValue(null);
        filtrarFechaAlta.setValue(null);
        filtrarEstado.getSelectionModel().clearSelection();
        filtrarDepartamento.getSelectionModel().clearSelection();
        filtrarRol.getSelectionModel().clearSelection();


        cargarEmpleados();
    }

    private void formularioEditarEmpleado(EmpleadoDTO empleadoDTO) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/controlhorario/formulario_editar_empleado.fxml"));
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Editar empleado");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tablaEmpleados.getScene().getWindow());

        EmpleadoFormularioEditarController empleadoFormularioEditarController = fxmlLoader.getController();

        empleadoFormularioEditarController.initData(empleadoDTO, controlHorarioEmpleadorEmpleadoService, controlHorarioEmpleadorService, cargarEstadosUtil.cargarEstados(), stage, tablaEmpleados);

        stage.setScene(new Scene(parent));
        stage.showAndWait();
        actualizarContadores();

        cargarEmpleados();
    }


    @FXML
    private void botonEditarEmpleado(ActionEvent actionEvent) throws Exception {
        EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();
        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        formularioEditarEmpleado(empleadoDTO);
        actualizarContadores();
    }

    @FXML
    private void botonVerDetalleEmpleado() {
        EmpleadoDTO empleadoDTO = tablaEmpleados.getSelectionModel().getSelectedItem();

        if(empleadoDTO == null) {
            AlertUtils.mostrarAdvertencia("Advertencia", "Tienes que seleccionar un empleado");
            return;
        }

        new Thread(() -> {
            try {
                EmpleadoDetalleDTO empleadoDetalleDTO = controlHorarioEmpleadorEmpleadoService.getEmpleadoDetalle(empleadoDTO.getIdEmpleado());
                Platform.runLater(() -> mostrarVentanaDetalleEmpleado(empleadoDetalleDTO));
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> AlertUtils.mostrarError("Error", "No se ha podido cargar el detalle"));
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void mostrarVentanaDetalleEmpleado(EmpleadoDetalleDTO empleadoDetalleDTO) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/proyectodam/fichappclient/views/controlhorario/detalle_empleado.fxml"));
            Parent parent = fxmlLoader.load();

            EmpleadoDetalleController empleadoDetalleController = fxmlLoader.getController();
            empleadoDetalleController.setEmpleadoDetalle(empleadoDetalleDTO);

            Stage stage = new Stage();
            stage.setTitle("Detalle Empleado");
            stage.setScene(new Scene(parent, 400, 570));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void actualizarContadores() {
        new Thread(() -> {
            try {
                EmpleadoEstadoContadorDTO empleadoEstadoContadorDTO = controlHorarioEmpleadorEmpleadoService.getEstadoEmpleado();

                Platform.runLater(() -> {
                    totalEmpleadosLabel.setText(String.valueOf(empleadoEstadoContadorDTO.getTotalEmpleados()));
                    empleadosActivosLabel.setText(String.valueOf(empleadoEstadoContadorDTO.getActivos()));
                    empleadosInactivosLabel.setText(String.valueOf(empleadoEstadoContadorDTO.getInactivos()));
                    empleadosBajaMedicaLabel.setText(String.valueOf(empleadoEstadoContadorDTO.getBajaMedica()));
                    empleadosExcedenciaLabel.setText(String.valueOf(empleadoEstadoContadorDTO.getExcedencia()));
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> AlertUtils.mostrarError("Error", "No se han podido cargar los contadores de los estados"));
                throw new RuntimeException(e);
            }
        }).start();
    }




}
