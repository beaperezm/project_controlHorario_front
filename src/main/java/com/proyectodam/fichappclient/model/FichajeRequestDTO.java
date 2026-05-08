package com.proyectodam.fichappclient.model;

import com.proyectodam.fichappclient.enums.MetodoFichaje;
import com.proyectodam.fichappclient.enums.TipoEventoFichaje;
import java.time.LocalDateTime;

public class FichajeRequestDTO {

    private int idEmpleado;
    private TipoEventoFichaje tipoEventoFichaje;
    private MetodoFichaje metodoFichaje;
    private LocalDateTime timestampDispositivo;
    private Double latitud;
    private Double longitud;
    private String dispositivoId;
    private String comentario;

    public FichajeRequestDTO() {}

    public FichajeRequestDTO(int idEmpleado, TipoEventoFichaje tipoEventoFichaje, MetodoFichaje metodoFichaje,
                              LocalDateTime timestampDispositivo, Double latitud, Double longitud,
                              String dispositivoId, String comentario) {
        this.idEmpleado = idEmpleado;
        this.tipoEventoFichaje = tipoEventoFichaje;
        this.metodoFichaje = metodoFichaje;
        this.timestampDispositivo = timestampDispositivo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.dispositivoId = dispositivoId;
        this.comentario = comentario;
    }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public TipoEventoFichaje getTipoEventoFichaje() { return tipoEventoFichaje; }
    public void setTipoEventoFichaje(TipoEventoFichaje tipoEventoFichaje) { this.tipoEventoFichaje = tipoEventoFichaje; }
    public MetodoFichaje getMetodoFichaje() { return metodoFichaje; }
    public void setMetodoFichaje(MetodoFichaje metodoFichaje) { this.metodoFichaje = metodoFichaje; }
    public LocalDateTime getTimestampDispositivo() { return timestampDispositivo; }
    public void setTimestampDispositivo(LocalDateTime timestampDispositivo) { this.timestampDispositivo = timestampDispositivo; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public String getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(String dispositivoId) { this.dispositivoId = dispositivoId; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
