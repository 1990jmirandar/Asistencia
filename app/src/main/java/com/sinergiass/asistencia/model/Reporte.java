package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Julio Alfredo on 27/9/2017.
 */

public class Reporte implements Serializable{
    @Expose
    private String fechaInicio;
    @Expose
    private String fechaFin;
    @Expose
    private String[] destinatarios;

    public Reporte() {
    }

    public Reporte(String fechaInicio, String fechaFin, String[] destinatarios) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.destinatarios = destinatarios;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String[] getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String[] destinatarios) {
        this.destinatarios = destinatarios;
    }
}
