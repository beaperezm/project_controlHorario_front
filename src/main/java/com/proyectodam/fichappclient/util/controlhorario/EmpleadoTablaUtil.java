package com.proyectodam.fichappclient.util.controlhorario;

import com.proyectodam.fichappclient.model.EmpleadoDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

/** Configura las columnas y los datos de un TableView de empleados. */
public class EmpleadoTablaUtil {


    public static void tablaEmpleado(TableView<EmpleadoDTO> tablaEmpleados, ObservableList<EmpleadoDTO> listaEmpleados, TableColumn<EmpleadoDTO, String> columnaParaNombre, TableColumn<EmpleadoDTO, String> columnaParaApellido, TableColumn<EmpleadoDTO, String> columnaParaCorreo, TableColumn<EmpleadoDTO, String> columnaParaDireccion, TableColumn<EmpleadoDTO, String> columnaParaTelefono, TableColumn<EmpleadoDTO, String> columnaParaDepartamento, TableColumn<EmpleadoDTO, String> columnaParaRol, TableColumn<EmpleadoDTO, String> columnaParaDni, TableColumn<EmpleadoDTO, String> columnaParaFechaAlta, TableColumn<EmpleadoDTO, String> columnaParaFechaNacimiento, TableColumn<EmpleadoDTO, String> columnaParaEstado) {

        tablaEmpleados.setItems(listaEmpleados);

        columnaParaNombre.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getNombre())));
        columnaParaApellido.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getApellidos())));
        columnaParaCorreo.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getEmail())));
        columnaParaDireccion.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getDireccion())));
        columnaParaTelefono.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getTelefono())));
        columnaParaDepartamento.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getDepartamento() != null ? altaRapida.getValue().getDepartamento() : "--"));
        columnaParaRol.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getRol() != null ? altaRapida.getValue().getRol() : "--"));
      //  columnaParaDepartamento.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getDepartamentoDTO() != null ? altaRapida.getValue().getDepartamentoDTO().getNombre() : "--"));
     //   columnaParaRol.setCellValueFactory(altaRapida -> new SimpleStringProperty(altaRapida.getValue().getRolDTO() != null ? altaRapida.getValue().getRolDTO().getNombre() : "--"));
        columnaParaDni.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getDni())));
        // fechaAlta puede ser nula si el empleado fue creado sólo con fechaAltaSistema
        columnaParaFechaAlta.setCellValueFactory(altaRapida -> {
            LocalDate fecha = altaRapida.getValue().getFechaAlta();
            if (fecha == null) {
                fecha = altaRapida.getValue().getFechaAltaSistema();
            }
            return new SimpleStringProperty(seteoValorNull(fecha != null ? String.valueOf(fecha) : null)); 
        });
        columnaParaFechaNacimiento.setCellValueFactory(altaRapida -> { LocalDate fechaNacimiento = altaRapida.getValue().getFechaNacimiento(); return new SimpleStringProperty(seteoValorNull(String.valueOf(fechaNacimiento))); });
        columnaParaEstado.setCellValueFactory(altaRapida -> new SimpleStringProperty(seteoValorNull(altaRapida.getValue().getEstado())));
    }

    private static String seteoValorNull(String valor) {
        return (valor == null || valor.isBlank() || valor.equalsIgnoreCase("null")) ? "--" : valor;
    }
}
