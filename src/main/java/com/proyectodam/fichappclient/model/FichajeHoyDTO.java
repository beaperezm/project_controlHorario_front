package com.proyectodam.fichappclient.model;

import com.proyectodam.fichappclient.enums.TipoEventoFichaje;

public class FichajeHoyDTO {

    private String fecha;
    private String entrada;
    private String descanso;
    private String salida;
    private String tipoEvento;
    private String timeStampServidor;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getDescanso() {
        return descanso;
    }

    public void setDescanso(String descanso) {
        this.descanso = descanso;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTimeStampServidor() {
        return timeStampServidor;
    }

    public void setTimeStampServidor(String timeStampServidor) {
        this.timeStampServidor = timeStampServidor;
    }
}
