package com.proyectodam.fichappclient.enums;

public enum EstadoFirma {
    PENDIENTE("Pendiente de firma"),
    NOTIFICADA("Notificada"),
    FIRMADO("Firmado"),
    RECHAZADO("Rechazado");

    private final String displayName;

    EstadoFirma(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
