package com.proyectodam.fichappclient.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class SolicitudVacacionesDTO {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> inicio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fin = new SimpleObjectProperty<>();
    private final IntegerProperty dias = new SimpleIntegerProperty();
    private final StringProperty estado = new SimpleStringProperty();

    public SolicitudVacacionesDTO(int id, String tipo, LocalDate inicio, LocalDate fin, int dias, String estado) {
        this.id.set(id);
        this.tipo.set(tipo);
        this.inicio.set(inicio);
        this.fin.set(fin);
        this.dias.set(dias);
        this.estado.set(estado);
    }

    // Getters (para TableView / PropertyValueFactory)
    public int getId() {
        return id.get();
    }

    public String getTipo() {
        return tipo.get();
    }

    public LocalDate getInicio() {
        return inicio.get();
    }

    public LocalDate getFin() {
        return fin.get();
    }

    public int getDias() {
        return dias.get();
    }

    public String getEstado() {
        return estado.get();
    }

    // Propiedades 
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public ObjectProperty<LocalDate> inicioProperty() {
        return inicio;
    }

    public ObjectProperty<LocalDate> finProperty() {
        return fin;
    }

    public IntegerProperty diasProperty() {
        return dias;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
