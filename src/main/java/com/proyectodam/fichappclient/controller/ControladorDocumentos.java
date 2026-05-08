package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.DocumentoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.DepartamentoDTO;
import com.proyectodam.fichappclient.service.DocumentoService;
import com.proyectodam.fichappclient.service.ControlHorarioService;
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
import java.util.stream.Collectors;

public class ControladorDocumentos {

    @FXML
    private TextField searchEmpleadoField;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private ComboBox<String> groupCombo;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private TableView<DocumentoDTO> documentsTable;

    @FXML
    private Label paginationLabel;

    @FXML
    private TableColumn<DocumentoDTO, Boolean> colSelect;

    @FXML
    private CheckBox selectAllCheck;

    @FXML
    private TableColumn<DocumentoDTO, String> colName;

    @FXML
    private TableColumn<DocumentoDTO, String> colType;

    @FXML
    private TableColumn<DocumentoDTO, String> colGroup; // Usa 'categoria'

    @FXML
    private TableColumn<DocumentoDTO, Void> colActions;

    private DocumentoService documentoService;
    private ControlHorarioService controlHorarioService;
    private ObservableList<DocumentoDTO> documentsData = FXCollections.observableArrayList();
    private ObservableList<EmpleadoDTO> empleadosData = FXCollections.observableArrayList();

    // Paginación
    private int currentPage = 0;
    private int pageSize = 15;
    private long totalElements = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        documentoService = new DocumentoService();
        controlHorarioService = new ControlHorarioService();

