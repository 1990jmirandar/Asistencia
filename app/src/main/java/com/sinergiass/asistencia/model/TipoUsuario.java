package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by avera on 30/11/17.
 */

public class TipoUsuario extends SugarRecord implements Serializable {
    @Expose
    private int idTipoUsuario;
    @Expose
    private String nombre;
    @Expose
    private String estado;

    private int sync=1;
    private int actualiza=1;

    public TipoUsuario(int idTipoUsuario, String nombre, String estado) {
        this.idTipoUsuario = idTipoUsuario;
        this.nombre = nombre;
        this.estado = estado;
    }

    public TipoUsuario() {
    }

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
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


    public int getUpdate() {
        return actualiza;
    }

    public void setUpdate(int update) {
        this.actualiza = update;
    }
}
