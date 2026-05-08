package com.proyectodam.fichappclient.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Wrapper observable de SolicitudVacacionesDTO para su uso directo en TableView con binding de propiedades JavaFX.
 */
public class SolicitudVacacionesViewModel {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> inicio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fin = new SimpleObjectProperty<>();
    private final IntegerProperty dias = new SimpleIntegerProperty();
    private final StringProperty estado = new SimpleStringProperty();

    public SolicitudVacacionesViewModel(SolicitudVacacionesDTO dto) {
        this.id.set(dto.getId() != null ? dto.getId() : 0);
        this.tipo.set(dto.getTipo());
        this.inicio.set(dto.getInicio());
        this.fin.set(dto.getFin());
        this.dias.set(dto.getDias() != null ? dto.getDias() : 0);
        this.estado.set(dto.getEstado());
    }

    public SolicitudVacacionesViewModel(int id, String tipo, LocalDate inicio, LocalDate fin,
                                        int dias, String estado) {
        this.id.set(id);
        this.tipo.set(tipo);
        this.inicio.set(inicio);
        this.fin.set(fin);
        this.dias.set(dias);
        this.estado.set(estado);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getTipo() { return tipo.get(); }
    public StringProperty tipoProperty() { return tipo; }

    public LocalDate getInicio() { return inicio.get(); }
    public ObjectProperty<LocalDate> inicioProperty() { return inicio; }

    public LocalDate getFin() { return fin.get(); }
    public ObjectProperty<LocalDate> finProperty() { return fin; }

    public int getDias() { return dias.get(); }
    public IntegerProperty diasProperty() { return dias; }

    public String getEstado() { return estado.get(); }
    public StringProperty estadoProperty() { return estado; }
}