        // Listener para el filtrado reactivo de todos los campos
        searchEmpleadoField.textProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        searchField.textProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        groupCombo.valueProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });
        yearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> { currentPage = 0; cargarBackendData(); });

        // Lógica del CheckBox general 'Seleccionar Todos'
        if (selectAllCheck != null) {
            selectAllCheck.setOnAction(event -> {
                boolean isSelected = selectAllCheck.isSelected();
                for (DocumentoDTO doc : documentsTable.getItems()) {
                    doc.setSelected(isSelected);
                }
                documentsTable.refresh();
            });
        }

        // ColName pasa a ser "Nombre Empleado", enrutándolo al nuevo campo que envía el
        // backend
        colName.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));

        // ColType pasa a ser "Nombre Documento" (antes era mime type)
        colType.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));

        // ColGroup pasa a ser "Tipo de Documento" (antes era Categoría)
        colGroup.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategoria() != null ? cellData.getValue().getCategoria() : ""));

        // Celda personalizada para selección (CheckBox)
        colSelect.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    doc.setSelected(checkBox.isSelected());
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(doc.isSelected());
                    setGraphic(checkBox);
                }
            }
        });

        // Celda para acciones rápidas en cada fila
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button btnDescargar = new Button("↓");
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(5, btnDescargar, btnDelete);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                btnDescargar.getStyleClass().add("boton-icono");
                btnDelete.getStyleClass().add("boton-icono-peligro");

                btnDescargar.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    descargarFichero(doc);
                });

                btnDelete.setOnAction(event -> {
                    DocumentoDTO doc = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Seguro que desea borrar el documento: " + doc.getNombreArchivo() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            eliminarFichero(doc);
                        }
                    });
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

        documentsTable.getItems().addListener((ListChangeListener<DocumentoDTO>) c -> actualizarEtiquetaPaginacion());
        documentsTable.setItems(documentsData);

        // Carga inicial conectada a BBDD
        cargarTiempos();
        cargarBackendData();
    }
    
    private void cargarTiempos() {
        new Thread(() -> {
            try {
                List<String> tiempos = documentoService.obtenerTiemposDisponibles(null); // Obtener años de todas las categorías
                Platform.runLater(() -> {
                    ObservableList<String> years = FXCollections.observableArrayList("Todos");
                    if (tiempos != null) {
                        years.addAll(tiempos);
                    }
                    yearComboBox.setItems(years);
                    yearComboBox.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText((empty || item == null) ? "Tiempo" : item);
                        }
                    });
                    yearComboBox.getSelectionModel().clearSelection();
                });
            } catch (Exception e) {
                System.err.println("Error cargando tiempos en documentos: " + e.getMessage());
            }
        }).start();
    }

    private void cargarBackendData() {
        new Thread(() -> {
            try {
                // Cargar empleados solo si están vacíos para el diálogo de subida
                if (empleadosData.isEmpty()) {
                    List<EmpleadoDTO> listadoEmpleados = controlHorarioService.getAllEmpleados();
                    Platform.runLater(() -> {
                        empleadosData.clear();
                        EmpleadoDTO todos = new EmpleadoDTO();
                        todos.setIdEmpleado(-1);
                        todos.setNombre("Todos");
                        todos.setApellidos("los Empleados");
                        empleadosData.add(todos);
                        empleadosData.addAll(listadoEmpleados);
                    });
                }
                
                // Cargar departamentos para los combos si están vacíos
                if (groupCombo.getItems() == null || groupCombo.getItems().isEmpty()) {
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
                        
                        // Tipos fijos (se podrían parametrizar también)
                        typeCombo.setItems(FXCollections.observableArrayList("Todos", "CONTRATO", "CERTIFICADO", "PRL", "OTROS"));
                        typeCombo.setButtonCell(new ListCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "Tipo Documento" : item);
                            }
                        });
                        typeCombo.getSelectionModel().clearSelection();
                    });
                }

                String qEmpleado = searchEmpleadoField.getText();
                String qArchivo = searchField.getText();
                String qCategoria = typeCombo.getValue();
                if ("Todos".equalsIgnoreCase(qCategoria)) qCategoria = null;
                String selectedYear = yearComboBox.getValue();
                String selectedDep = groupCombo.getValue();
                
                // En Documentos filtramos sin categoría fija o con la del combo
                com.proyectodam.fichappclient.model.PageResponseDTO<DocumentoDTO> pagina = 
                    documentoService.obtenerPaginados(currentPage, pageSize, qCategoria, null, (qArchivo != null && !qArchivo.isEmpty() ? qArchivo : qEmpleado), selectedYear, selectedDep);

                Platform.runLater(() -> {
                    documentsData.clear();
                    if (pagina != null && pagina.getContent() != null) {
                        documentsData.addAll(pagina.getContent());
                        totalElements = pagina.getTotalElements();
                        totalPages = pagina.getTotalPages();
                    } else {
                        totalElements = 0;
                        totalPages = 0;
                    }
                    actualizarEtiquetaPaginacion();
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error al cargar datos de documentos", e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void accionSubirSintetica() {
        // En vez de buscar un id por defecto o desde el input de filtro, pasamos nulo
        // para que se escoja obligatoriamente en el Modal.
        mostrarDialogoSubida(null);
    }

    private void mostrarDialogoSubida(Integer idEmpleado) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cargar Documento");
        dialog.setHeaderText("Introduce los detalles del documento");

        // Botón de confirmación (oculto inicialmente para forzar el flujo del botón de
        // cargar archivo)
        ButtonType subirType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(subirType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nombreDocField = new TextField();
        nombreDocField.setPromptText("Nombre del documento (ej: Nómina Enero)");

        // Selector editable de Empleado (Autocompletado Rústico)
        ComboBox<EmpleadoDTO> empleadoCombo = new ComboBox<>();
        empleadoCombo.setPromptText("Elegir o buscar empleado...");
        List<EmpleadoDTO> empleadosReales = empleadosData.stream()
                .filter(e -> e.getIdEmpleado() != -1)
                .collect(Collectors.toList());

        empleadoCombo.getItems().addAll(empleadosReales);
        empleadoCombo.setEditable(true);
        empleadoCombo.setMaxWidth(Double.MAX_VALUE);

        // StringConverter estricto para evitar ClassCastException en editable=true
        empleadoCombo.setConverter(new javafx.util.StringConverter<EmpleadoDTO>() {
            @Override
            public String toString(EmpleadoDTO object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public EmpleadoDTO fromString(String string) {
                return empleadoCombo.getItems().stream()
                        .filter(e -> e.toString().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        // Lógica de Autocompletado Simple para empleadoCombo
        empleadoCombo.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                empleadoCombo.setItems(FXCollections.observableArrayList(empleadosReales));
            } else {
                List<EmpleadoDTO> autoCompletado = empleadosReales.stream().filter(e -> {
                    String full = (e.getNombre() + " " + e.getApellidos()).toLowerCase();
                    return full.contains(newV.toLowerCase());
                }).collect(Collectors.toList());
                // Evitamos el reseteo del texto al cambiar la lista
                empleadoCombo.setItems(FXCollections.observableArrayList(autoCompletado));
                empleadoCombo.show();
            }
        });

        ComboBox<String> categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Elegir tipo...");
        categoriaCombo.getItems().addAll("CONTRATO", "CERTIFICADO", "OTROS");
        categoriaCombo.setEditable(false);
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);

        Button btnAddCategory = new Button("+ Añadir");
        btnAddCategory.getStyleClass().add("boton-secundario");
        btnAddCategory.setOnAction(e -> {
            TextInputDialog input = new TextInputDialog();
            input.setTitle("Nuevo Tipo de Documento");
            input.setHeaderText("Añadir categoría personalizada");
            input.setContentText("Nombre del tipo:");
            input.showAndWait().ifPresent(tipo -> {
                if (!tipo.trim().isEmpty()) {
                    categoriaCombo.getItems().add(tipo.toUpperCase());
                    categoriaCombo.getSelectionModel().select(tipo.toUpperCase());
                }
            });
        });

        // HBox para contener el ComboBox de categoría y el botón de añadir lado a lado
        HBox categoriaBox = new HBox(10, categoriaCombo, btnAddCategory);
        categoriaBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button btnOpenFile = new Button("Cargar archivo");
        btnOpenFile.getStyleClass().add("boton-primario");
        btnOpenFile.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
        btnOpenFile.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Seleccionar Empleado:"), 0, 0);
        grid.add(empleadoCombo, 1, 0);
        grid.add(new Label("Nombre que saldrá en búsquedas:"), 0, 1);
        grid.add(nombreDocField, 1, 1);
        grid.add(new Label("Elegir tipo de documento:"), 0, 2);
        grid.add(categoriaBox, 1, 2);
        grid.add(new Label("Seleccionar archivo:"), 0, 3);
        grid.add(btnOpenFile, 1, 3);

        dialog.getDialogPane().setContent(grid);

        btnOpenFile.setOnAction(e -> {
            String nombreCustom = nombreDocField.getText();
            String cat = categoriaCombo.getValue();
            EmpleadoDTO empleadoSel = empleadoCombo.getSelectionModel().getSelectedItem();

            if (nombreCustom == null || nombreCustom.trim().isEmpty() || cat == null || empleadoSel == null) {
                mostrarError("Campos incompletos",
                        "Por favor, busca y selecciona un empleado válido, rellena el nombre y selecciona un tipo.");
                return;
            }

            Integer finalTargetId = empleadoSel.getIdEmpleado();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo");
            File file = fileChooser.showOpenDialog(dialog.getOwner());

            if (file != null) {
                dialog.close(); // Cerramos el diálogo tras elegir
                ejecutarSubida(file, nombreCustom, cat, finalTargetId);
            }
        });

        dialog.showAndWait();
    }

    private void ejecutarSubida(File file, String nombreCustom, String categoria, Integer idEmpleado) {
        new Thread(() -> {
            try {
                DocumentoDTO subido = documentoService.subirDocumento(file, nombreCustom, categoria, idEmpleado);

                Platform.runLater(() -> {
                    documentsData.add(0, subido);
                    actualizarEtiquetaPaginacion();
                    mostrarInfo("Éxito", "Documento '" + nombreCustom + "' subido correctamente.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error en la subida", e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void accionSubirDocumento(Integer idEmpleado) {
        mostrarDialogoSubida(idEmpleado);
    }

    private void descargarFichero(DocumentoDTO doc) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Documento");
        fileChooser.setInitialFileName(doc.getNombreArchivo());
        File fileToSave = fileChooser.showSaveDialog(null);

        if (fileToSave != null) {
            new Thread(() -> {
                try {
                    byte[] bytes = documentoService.descargarDocumento(doc.getId());
                    try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                        fos.write(bytes);
                    }
                    Platform.runLater(() -> mostrarInfo("Descarga completada",
                            "El archivo se ha guardado en: " + fileToSave.getAbsolutePath()));
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarError("Error de descarga", e.getMessage()));
                }
            }).start();
        }
    }

    private void eliminarFichero(DocumentoDTO doc) {
        new Thread(() -> {
            try {
                documentoService.eliminarDocumento(doc.getId());
                Platform.runLater(() -> {
                    documentsData.remove(doc);
                    actualizarEtiquetaPaginacion();
                    mostrarInfo("Eliminado", "El archivo fue borrado exitosamente del sistema.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarError("Error de borrado", e.getMessage()));
            }
        }).start();
    }

    private void actualizarEtiquetaPaginacion() {
        int pageText = totalPages == 0 ? 0 : currentPage + 1;
        paginationLabel.setText("Página " + pageText + " de " + totalPages + " (Total: " + totalElements + ")");
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
        typeCombo.getSelectionModel().clearSelection();
        groupCombo.getSelectionModel().clearSelection();
        yearComboBox.getSelectionModel().clearSelection();
        currentPage = 0;
        cargarBackendData();
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

    @FXML
    public void borrarSeleccion() {
        List<DocumentoDTO> aBorrar = getDocumentsTable().getItems().stream()
                .filter(DocumentoDTO::isSelected)
                .collect(Collectors.toList());

        if (aBorrar.isEmpty()) {
            mostrarInfo("Aviso", "No hay ningún documento seleccionado.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que desea borrar los " + aBorrar.size() + " documentos seleccionados?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                aBorrar.forEach(this::eliminarFichero);
            }
        });
    }

    private TableView<DocumentoDTO> getDocumentsTable() {
        return documentsTable;
    }
}
