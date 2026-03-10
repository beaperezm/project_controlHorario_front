package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.RecordatorioDTO;
import com.proyectodam.fichappclient.util.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControladorCalendario {

    @FXML
    private Label mesLabel;
    @FXML
    private GridPane gridCalendario;

    private YearMonth currentYearMonth;
    private List<RecordatorioDTO> recordatoriosMes = new ArrayList<>();
    private ObjectMapper mapper;

    @FXML
    public void initialize() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Para LocalDate
        currentYearMonth = YearMonth.now();
        cargarMes(currentYearMonth);
    }

    @FXML
    public void mesAnterior() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        cargarMes(currentYearMonth);
    }

    @FXML
    public void mesSiguiente() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        cargarMes(currentYearMonth);
    }

    private void cargarMes(YearMonth yearMonth) {
        String tituloMes = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase()
                + " " + yearMonth.getYear();
        mesLabel.setText(tituloMes);

        // Limpiar el grid antes de pedir del backend
        pintarCalendario(yearMonth, new ArrayList<>());

        // Llamar al backend
        new Thread(() -> {
            try {
                ApiClient apiClient = new ApiClient();
                String url = "/recordatorios?year=" + yearMonth.getYear() + "&month=" + yearMonth.getMonthValue();
                String resBody = apiClient.get(url);

                if (resBody != null && !resBody.isEmpty()) {
                    List<RecordatorioDTO> lista = mapper.readValue(resBody,
                            new TypeReference<List<RecordatorioDTO>>() {
                            });
                    Platform.runLater(() -> {
                        recordatoriosMes = lista;
                        pintarCalendario(yearMonth, recordatoriosMes);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void pintarCalendario(YearMonth yearMonth, List<RecordatorioDTO> recordatorios) {
        gridCalendario.getChildren().clear();

        // Nombres de los días de la semana
        String[] diasNombres = { "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom" };
        for (int i = 0; i < diasNombres.length; i++) {
            Label lbl = new Label(diasNombres[i]);
            lbl.getStyleClass().add("etiqueta-dia-semana");
            gridCalendario.add(lbl, i, 0);
        }

        LocalDate primerDiaMes = yearMonth.atDay(1);
        int offset = primerDiaMes.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (offset < 0)
            offset += 7; // Lunes = 1 (getValue devuelve 1 a 7)

        int diasEnMes = yearMonth.lengthOfMonth();
        int row = 1;
        int col = offset;

        // Celdas pre-mes
        for (int i = 0; i < offset; i++) {
            gridCalendario.add(crearCeldaVacia(), i, row);
        }

        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fechaActual = yearMonth.atDay(dia);
            VBox celda = crearCeldaDia(fechaActual, recordatorios);

            gridCalendario.add(celda, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        // Celdas post-mes
        while (row < 7) {
            gridCalendario.add(crearCeldaVacia(), col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private VBox crearCeldaVacia() {
        VBox vbox = new VBox();
        vbox.setMinHeight(80);
        vbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vbox.getStyleClass().add("celda-vacia");
        return vbox;
    }

    private VBox crearCeldaDia(LocalDate fecha, List<RecordatorioDTO> recordatorios) {
        VBox vbox = new VBox(5); // Espacio entre elementos de la celda
        vbox.setMinHeight(80);
        vbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vbox.getStyleClass().add("celda-dia");

        // Clic para crear recordatorio
        vbox.setOnMouseClicked(e -> {
            mostrarDialogoRecordatorio(fecha);
        });

        Label lbl = new Label(String.valueOf(fecha.getDayOfMonth()));

        if (fecha.equals(LocalDate.now())) {
            lbl.getStyleClass().add("etiqueta-dia-hoy");
        } else {
            lbl.getStyleClass().add("etiqueta-dia-numero");
        }

        vbox.getChildren().add(lbl);

        // Comprobar recordatorios para este día
        for (RecordatorioDTO rec : recordatorios) {
            if (rec.getFecha().equals(fecha)) {
                Label lblRec = new Label("• " + rec.getTitulo());
                lblRec.getStyleClass().add("etiqueta-recordatorio");
                vbox.getChildren().add(lblRec);
            }
        }

        return vbox;
    }

    private void mostrarDialogoRecordatorio(LocalDate fecha) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestión de Recordatorios");
        dialog.setHeaderText("Recordatorios para el " + fecha.toString());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setPadding(new javafx.geometry.Insets(10));

        // Buscar recordatorios existentes
        List<RecordatorioDTO> recsDia = recordatoriosMes.stream().filter(r -> r.getFecha().equals(fecha)).toList();

        if (!recsDia.isEmpty()) {
            content.getChildren().add(new Label("Recordatorios programados:"));
            for (RecordatorioDTO rec : recsDia) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label txt = new Label(
                        rec.getTitulo() + (rec.getDescripcion() != null ? " - " + rec.getDescripcion() : ""));
                Button btnBorrar = new Button("Borrar");
                btnBorrar.setStyle("-fx-text-fill: red;");
                btnBorrar.setOnAction(e -> borrarRecordatorio(rec.getId(), fecha, dialog));
                row.getChildren().addAll(txt, btnBorrar);
                content.getChildren().add(row);
            }
            content.getChildren().add(new Separator());
        }

        Label tNuevo = new Label("Añadir nuevo:");
        tNuevo.setStyle("-fx-font-weight: bold;");
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Título del recordatorio");
        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Descripción opcional");
        Button btnGuardar = new Button("Guardar Recordatorio");
        btnGuardar.setStyle("-fx-background-color: #00897b; -fx-text-fill: white;");

        btnGuardar.setOnAction(e -> {
            if (txtTitulo.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "El título es obligatorio");
                a.show();
                return;
            }
            guardarNuevoRecordatorio(txtTitulo.getText(), txtDesc.getText(), fecha, dialog);
        });

        content.getChildren().addAll(tNuevo, txtTitulo, txtDesc, btnGuardar);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void guardarNuevoRecordatorio(String titulo, String desc, LocalDate fecha, Dialog<ButtonType> dialog) {
        RecordatorioDTO dto = new RecordatorioDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(desc);
        dto.setFecha(fecha);
        // El idEmpleado podría setearse si tuviéramos acceso a los datos de la sesión
        // actuall. Opcional por ahora.

        new Thread(() -> {
            try {
                ApiClient apiClient = new ApiClient();
                String json = mapper.writeValueAsString(dto);
                apiClient.post("/recordatorios", json);

                Platform.runLater(() -> {
                    dialog.close();
                    cargarMes(currentYearMonth); // Recargar
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void borrarRecordatorio(Long id, LocalDate fecha, Dialog<ButtonType> dialog) {
        new Thread(() -> {
            try {
                ApiClient apiClient = new ApiClient();
                apiClient.delete("/recordatorios/" + id);
                Platform.runLater(() -> {
                    dialog.close();
                    cargarMes(currentYearMonth);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
