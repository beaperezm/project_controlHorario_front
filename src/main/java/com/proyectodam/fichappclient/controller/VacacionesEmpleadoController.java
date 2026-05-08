package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.VacacionesDTO;
import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.service.VacacionesService;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VacacionesEmpleadoController {

    @FXML
    private Label estadoLabel;

    @FXML
    private Button btnSolicitar;

    @FXML
    private TableView<VacacionesDTO> tablaSolicitudes;
    @FXML
    private TableColumn<VacacionesDTO, Integer> colId;
    @FXML
    private TableColumn<VacacionesDTO, String> colTipo;
    @FXML
    private TableColumn<VacacionesDTO, LocalDate> colInicio;
    @FXML
    private TableColumn<VacacionesDTO, LocalDate> colFin;
    @FXML
    private TableColumn<VacacionesDTO, Integer> colDias;
    @FXML
    private TableColumn<VacacionesDTO, String> colEstado;

    private final ObservableList<VacacionesDTO> datos = FXCollections.observableArrayList();
    private final VacacionesService vacacionesService = new VacacionesService();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdSolicitud() != null ? c.getValue().getIdSolicitud() : 0).asObject());
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colInicio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        colFin.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaFin()));
        colDias.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDiasHabiles() != null ? c.getValue().getDiasHabiles() : 0).asObject());
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        tablaSolicitudes.setItems(datos);
        cargarDatos();
    }

    private void cargarDatos() {
        estadoLabel.setText("Cargando sus solicitudes...");
        new Thread(() -> {
            try {
                int myId = SessionData.getEmpleadoActual().getIdEmpleado();
                List<VacacionesDTO> lista = vacacionesService.obtenerPorEmpleado(myId);

                Platform.runLater(() -> {
                    datos.clear();
                    datos.addAll(lista);
                    estadoLabel.setText(""); // Limpiar mensaje de carga
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    estadoLabel.setText("Error al cargar datos.");
                    mostrarError("Error al cargar datos", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void onSolicitarClick() {
        Dialog<VacacionesDTO> dialog = new Dialog<>();
        dialog.setTitle("Solicitar Vacaciones / Ausencia");
        dialog.setHeaderText("Rellena los datos de la solicitud");

        ButtonType btnGuardar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("VACACIONES", "AUSENCIA");
        cbTipo.getSelectionModel().selectFirst();

        DatePicker dpInicio = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker dpFin = new DatePicker(LocalDate.now().plusDays(2));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(cbTipo, 1, 0);

        grid.add(new Label("Inicio:"), 0, 1);
        grid.add(dpInicio, 1, 1);

        grid.add(new Label("Fin:"), 0, 2);
        grid.add(dpFin, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node okBtn = dialog.getDialogPane().lookupButton(btnGuardar);
        okBtn.disableProperty().bind(
                dpInicio.valueProperty().isNull().or(dpFin.valueProperty().isNull()));

        dialog.setResultConverter(button -> {
            if (button != btnGuardar)
                return null;

            LocalDate ini = dpInicio.getValue();
            LocalDate fin = dpFin.getValue();

            if (fin.isBefore(ini)) {
                mostrarError("Fechas incorrectas", "La fecha de fin no puede ser anterior a la de inicio.");
                return null;
            }

            VacacionesDTO dto = new VacacionesDTO();
            dto.setTipo(cbTipo.getValue());
            dto.setFechaInicio(ini);
            dto.setFechaFin(fin);
            dto.setDiasHabiles(0); // Backend calculará si va a 0
            dto.setEstado("PENDIENTE");
            
            return dto;
        });

        Optional<VacacionesDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            new Thread(() -> {
                try {
                    int myId = SessionData.getEmpleadoActual().getIdEmpleado();
                    vacacionesService.solicitarVacaciones(myId, dto);
                    Platform.runLater(() -> {
                        mostrarInfo("Éxito", "Solicitud enviada correctamente.");
                        cargarDatos(); // Refresh table
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error al solicitar", e.getMessage()));
                }
            }).start();
        });
    }

    @FXML
    private void onVerSolicitudesClick() {
        cargarDatos();
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }

    private void mostrarError(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(titulo);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(titulo);
        a.setContentText(msg);
        a.showAndWait();
    }
}
