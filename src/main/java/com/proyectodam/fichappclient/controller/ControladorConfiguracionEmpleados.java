package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.dto.TipoContratoConfigDTO;
import com.proyectodam.fichappclient.model.HorarioDTO;
import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.service.TipoContratoConfigService;
import com.proyectodam.fichappclient.service.HorarioService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;



public class ControladorConfiguracionEmpleados {

    // Tipos de Contrato
    @FXML private TextField txtNombreContrato;
    @FXML private CheckBox chkIndefinido;
    @FXML private TextField txtDuracionMeses;
    @FXML private ListView<TipoContratoConfigDTO> listContratos;
    @FXML private Label lblMensajeContrato;



    // Horarios
    @FXML private TextField txtNombreHorario;
    @FXML private ListView<HorarioDTO> listHorarios;
    @FXML private Label lblMensajeHorario;

    private TipoContratoConfigService tipoContratoService;
    private HorarioService horarioService;

    private ObservableList<TipoContratoConfigDTO> contratosObservableList;
    private ObservableList<HorarioDTO> horariosObservableList;

    @FXML
    public void initialize() {
        tipoContratoService = new TipoContratoConfigService();
        horarioService = new HorarioService();

        contratosObservableList = FXCollections.observableArrayList();
        horariosObservableList = FXCollections.observableArrayList();

        listContratos.setItems(contratosObservableList);
        listHorarios.setItems(horariosObservableList);
        
        // Formatear celdas del ListView
        listContratos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TipoContratoConfigDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });


        listHorarios.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(HorarioDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });

        chkIndefinido.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtDuracionMeses.setDisable(newVal);
            if(newVal) txtDuracionMeses.clear();
        });

        cargarDatos();
    }

    private void cargarDatos() {
        Platform.runLater(() -> {
            try {
                contratosObservableList.setAll(tipoContratoService.listarTodos());
                horariosObservableList.setAll(horarioService.obtenerTodos());
            } catch (Exception e) {
                lblMensajeContrato.setText("Error al cargar datos: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleGuardarContrato() {
        String nombre = txtNombreContrato.getText();
        boolean indefinido = chkIndefinido.isSelected();
        String duracionStr = txtDuracionMeses.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            lblMensajeContrato.setText("El nombre es obligatorio.");
            return;
        }

        Integer duracion = null;
        if (!indefinido) {
            try {
                duracion = Integer.parseInt(duracionStr);
            } catch (NumberFormatException e) {
                lblMensajeContrato.setText("La duración debe ser un número entero.");
                return;
            }
        }

        TipoContratoConfigDTO nuevo = new TipoContratoConfigDTO(0, nombre, indefinido, duracion);
        
        Platform.runLater(() -> {
            try {
                TipoContratoConfigDTO guardado = tipoContratoService.guardar(nuevo);
                contratosObservableList.add(guardado);
                txtNombreContrato.clear();
                txtDuracionMeses.clear();
                chkIndefinido.setSelected(false);
                lblMensajeContrato.setText("");
            } catch (Exception e) {
                lblMensajeContrato.setText("Error al guardar: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminarContrato() {
        TipoContratoConfigDTO seleccionado = listContratos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensajeContrato.setText("Selecciona un contrato para eliminar.");
            return;
        }

        Platform.runLater(() -> {
            try {
                tipoContratoService.eliminar(seleccionado.getIdTipoContrato());
                contratosObservableList.remove(seleccionado);
                lblMensajeContrato.setText("");
            } catch (Exception e) {
                lblMensajeContrato.setText("Error al eliminar: " + e.getMessage());
            }
        });
    }


    @FXML
    private void handleGuardarHorario() {
        String nombre = txtNombreHorario.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            lblMensajeHorario.setText("El nombre es obligatorio.");
            return;
        }

        HorarioDTO nuevo = new HorarioDTO();
        nuevo.setNombre(nombre);
        
        Platform.runLater(() -> {
            try {
                HorarioDTO guardado = horarioService.guardar(nuevo);
                horariosObservableList.add(guardado);
                txtNombreHorario.clear();
                lblMensajeHorario.setText("");
            } catch (Exception e) {
                lblMensajeHorario.setText("Error al guardar: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminarHorario() {
        HorarioDTO seleccionado = listHorarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensajeHorario.setText("Selecciona un horario.");
            return;
        }

        Platform.runLater(() -> {
            try {
                horarioService.eliminar(seleccionado.getIdHorario());
                horariosObservableList.remove(seleccionado);
                lblMensajeHorario.setText("");
            } catch (Exception e) {
                lblMensajeHorario.setText("No se puede eliminar (Puede estar en uso).");
            }
        });
    }

    @FXML
    private void handleBackToDashboard() {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }
}
