package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

import cz.msebera.android.httpclient.entity.SerializableEntity;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Asistencia extends SugarRecord implements Serializable {

    @Expose
    private long operador;
    @Expose
    private String latitud;
    @Expose
    private String longitud;
    @Expose
    private String fecha;
    @Expose
    private String hora;
    @Expose
    private boolean isEntrada;
    private int estado = 1;

    public Asistencia() {

    }

    public Asistencia(long idOperador, String latitud, String longitud, String fecha, String hora, boolean isEntrada) {
        this.operador = idOperador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.hora = hora;
        this.isEntrada = isEntrada;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public long getIdOperador() {
        return operador;
    }

    public void setIdOperador(long idOperador) {
        this.operador = idOperador;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isEntrada() {
        return isEntrada;
    }

    public void setEntrada(boolean entrada) {
        isEntrada = entrada;
    }
}
