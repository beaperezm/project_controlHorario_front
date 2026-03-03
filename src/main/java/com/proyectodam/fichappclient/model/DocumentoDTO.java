package com.proyectodam.fichappclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proyectodam.fichappclient.enums.EstadoFirma;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentoDTO {

    private Long id;
    private String nombreArchivo;
    private String rutaAcceso;
    private String tipoMime;
    private long tamanoBytes;
    private String categoria;
    private EstadoFirma estadoFirma;
    private LocalDateTime fechaSubida;
    private LocalDateTime fechaFirma;
    private String urlDescarga;
    private Integer idEmpleado;
    private String nombreEmpleado;
    private String departamento;

    // UI Helpers (Transient properties not from API)
    private transient boolean selected;

    public DocumentoDTO() {
        this.selected = false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaAcceso() {
        return rutaAcceso;
    }

    public void setRutaAcceso(String rutaAcceso) {
        this.rutaAcceso = rutaAcceso;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public EstadoFirma getEstadoFirma() {
        return estadoFirma;
    }

    public void setEstadoFirma(EstadoFirma estadoFirma) {
        this.estadoFirma = estadoFirma;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public String getUrlDescarga() {
        return urlDescarga;
    }

    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
