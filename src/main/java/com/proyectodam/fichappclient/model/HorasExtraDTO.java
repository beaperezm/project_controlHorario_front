package com.proyectodam.fichappclient.model;

public class HorasExtraDTO {

    private double horasTrabajadas;
    private double horasContrato;
    private double horasExtra;
    private double saldoHoras;

    public HorasExtraDTO() {}

    public HorasExtraDTO(double horasTrabajadas, double horasContrato, double horasExtra, double saldoHoras) {
        this.horasTrabajadas = horasTrabajadas;
        this.horasContrato = horasContrato;
        this.horasExtra = horasExtra;
        this.saldoHoras = saldoHoras;
    }

    public double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public double getHorasContrato() { return horasContrato; }
    public void setHorasContrato(double horasContrato) { this.horasContrato = horasContrato; }
    public double getHorasExtra() { return horasExtra; }
    public void setHorasExtra(double horasExtra) { this.horasExtra = horasExtra; }
    public double getSaldoHoras() { return saldoHoras; }
    public void setSaldoHoras(double saldoHoras) { this.saldoHoras = saldoHoras; }
}
