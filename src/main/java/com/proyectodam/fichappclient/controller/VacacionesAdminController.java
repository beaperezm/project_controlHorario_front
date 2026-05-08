package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.VacacionesDTO;
import com.proyectodam.fichappclient.service.VacacionesService;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VacacionesAdminController {

    @FXML
    private Label estadoLabel;

    @FXML
    private TextField txtBuscarEmpleado;
    @FXML
    private VBox tarjetaEmpleadoPanel;
    @FXML
    private Label lblNombreEmpleado;
    @FXML
    private Label lblEmailEmpleado;
    @FXML
    private Label lblIdEmpleado;

    @FXML
    private TableView<VacacionesDTO> tablaSolicitudes;
    @FXML
    private TableColumn<VacacionesDTO, Integer> colId;
    @FXML
    private TableColumn<VacacionesDTO, String> colEmpleado;
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
    @FXML
    private TableColumn<VacacionesDTO, Void> colAcciones;

    private final ObservableList<VacacionesDTO> datosOriginales = FXCollections.observableArrayList();
    private FilteredList<VacacionesDTO> datosFiltrados;
    private final VacacionesService vacacionesService = new VacacionesService();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdSolicitud() != null ? c.getValue().getIdSolicitud() : 0).asObject());
        colEmpleado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreEmpleado()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colInicio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        colFin.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaFin()));
        colDias.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDiasHabiles() != null ? c.getValue().getDiasHabiles() : 0).asObject());
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        configurarColumnaAcciones();

        datosFiltrados = new FilteredList<>(datosOriginales, p -> true); // Inicialmente mostrar todos
        tablaSolicitudes.setItems(datosFiltrados);

        configurarBuscador();
        configurarSeleccionTabla();

        cargarDatos();
    }

    private void configurarBuscador() {
        txtBuscarEmpleado.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                // Limpiar filtro
                datosFiltrados.setPredicate(p -> true);
            } else {
                String filtro = newVal.toLowerCase().trim();
                
                // Aplicar predicado para filtrar tabla dinamicamente
                datosFiltrados.setPredicate(p -> {
                    if (p.getEmpleado() == null) return false;
                    String comboText = (p.getEmpleado().getNombre() + " " + p.getEmpleado().getApellidos() + " " + p.getEmpleado().getIdEmpleado()).toLowerCase();
                    return comboText.contains(filtro);
                });
            }
        });
    }

    private void configurarSeleccionTabla() {
        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.getEmpleado() != null) {
                EmpleadoDTO matchCandidato = newSelection.getEmpleado();
                lblNombreEmpleado.setText(matchCandidato.getNombre() + " " + (matchCandidato.getApellidos() != null ? matchCandidato.getApellidos() : ""));
                lblEmailEmpleado.setText(matchCandidato.getEmail() != null ? matchCandidato.getEmail() : "Sin email");
                lblIdEmpleado.setText(matchCandidato.getIdEmpleado() + "");

                tarjetaEmpleadoPanel.setVisible(true);
                tarjetaEmpleadoPanel.setManaged(true);
            } else {
                tarjetaEmpleadoPanel.setVisible(false);
                tarjetaEmpleadoPanel.setManaged(false);
            }
        });
    }

    private void cargarDatos() {
        estadoLabel.setText("Cargando solicitudes globales...");
        new Thread(() -> {
            try {
                List<VacacionesDTO> lista = vacacionesService.obtenerTodas();

                Platform.runLater(() -> {
                    datosOriginales.clear();
                    datosOriginales.addAll(lista);
                    
                    // Disparar un re-check del texto por si había algo escrito al refrescar
                    String textoActual = txtBuscarEmpleado.getText();
                    txtBuscarEmpleado.setText("");
                    if (textoActual != null && !textoActual.isEmpty()) {
                        txtBuscarEmpleado.setText(textoActual);
                    }

                    estadoLabel.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    estadoLabel.setText("Error al cargar datos.");
                    mostrarError("Error al cargar datos", e.getMessage());
                });
            }
        }).start();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("✅");
            private final Button btnDenegar = new Button("❌");
            private final HBox pane = new HBox(5, btnAprobar, btnDenegar);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                btnAprobar.setStyle("-fx-cursor: hand; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 4;");
                btnDenegar.setStyle("-fx-cursor: hand; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4;");

                btnAprobar.setOnAction(event -> {
                    VacacionesDTO doc = getTableView().getItems().get(getIndex());
                    aprobarSolicitud(doc);
                });

                btnDenegar.setOnAction(event -> {
                    VacacionesDTO doc = getTableView().getItems().get(getIndex());
                    denegarSolicitud(doc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    VacacionesDTO row = getTableView().getItems().get(getIndex());
                    if ("PENDIENTE".equalsIgnoreCase(row.getEstado())) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void aprobarSolicitud(VacacionesDTO doc) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas APROBAR la solicitud de " + doc.getNombreEmpleado() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        vacacionesService.aprobarSolicitud(doc.getIdSolicitud());
                        Platform.runLater(() -> {
                            mostrarInfo("Aprobada", "Solicitud aprobada correctamente e email enviado al empleado.");
                            cargarDatos();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> mostrarError("Error al aprobar", e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void denegarSolicitud(VacacionesDTO doc) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Denegar Solicitud");
        dialog.setHeaderText("Rechazar vacaciones de " + doc.getNombreEmpleado());
        dialog.setContentText("Motivo del rechazo:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(motivo -> {
            new Thread(() -> {
                try {
                    vacacionesService.rechazarSolicitud(doc.getIdSolicitud(), motivo);
                    Platform.runLater(() -> {
                        mostrarInfo("Denegada", "Solicitud rechazada e email enviado al empleado.");
                        cargarDatos();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error al rechazar", e.getMessage()));
                }
            }).start();
        });
    }

    @FXML
    private void onVerSolicitudesClick() {
        cargarDatos();
    }
    
    @FXML
    private void onLimpiarBusqueda() {
        txtBuscarEmpleado.clear();
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
