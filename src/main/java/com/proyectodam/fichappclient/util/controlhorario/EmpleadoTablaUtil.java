package com.proyectodam.fichappclient.util.controlhorario;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

public class EmpleadoTablaUtil {


    public static void tablaEmpleado(TableView<EmpleadoDTO> tablaEmpleados, ObservableList<EmpleadoDTO> listaEmpleados, TableColumn<EmpleadoDTO, String> columnaParaNombre, TableColumn<EmpleadoDTO, String> columnaParaApellido, TableColumn<EmpleadoDTO, String> columnaParaCorreo, TableColumn<EmpleadoDTO, String> columnaParaDireccion, TableColumn<EmpleadoDTO, String> columnaParaTelefono, TableColumn<EmpleadoDTO, String> columnaParaDepartamento, TableColumn<EmpleadoDTO, String> columnaParaRol, TableColumn<EmpleadoDTO, String> columnaParaDni, TableColumn<EmpleadoDTO, String> columnaParaFechaAlta, TableColumn<EmpleadoDTO, String> columnaParaFechaNacimiento, TableColumn<EmpleadoDTO, String> columnaParaEstado) {

        tablaEmpleados.setItems(listaEmpleados);

        columnaParaNombre.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getNombre()));
        columnaParaApellido.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getApellidos()));
        columnaParaCorreo.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getEmail()));
        columnaParaDireccion.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getDireccion()));
        columnaParaTelefono.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getTelefono()));
        columnaParaDepartamento.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getDepartamentoDTO() != null ? altaRapida.getValue().getDepartamentoDTO().getNombre() : ""));
        columnaParaRol.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getRolDTO() != null ? altaRapida.getValue().getRolDTO().getNombre() : ""));
        columnaParaDni.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getDni()));
        columnaParaFechaAlta.setCellValueFactory(altaRapida -> new SimpleObjectProperty<LocalDate>(altaRapida.getValue().getFechaAltaSistema()).asString());
        columnaParaFechaNacimiento.setCellValueFactory(altaRapida -> new SimpleObjectProperty<LocalDate>(altaRapida.getValue().getFechaNacimiento()).asString());
        columnaParaEstado.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getEstado()));

    }
}
