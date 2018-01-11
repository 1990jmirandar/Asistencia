package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

/**
 * Created by avera on 05/12/17.
 */

public class Proyecto extends SugarRecord {
    @Expose
    private int idProyecto;
    @Expose
    private String nombre;
    @Expose
    private String estado;
    private int sync=1;
    private int actualiza=1;

    public Proyecto() {
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public int getActualiza() {
        return actualiza;
    }

    public void setActualiza(int actualiza) {
        this.actualiza = actualiza;
    }
}
