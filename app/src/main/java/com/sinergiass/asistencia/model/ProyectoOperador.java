package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

/**
 * Created by avera on 05/12/17.
 */

public class ProyectoOperador extends SugarRecord {
    @Expose
    private int idProyectoOperador;
    @Expose
    private int idProyecto;
    @Expose
    private int idOperador;
    @Expose
    private String estado;

    private int sync=1;
    private int borrar=1;

    public ProyectoOperador() {
    }

    public int getIdProyectoOperador() {
        return idProyectoOperador;
    }

    public void setIdProyectoOperador(int idProyectoOperador) {
        this.idProyectoOperador = idProyectoOperador;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public int getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(int idOperador) {
        this.idOperador = idOperador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getBorrar() {
        return borrar;
    }

    public void setBorrar(int borrar) {
        this.borrar = borrar;
    }
}
