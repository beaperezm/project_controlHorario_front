package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
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
import com.proyectodam.fichappclient.model.DepartamentoDTO;
import java.util.stream.Collectors;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    @FXML
    private HBox searchEmpleadoBox;

    @FXML
    private HBox filterBox;

    @FXML
    private HBox yearComboBoxContainer;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private ComboBox<String> groupCombo;

    @FXML
    private Button btnCargarNomina;

    private NominaService nominaService;
    private DocumentoService documentoService;
    private ControlHorarioService controlHorarioService;

    private ObservableList<DocumentoDTO> nominasData = FXCollections.observableArrayList();
    private ObservableList<EmpleadoDTO> empleadosData = FXCollections.observableArrayList();
    
    // Paginación
    private int currentPage = 0;
    private int pageSize = 15;
    private long totalElements = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        nominaService = new NominaService();
        documentoService = new DocumentoService();
        controlHorarioService = new ControlHorarioService();

        configurarVistaSegunRol();

        // Listeners: en vez de filtrar en memoria, recargamos pagina con nuevos filtros
        searchEmpleadoField.textProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        searchField.textProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        yearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> { 
            if(newVal != null && !newVal.equals(oldVal)) {
                currentPage = 0; 
                cargarBackendData(); 
            }
        });
        if (groupCombo != null) {
            groupCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                currentPage = 0;
                cargarBackendData();
            });
        }

        colName.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        colMesAnio.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));

        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstadoFirma() != null ? cellData.getValue().getEstadoFirma().name()
                        : "PENDIENTE"));

        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button btnFirmar = new Button("F"); // F de Firmar
            private final Button btnDescargar = new Button("\u2B07"); // â¬‡
            private final Button btnAbrir = new Button("\uD83D\uDC41"); // ðŸ‘
            private final Button btnEnviar = new Button("\u2709"); // âœ‰
            private final Button btnEliminar = new Button("\uD83D\uDDD1"); // ðŸ—‘
            private final HBox pane = new HBox(5);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                btnFirmar.getStyleClass().add("boton-primario");
                btnDescargar.getStyleClass().add("boton-icono");
                btnAbrir.getStyleClass().add("boton-secundario");
                btnEnviar.getStyleClass().add("boton-primario");
                btnEliminar.getStyleClass().add("boton-secundario");

                String smallStyle = "-fx-font-size: 11px; -fx-padding: 3 6 3 6; -fx-min-width: 26px;";
                btnFirmar.setStyle(smallStyle);
                btnDescargar.setStyle(smallStyle);
                btnAbrir.setStyle(smallStyle);
                btnEnviar.setStyle(smallStyle + "-fx-background-color: #4CAF50;"); // Verde para enviar
                btnEliminar.setStyle(smallStyle + "-fx-background-color: #f44336; -fx-text-fill: white;"); // Rojo para borrar

                Tooltip.install(btnFirmar, new Tooltip("Firmar Nómina"));
                Tooltip.install(btnDescargar, new Tooltip("Descargar Nómina"));
                Tooltip.install(btnAbrir, new Tooltip("Abrir Nómina"));
                Tooltip.install(btnEnviar, new Tooltip("Enviar por Email"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar Nómina"));

                btnFirmar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    logicaParaFirmarEmpleado(doc);
                });

                btnDescargar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    descargarFichero(doc);
                });

                btnAbrir.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    abrirDocumento(doc);
                });

                btnEnviar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    enviarEmail(doc);
                });

                btnEliminar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    eliminarNomina(doc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    String rol = com.proyectodam.fichappclient.util.SessionData.getRolActual();
                    boolean isAdmin = "ADMIN".equalsIgnoreCase(rol);

                    pane.getChildren().clear();
                    if (isAdmin) {
                        pane.getChildren().addAll(btnAbrir, btnDescargar, btnEnviar, btnEliminar);
                    } else {
                        pane.getChildren().addAll(btnFirmar, btnDescargar, btnAbrir);
                        boolean firmado = doc.getEstadoFirma() == EstadoFirma.FIRMADO;
                        btnFirmar.setDisable(firmado);
                    }
                    setGraphic(pane);
                }
            }
        });

        nominasTable.getItems().addListener((ListChangeListener<DocumentoDTO>) c -> actualizarEtiquetaPaginacion());
        nominasTable.setItems(nominasData);

        cargarBackendData();
    }

    private void configurarVistaSegunRol() {
        String rol = com.proyectodam.fichappclient.util.SessionData.getRolActual();
        boolean isUser = "USER".equalsIgnoreCase(rol);

        yearComboBoxContainer.setVisible(true);
        yearComboBoxContainer.setManaged(true);

        if (isUser) {
            searchEmpleadoBox.setVisible(false);
            searchEmpleadoBox.setManaged(false);
            if (groupCombo != null) {
                groupCombo.setVisible(false);
                groupCombo.setManaged(false);
            }
            btnCargarNomina.setVisible(false);
            btnCargarNomina.setManaged(false);
        }
        
        cargarTiempos();
    }
    
    private void cargarTiempos() {
        new Thread(() -> {
            try {
                List<String> tiempos = documentoService.obtenerTiemposDisponibles("NOMINA");
                Platform.runLater(() -> {
                    ObservableList<String> years = FXCollections.observableArrayList("Todos");
                    if (tiempos != null) {
                        years.addAll(tiempos);
                    }
                    yearComboBox.setItems(years);
                    yearComboBox.setValue("Todos");
                });
            } catch (Exception e) {
                Platform.runLater(() -> System.err.println("Error cargando tiempos: " + e.getMessage()));
            }
        }).start();
    }

    private void cargarBackendData() {
        new Thread(() -> {
            try {
                // Para las subidas, necesitamos empleados
                if (empleadosData.isEmpty() && "ADMIN".equalsIgnoreCase(com.proyectodam.fichappclient.util.SessionData.getRolActual())) {
                    List<EmpleadoDTO> listadoEmpleados = controlHorarioService.getAllEmpleados();
                    Platform.runLater(() -> {
                        empleadosData.clear();
                        empleadosData.addAll(listadoEmpleados);
                    });
                }
                
                String qEmpleado = searchEmpleadoField.getText();
                String qArchivo = searchField.getText();
                String selectedYear = yearComboBox.getValue();
                String selectedDep = (groupCombo != null) ? groupCombo.getValue() : null;
                
                String rol = com.proyectodam.fichappclient.util.SessionData.getRolActual();
                Integer idEmpleadoActual = null;
                if ("USER".equalsIgnoreCase(rol)) {
                    idEmpleadoActual = com.proyectodam.fichappclient.util.SessionData.getEmpleadoActual() != null
                            ? com.proyectodam.fichappclient.util.SessionData.getEmpleadoActual().getIdEmpleado() : null;
                }
                
                // Cargar departamentos si es ADMIN y combo está vacío
                if ("ADMIN".equalsIgnoreCase(rol) && groupCombo != null && (groupCombo.getItems() == null || groupCombo.getItems().isEmpty())) {
                    List<DepartamentoDTO> listadoDepartamentos = controlHorarioService.getAllDepartamentos();
                    Platform.runLater(() -> {
                        List<String> deps = listadoDepartamentos.stream()
                                .map(DepartamentoDTO::getNombre)
                                .distinct().collect(Collectors.toList());
                        deps.add(0, "Todos");
                        groupCombo.setItems(FXCollections.observableArrayList(deps));
                        groupCombo.setButtonCell(new ListCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "Departamento" : item);
                            }
                        });
                        groupCombo.getSelectionModel().clearSelection();
                    });
                }

                com.proyectodam.fichappclient.model.PageResponseDTO<DocumentoDTO> pagina = 
                    documentoService.obtenerPaginados(currentPage, pageSize, "NOMINA", idEmpleadoActual, (qArchivo != null && !qArchivo.isEmpty() ? qArchivo : qEmpleado), selectedYear, selectedDep);

                Platform.runLater(() -> {
                    nominasData.clear();
                    if (pagina != null && pagina.getContent() != null) {
                        nominasData.addAll(pagina.getContent());
                        totalElements = pagina.getTotalElements();
                        totalPages = pagina.getTotalPages();
                    } else {
                        totalElements = 0;
                        totalPages = 0;
                    }
                    actualizarEtiquetaPaginacion();
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al cargar datos", e.getMessage()));
            }
        }).start();
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
                return upper; // Dejar como se escribió pero en mayúsculas
        }
    }

    private void ejecutarSubida(File file, String mesAnio, Integer idEmpleado) {
        new Thread(() -> {
            try {
                String customName = "Nomina_" + mesAnio + ".pdf";
                DocumentoDTO subido = documentoService.subirDocumento(file, customName, "NOMINA", idEmpleado);

                Platform.runLater(() -> {
                    if (subido != null) {
                        nominasData.add(0, subido); // Añadir al inicio
                        actualizarEtiquetaPaginacion();
                        mostrarInfo("Éxito", "Nómina subida correctamente.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al subir", e.getMessage()));
            }
        }).start();
    }

    private void logicaParaFirmarEmpleado(DocumentoDTO doc) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Firmar Nómina");
        dialog.setHeaderText("Firma segura para: " + doc.getNombreArchivo());
        dialog.setContentText("Por favor, introduzca su contraseña para firmar:");

        // Reemplazar campo de texto con campo de contraseña en TextInputDialog estándar
        // TextInputDialog estándar usa un TextField, queremos PasswordField por seguridad
        
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

    private void eliminarNomina(DocumentoDTO doc) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta nómina?");
        alert.setContentText("Esta acción no se puede deshacer: " + doc.getNombreArchivo());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    documentoService.eliminarDocumento(doc.getId());
                Platform.runLater(() -> {
                    nominasData.remove(doc);
                    actualizarEtiquetaPaginacion();
                    mostrarInfo("Éxito", "Nómina eliminada correctamente.");
                });
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error al eliminar", e.getMessage()));
                }
            }).start();
        }
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

    private void abrirDocumento(DocumentoDTO doc) {
        new Thread(() -> {
            try {
                byte[] content = documentoService.descargarDocumento(doc.getId());
                File tempFile = File.createTempFile("nomina_" + doc.getId(), ".pdf");
                java.nio.file.Files.write(tempFile.toPath(), content);

                Platform.runLater(() -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(tempFile);
                        } catch (IOException e) {
                            mostrarError("Error al abrir", "No se pudo abrir el archivo: " + e.getMessage());
                        }
                    } else {
                        mostrarInfo("Archivo descargado",
                                "El archivo se ha descargado en una ubicación temporal ya que no se soporta la apertura automática: "
                                        + tempFile.getAbsolutePath());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al descargar", e.getMessage()));
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
        int pageText = totalPages == 0 ? 0 : currentPage + 1;
        paginationLabel.setText("Página " + pageText + " de " + totalPages + " (Total: " + totalElements + ")");
    }
    
    @FXML
    public void paginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            cargarBackendData();
        }
    }
    
    @FXML
    public void paginaSiguiente() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            cargarBackendData();
        }
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
        yearComboBox.setValue("Todos");
        if (groupCombo != null) {
            groupCombo.getSelectionModel().clearSelection();
        }
        currentPage = 0;
        cargarBackendData();
    }

    @FXML
    private void handleBackToDashboard() {
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }
}
