package com.proyectodam.fichappclient.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.HBox;

/**
 * Controlador para la visualización y gestión de documentos.
 * Muestra una tabla con documentos filtrables por tipo y grupo.
 */
public class ControladorDocumentos {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private ComboBox<String> groupCombo;

    @FXML
    private TableView<Documento> documentsTable;

    @FXML
    private Label paginationLabel;

    @FXML
    private TableColumn<Documento, Boolean> colSelect;

    @FXML
    private TableColumn<Documento, String> colName;

    @FXML
    private TableColumn<Documento, String> colType;

    @FXML
    private TableColumn<Documento, String> colGroup;

    @FXML
    private TableColumn<Documento, Void> colActions;

    @FXML
    public void initialize() {
        // Configuración de columnas
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colGroup.setCellValueFactory(new PropertyValueFactory<>("grupo"));

        // Celda personalizada para selección (CheckBox)
        colSelect.setCellFactory(column -> new TableCell<Documento, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Documento doc = getTableView().getItems().get(getIndex());
                    doc.setSelected(checkBox.isSelected());
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Documento doc = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(doc.isSelected());
                    setGraphic(checkBox);
                }
            }
        });

        // Celda personalizada para acciones
        colActions.setCellFactory(column -> new TableCell<Documento, Void>() {
            private final Button btnView = new Button("👁"); // Icono marcador de posición
            private final Button btnSettings = new Button("⚙"); // Icono marcador de posición
            private final Button btnDelete = new Button("🗑"); // Botón de borrar
            private final HBox pane = new HBox(5, btnView, btnSettings, btnDelete);

            {
                btnView.getStyleClass().add("boton-icono");
                btnSettings.getStyleClass().add("boton-icono");
                btnDelete.getStyleClass().add("boton-icono-peligro");

                btnDelete.setOnAction(event -> {
                    Documento doc = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Seguro que desea borrar el documento: " + doc.getNombre() + "?", ButtonType.YES,
                            ButtonType.NO);
                    confirm.showAndWait();
                    if (confirm.getResult() == ButtonType.YES) {
                        getTableView().getItems().remove(doc);
                    }
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

        // Add listener to update pagination label when items change
        documentsTable.getItems().addListener((ListChangeListener<Documento>) c -> actualizarEtiquetaPaginacion());

        // Initial update
        actualizarEtiquetaPaginacion();

        // Cargar datos de prueba
        documentsTable.setItems(getMockData());
    }

    private void actualizarEtiquetaPaginacion() {
        int total = documentsTable.getItems().size();
        if (total == 0) {
            paginationLabel.setText("No hay documentos");
        } else {
            // Asumiendo por ahora que se muestran todos u obtenemos paginación real
            paginationLabel.setText("Mostrando " + total + " de " + total);
        }
    }

    private ObservableList<Documento> getMockData() {
        ObservableList<Documento> data = FXCollections.observableArrayList();
        data.add(new Documento("Manual de Usuario", "PDF", "Recursos Humanos"));
        data.add(new Documento("Contrato Laboral", "Word", "Legal"));
        data.add(new Documento("Políticas de Empresa", "PDF", "General"));
        return data;
    }

    // Clase de modelo interna
    public static class Documento {
        private boolean selected;
        private String nombre;
        private String tipo;
        private String grupo;

        public Documento(String nombre, String tipo, String grupo) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.grupo = grupo;
            this.selected = false;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getGrupo() {
            return grupo;
        }

        public void setGrupo(String grupo) {
            this.grupo = grupo;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
