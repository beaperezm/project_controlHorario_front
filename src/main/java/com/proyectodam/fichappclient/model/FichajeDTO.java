package com.proyectodam.fichappclient.model;

import com.proyectodam.fichappclient.enums.MetodoFichaje;
import com.proyectodam.fichappclient.enums.TipoEventoFichaje;
import java.time.LocalDateTime;

public class FichajeDTO {

    private int idFichaje;
    private TipoEventoFichaje tipoEventoFichaje;
    private MetodoFichaje metodoFichaje;
    private LocalDateTime timestampServidor;

    public FichajeDTO() {}

    public FichajeDTO(int idFichaje, TipoEventoFichaje tipoEventoFichaje, MetodoFichaje metodoFichaje, LocalDateTime timestampServidor) {
        this.idFichaje = idFichaje;
        this.tipoEventoFichaje = tipoEventoFichaje;
        this.metodoFichaje = metodoFichaje;
        this.timestampServidor = timestampServidor;
    }

    public int getIdFichaje() { return idFichaje; }
    public void setIdFichaje(int idFichaje) { this.idFichaje = idFichaje; }
    public TipoEventoFichaje getTipoEventoFichaje() { return tipoEventoFichaje; }
    public void setTipoEventoFichaje(TipoEventoFichaje tipoEventoFichaje) { this.tipoEventoFichaje = tipoEventoFichaje; }
    public MetodoFichaje getMetodoFichaje() { return metodoFichaje; }
    public void setMetodoFichaje(MetodoFichaje metodoFichaje) { this.metodoFichaje = metodoFichaje; }
    public LocalDateTime getTimestampServidor() { return timestampServidor; }
    public void setTimestampServidor(LocalDateTime timestampServidor) { this.timestampServidor = timestampServidor; }
}
