package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.ApiClient;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class InformesController {

    @FXML private ComboBox<String> cbTipoInforme;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private TextField txtBuscarEmpleado;
    @FXML private ComboBox<EmpleadoDTO> cbEmpleado;
    @FXML private Label lblEmpleado;
    @FXML private Label lblBuscar;

    private final ApiClient apiClient = new ApiClient();
    private final com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService empleadoService = 
            new com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadorService();
    
    private javafx.collections.transformation.FilteredList<EmpleadoDTO> filteredEmployees;

    @FXML
    public void initialize() {
        // Cargar tipos de informes
        cbTipoInforme.setItems(FXCollections.observableArrayList(
                "Registro Diario de Jornada (PDF)",
                "Resumen Mensual de Horas (Excel)"
        ));

        // Configurar selección por defecto
        cbTipoInforme.getSelectionModel().selectFirst();

        // Inicializar y cargar empleados
        cargarYConfigurarEmpleados();
        
        // Valores por defecto de fechas
        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());

        // Lógica de visibilidad dinámica
        cbTipoInforme.valueProperty().addListener((obs, oldVal, newVal) -> {
            actualizarVisibilidadFiltros(newVal);
        });

        // Estado inicial
        actualizarVisibilidadFiltros(cbTipoInforme.getValue());
    }

    private void cargarYConfigurarEmpleados() {
        try {
            javafx.collections.ObservableList<EmpleadoDTO> allEmployees = SessionData.getInstance().getEmpleados();
            
            // Si la sesión está vacía (p.ej. navegación directa), cargamos desde API
            if (allEmployees.isEmpty()) {
                List<EmpleadoDTO> list = empleadoService.getAllEmpleados();
                    // El backend ya devuelve la lista filtrada de cuentas de sistema
                    allEmployees.addAll(list);
            }

            filteredEmployees = new javafx.collections.transformation.FilteredList<>(allEmployees, p -> true);
            cbEmpleado.setItems(filteredEmployees);

            // Buscador reactivo
            txtBuscarEmpleado.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredEmployees.setPredicate(emp -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String filter = newVal.toLowerCase();
                    String nombreCompleto = (emp.getNombre() + " " + emp.getApellidos()).toLowerCase();
                    String dni = emp.getDni() != null ? emp.getDni().toLowerCase() : "";
                    return nombreCompleto.contains(filter) || dni.contains(filter);
                });
                
                // Si hay resultados y solo es uno, lo seleccionamos automáticamente para agilizar
                if (filteredEmployees.size() == 1) {
                    cbEmpleado.getSelectionModel().select(0);
                }
            });

            // Configurar visualización en el ComboBox
            javafx.util.Callback<ListView<EmpleadoDTO>, ListCell<EmpleadoDTO>> cellFactory = lv -> new ListCell<>() {
                @Override
                protected void updateItem(EmpleadoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String dni = item.getDni() != null ? " [" + item.getDni() + "]" : "";
                        setText(item.getNombre() + " " + item.getApellidos() + dni);
                        setStyle("-fx-text-fill: #2c3e50; -fx-padding: 5 10;");
                    }
                }
            };

            cbEmpleado.setCellFactory(cellFactory);
            cbEmpleado.setButtonCell(cellFactory.call(null));

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudieron obtener los empleados del sistema.");
        }
    }

    private void actualizarVisibilidadFiltros(String tipo) {
        boolean esIndividual = tipo != null && tipo.contains("PDF");
        cbEmpleado.setVisible(esIndividual);
        lblEmpleado.setVisible(esIndividual);
        txtBuscarEmpleado.setVisible(esIndividual);
        lblBuscar.setVisible(esIndividual);
        
        // Si es Excel, limpiar selección de empleado para evitar confusiones
        if (!esIndividual) {
            cbEmpleado.getSelectionModel().clearSelection();
            txtBuscarEmpleado.clear();
        }
    }

    @FXML
    private void handleLimpiar() {
        cbTipoInforme.getSelectionModel().selectFirst();
        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());
        cbEmpleado.getSelectionModel().clearSelection();
        txtBuscarEmpleado.clear();
    }

    @FXML
    private void handleGenerarPDF() {
        String tipo = cbTipoInforme.getValue();
        if (tipo == null || !tipo.contains("PDF")) {
            AlertUtils.mostrarAdvertencia("Validación", "Por favor, seleccione un informe tipo PDF.");
            return;
        }

        EmpleadoDTO emp = cbEmpleado.getValue();
        if (emp == null) {
            AlertUtils.mostrarAdvertencia("Validación", "Debe seleccionar un empleado para este informe.");
            return;
        }

        descargarInforme("/reports/jornada/" + emp.getIdEmpleado() 
                + "?inicio=" + dpInicio.getValue() + "&fin=" + dpFin.getValue(), 
                "Registro_Jornada_" + emp.getNombre() + ".pdf", "PDF (*.pdf)", "*.pdf");
    }

    @FXML
    private void handleExportarExcel() {
        String tipo = cbTipoInforme.getValue();
        if (tipo == null || !tipo.contains("Excel")) {
            AlertUtils.mostrarAdvertencia("Validación", "Por favor, seleccione un informe tipo Excel.");
            return;
        }

        // Para el resumen mensual usamos el año y mes del selector de inicio
        int mes = dpInicio.getValue().getMonthValue();
        int anio = dpInicio.getValue().getYear();

        descargarInforme("/reports/empresa/2/mensual?mes=" + mes + "&anio=" + anio, 
                "Resumen_Mensual_" + mes + "_" + anio + ".xlsx", "Excel (*.xlsx)", "*.xlsx");
    }

    private void descargarInforme(String endpoint, String defaultFileName, String filterDesc, String filterExt) {
        try {
            byte[] data = apiClient.download(endpoint);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Informe");
            fileChooser.setInitialFileName(defaultFileName);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterDesc, filterExt));
            
            File file = fileChooser.showSaveDialog(cbTipoInforme.getScene().getWindow());
            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data);
                }
                AlertUtils.mostrarInfo("Éxito", "Informe guardado correctamente en: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            AlertUtils.mostrarError("Error", "No se pudo generar el informe: " + e.getMessage());
        }
    }
}
