package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.DocumentoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.service.ControlHorarioService;
import com.proyectodam.fichappclient.service.DocumentoService;
import com.proyectodam.fichappclient.service.NominaService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.proyectodam.fichappclient.enums.EstadoFirma;

public class ControladorNominas {

    @FXML
    private TextField searchEmpleadoField;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<DocumentoDTO> nominasTable;

    @FXML
    private Label paginationLabel;

    @FXML
    private TableColumn<DocumentoDTO, String> colName;

    @FXML
    private TableColumn<DocumentoDTO, String> colMesAnio;

    @FXML
    private TableColumn<DocumentoDTO, String> colEstado;

    @FXML
    private TableColumn<DocumentoDTO, Void> colActions;

    private NominaService nominaService;
    private DocumentoService documentoService;
    private ControlHorarioService controlHorarioService;

    private ObservableList<DocumentoDTO> nominasData = FXCollections.observableArrayList();
    private ObservableList<EmpleadoDTO> empleadosData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nominaService = new NominaService();
        documentoService = new DocumentoService();
        controlHorarioService = new ControlHorarioService();

        searchEmpleadoField.textProperty().addListener((obs, oldVal, newVal) -> filtrarTabla());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrarTabla());

        colName.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        colMesAnio.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));

        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstadoFirma() != null ? cellData.getValue().getEstadoFirma().name()
                        : "PENDIENTE"));

        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button btnDescargar = new Button("\u2B07"); // ⬇
            private final Button btnEmail = new Button("\u2709"); // ✉
            private final Button btnEditar = new Button("\u270E"); // ✎
            private final HBox pane = new HBox(5, btnDescargar, btnEmail, btnEditar);

            {
                btnDescargar.getStyleClass().add("boton-icono");
                btnEmail.getStyleClass().add("boton-secundario");
                btnEditar.getStyleClass().add("boton-primario");

                btnDescargar.setMaxWidth(Double.MAX_VALUE);
                btnEmail.setMaxWidth(Double.MAX_VALUE);
                btnEditar.setMaxWidth(Double.MAX_VALUE);

                String smallStyle = "-fx-font-size: 11px; -fx-padding: 3 6 3 6; -fx-min-width: 26px;";
                btnDescargar.setStyle(smallStyle);
                btnEmail.setStyle(smallStyle);
                btnEditar.setStyle(smallStyle);

                javafx.scene.layout.HBox.setHgrow(btnDescargar, javafx.scene.layout.Priority.ALWAYS);
                javafx.scene.layout.HBox.setHgrow(btnEmail, javafx.scene.layout.Priority.ALWAYS);
                javafx.scene.layout.HBox.setHgrow(btnEditar, javafx.scene.layout.Priority.ALWAYS);

                btnDescargar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    descargarFichero(doc);
                });

                btnEmail.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    enviarEmail(doc);
                });

                btnEditar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    mostrarDialogoEditar(doc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        nominasTable.getItems().addListener((ListChangeListener<DocumentoDTO>) c -> actualizarEtiquetaPaginacion());
        nominasTable.setItems(nominasData);

        cargarBackendData();
    }

    private void cargarBackendData() {
        new Thread(() -> {
            try {
                List<EmpleadoDTO> listadoEmpleados = controlHorarioService.getAllEmpleados();
                List<DocumentoDTO> listadoBackend = documentoService.listarTodos();

                Platform.runLater(() -> {
                    empleadosData.clear();
                    empleadosData.addAll(listadoEmpleados);

                    nominasData.clear();
                    nominasData.addAll(listadoBackend.stream()
                            .filter(d -> "NOMINA".equalsIgnoreCase(d.getCategoria()))
                            .collect(Collectors.toList()));

                    filtrarTabla();
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al cargar datos", e.getMessage()));
            }
        }).start();
    }

    private void filtrarTabla() {
        String textoBuscEmpleado = searchEmpleadoField.getText();
        String qEmpleado = (textoBuscEmpleado != null) ? textoBuscEmpleado.toLowerCase().trim() : "";

        String textoBuscArchivo = searchField.getText();
        String qArchivo = (textoBuscArchivo != null) ? textoBuscArchivo.toLowerCase().trim() : "";

        List<Integer> idsEmpleadosCoincidentes = empleadosData.stream()
                .filter(e -> (e.getNombre() != null && e.getNombre().toLowerCase().contains(qEmpleado)) ||
                        (e.getApellidos() != null && e.getApellidos().toLowerCase().contains(qEmpleado)))
                .map(EmpleadoDTO::getIdEmpleado)
                .collect(Collectors.toList());

        ObservableList<DocumentoDTO> filtrados = FXCollections.observableArrayList();

        for (DocumentoDTO doc : nominasData) {
            boolean matchEmpleado = qEmpleado.isEmpty()
                    || (doc.getIdEmpleado() != null && idsEmpleadosCoincidentes.contains(doc.getIdEmpleado()));
            boolean matchArchivo = qArchivo.isEmpty()
                    || (doc.getNombreArchivo() != null && doc.getNombreArchivo().toLowerCase().contains(qArchivo));

            if (matchEmpleado && matchArchivo) {
                filtrados.add(doc);
            }
        }
        nominasTable.setItems(filtrados);
        actualizarEtiquetaPaginacion();
    }

    @FXML
    public void accionSubirNomina() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cargar Nómina");
        dialog.setHeaderText("Introduce los detalles de la nómina a subir");

        ButtonType subirType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(subirType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField mesField = new TextField();
        mesField.setPromptText("Mes (ej. 1 o Enero)");
        mesField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                mesField.setText(parsearMes(mesField.getText()));
            }
        });

        TextField anioField = new TextField();
        anioField.setPromptText("Año (ej. 2024)");

        ComboBox<EmpleadoDTO> empleadoCombo = new ComboBox<>();
        empleadoCombo.setPromptText("Seleccionar empleado...");
        empleadoCombo.getItems().addAll(empleadosData);

        Button btnOpenFile = new Button("Cargar archivo");
        btnOpenFile.getStyleClass().add("boton-primario");
        btnOpenFile.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Seleccionar Empleado:"), 0, 0);
        grid.add(empleadoCombo, 1, 0);
        grid.add(new Label("Mes y Año:"), 0, 1);

        HBox mesAnioBox = new HBox(10, mesField, anioField);
        grid.add(mesAnioBox, 1, 1);

        grid.add(new Label("Seleccionar PDF:"), 0, 2);
        grid.add(btnOpenFile, 1, 2);

        dialog.getDialogPane().setContent(grid);

        btnOpenFile.setOnAction(e -> {
            String mes = parsearMes(mesField.getText());
            String anio = anioField.getText().trim();
            EmpleadoDTO empleadoSel = empleadoCombo.getSelectionModel().getSelectedItem();

            if (mes.isEmpty() || anio.isEmpty() || empleadoSel == null) {
                mostrarError("Campos incompletos", "Por favor, selecciona un empleado y completa el mes y año.");
                return;
            }

            String mesAnioFinal = mes.toUpperCase() + " " + anio;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Nómina (PDF)");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(dialog.getOwner());

            if (file != null) {
                dialog.close();
                ejecutarSubida(file, mesAnioFinal, empleadoSel.getIdEmpleado());
            }
        });

        dialog.showAndWait();
    }

    private String parsearMes(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        String upper = input.trim().toUpperCase();
        switch (upper) {
            case "1":
            case "01":
                return "ENERO";
            case "2":
            case "02":
                return "FEBRERO";
            case "3":
            case "03":
                return "MARZO";
            case "4":
            case "04":
                return "ABRIL";
            case "5":
            case "05":
                return "MAYO";
            case "6":
            case "06":
                return "JUNIO";
            case "7":
            case "07":
                return "JULIO";
            case "8":
            case "08":
                return "AGOSTO";
            case "9":
            case "09":
                return "SEPTIEMBRE";
            case "10":
                return "OCTUBRE";
            case "11":
                return "NOVIEMBRE";
            case "12":
                return "DICIEMBRE";
            default:
                return upper; // Leave as typed but uppercase
        }
    }

    private void ejecutarSubida(File file, String mesAnio, Integer idEmpleado) {
        new Thread(() -> {
            try {
                // Since upload behavior involves multipart which we didn't add directly in
                // NominaService right now, we can use DocumentoService.subirDocumento with
                // Categoria == NOMINA,
                // but wait, we made a custom endpoint `/api/nominas/upload`.
                // Actually, standard `/api/documentos/upload` still works if we pass
                // Categoria="NOMINA" and nombre="Nomina_Enero 2024.pdf".
                // I'll reuse DocumentoService for simplicity!
                String customName = "Nomina_" + mesAnio + ".pdf";
                DocumentoDTO subido = documentoService.subirDocumento(file, customName, "NOMINA", idEmpleado);

                Platform.runLater(() -> {
                    nominasData.add(subido);
                    filtrarTabla();
                    mostrarInfo("Éxito", "Nómina subida correctamente.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error en la subida", e.getMessage()));
            }
        }).start();
    }

    // TODO: LÓGICA PARA PANEL DE EMPLEADO
    @SuppressWarnings("unused")
    private void logicaParaFirmarEmpleado(DocumentoDTO doc) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Firmar Nómina");
        dialog.setHeaderText("Firma segura para: " + doc.getNombreArchivo());
        dialog.setContentText("Por favor, introduzca su contraseña para firmar:");

        // Replace text field with password field in standard TextInputDialog
        // Standard TextInputDialog uses a TextField, we want PasswordField for security
        Dialog<String> passDialog = new Dialog<>();
        passDialog.setTitle("Firmar Nómina");
        passDialog.setHeaderText("Validación de seguridad para firmar: " + doc.getNombreArchivo());
        ButtonType firmarButtonType = new ButtonType("Firmar", ButtonBar.ButtonData.OK_DONE);
        passDialog.getDialogPane().getButtonTypes().addAll(firmarButtonType, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Contraseña");
        HBox hBox = new HBox();
        hBox.getChildren().add(pwd);
        hBox.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));
        passDialog.getDialogPane().setContent(hBox);

        passDialog.setResultConverter(dialogButton -> {
            if (dialogButton == firmarButtonType) {
                return pwd.getText();
            }
            return null;
        });

        Optional<String> result = passDialog.showAndWait();
        result.ifPresent(password -> {
            new Thread(() -> {
                try {
                    nominaService.signNomina(doc.getId(), doc.getIdEmpleado(), password);
                    Platform.runLater(() -> {
                        doc.setEstadoFirma(EstadoFirma.FIRMADO);
                        nominasTable.refresh();
                        mostrarInfo("Firmado", "La nómina ha sido firmada correctamente.");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error al firmar", e.getMessage()));
                }
            }).start();
        });
    }

    private void mostrarDialogoEditar(DocumentoDTO doc) {
        TextInputDialog dialog = new TextInputDialog(doc.getNombreArchivo());
        dialog.setTitle("Editar Nómina");
        dialog.setHeaderText("Editar el nombre de la nómina:");
        dialog.setContentText("Nuevo nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nuevoNombre -> {
            if (!nuevoNombre.trim().isEmpty() && !nuevoNombre.equals(doc.getNombreArchivo())) {
                // TODO: Implementar llamada al backend para actualizar el nombre real en base
                // de datos.
                // apiClient.put("/documentos/" + doc.getId() + "/rename?newName=" +
                // nuevoNombre);

                doc.setNombreArchivo(nuevoNombre);
                nominasTable.refresh();
                mostrarInfo("Edición", "Nombre actualizado localmente (Falta endpoint en backend).");
            }
        });
    }

    private void enviarEmail(DocumentoDTO doc) {
        new Thread(() -> {
            try {
                nominaService.sendEmail(doc.getId());
                Platform.runLater(() -> {
                    doc.setEstadoFirma(EstadoFirma.NOTIFICADA);
                    nominasTable.refresh();
                    mostrarInfo("Email Enviado", "La notificación ha sido enviada al empleado.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al enviar email", e.getMessage()));
            }
        }).start();
    }

    private void descargarFichero(DocumentoDTO doc) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Nómina");
        fileChooser.setInitialFileName(doc.getNombreArchivo());
        File fileToSave = fileChooser.showSaveDialog(null);

        if (fileToSave != null) {
            new Thread(() -> {
                try {
                    byte[] bytes = documentoService.descargarDocumento(doc.getId());
                    try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                        fos.write(bytes);
                    }
                    Platform.runLater(
                            () -> mostrarInfo("Descarga completada", "El archivo se ha guardado exitosamente."));
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error de descarga", e.getMessage()));
                }
            }).start();
        }
    }

    private void actualizarEtiquetaPaginacion() {
        int total = nominasTable.getItems().size();
        paginationLabel.setText("Mostrando " + total + " de " + total);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void limpiarFiltros() {
        searchEmpleadoField.clear();
        searchField.clear();
        filtrarTabla();
    }
}
