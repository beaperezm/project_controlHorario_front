package com.proyectodam.fichappclient.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class VacacionesController {

    @FXML private Label estadoLabel;

    @FXML private TableView<VacacionesDTO> tablaSolicitudes;
    @FXML private TableColumn<VacacionesDTO, Integer> colId;
    @FXML private TableColumn<VacacionesDTO, String> colTipo;
    @FXML private TableColumn<VacacionesDTO, LocalDate> colInicio;
    @FXML private TableColumn<VacacionesDTO, LocalDate> colFin;
    @FXML private TableColumn<VacacionesDTO, Integer> colDias;
    @FXML private TableColumn<VacacionesDTO, String> colEstado;

    private final ObservableList<VacacionesDTO> datos = FXCollections.observableArrayList();
    private int autoincId = 1;

    @FXML
    private void initialize() {
        // Sin PropertyValueFactory => sin líos de módulos/reflexión
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colInicio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getInicio()));
        colFin.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFin()));
        colDias.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDias()).asObject());
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        tablaSolicitudes.setItems(datos);

        // Demo inicial
        datos.add(new VacacionesDTO(
                autoincId++, "VACACIONES",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7),
                6, "PENDIENTE"
        ));

        estadoLabel.setText("Listo.");
    }

    @FXML
    private void onSolicitarClick() {
        Dialog<VacacionesDTO> dialog = new Dialog<>();
        dialog.setTitle("Solicitar vacaciones");
        dialog.setHeaderText("Rellena los datos de la solicitud");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("VACACIONES", "AUSENCIA");
        cbTipo.getSelectionModel().selectFirst();

        DatePicker dpInicio = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker dpFin = new DatePicker(LocalDate.now().plusDays(2));

        TextArea taMotivo = new TextArea();
        taMotivo.setPromptText("Motivo (opcional)");
        taMotivo.setPrefRowCount(3);

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

        grid.add(new Label("Motivo:"), 0, 3);
        grid.add(taMotivo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Node okBtn = dialog.getDialogPane().lookupButton(btnGuardar);
        okBtn.disableProperty().bind(
                dpInicio.valueProperty().isNull().or(dpFin.valueProperty().isNull())
        );

        dialog.setResultConverter(button -> {
            if (button != btnGuardar) return null;

            LocalDate ini = dpInicio.getValue();
            LocalDate fin = dpFin.getValue();

            if (fin.isBefore(ini)) {
                new Alert(Alert.AlertType.ERROR,
                        "La fecha fin no puede ser anterior a la fecha inicio.").showAndWait();
                return null;
            }

            int dias = (int) ChronoUnit.DAYS.between(ini, fin) + 1;
            return new VacacionesDTO(autoincId++, cbTipo.getValue(), ini, fin, dias, "PENDIENTE");
        });

        Optional<VacacionesDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            datos.add(dto);
            estadoLabel.setText("Solicitud creada.");
        });
    }

    @FXML
    private void onVerSolicitudesClick() {
        estadoLabel.setText("Refrescar (pendiente de API)...");
    }

    // DTO simple
    public static class VacacionesDTO {
        private final int id;
        private final String tipo;
        private final LocalDate inicio;
        private final LocalDate fin;
        private final int dias;
        private final String estado;

        public VacacionesDTO(int id, String tipo, LocalDate inicio, LocalDate fin, int dias, String estado) {
            this.id = id;
            this.tipo = tipo;
            this.inicio = inicio;
            this.fin = fin;
            this.dias = dias;
            this.estado = estado;
        }

        public int getId() { return id; }
        public String getTipo() { return tipo; }
        public LocalDate getInicio() { return inicio; }
        public LocalDate getFin() { return fin; }
        public int getDias() { return dias; }
        public String getEstado() { return estado; }
    }
}