package com.sinergiass.asistencia.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Asistencia extends SugarRecord {

    private String latitud;
    private String longitud;
    private String fecha;
    private String hora;
    private boolean isEntrada;

    //Relation
    private Operador operador;

    public Asistencia() {

    }

    public Asistencia(String latitud, String longitud, String fecha, String hora, boolean isEntrada, Operador operador) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.hora = hora;
        this.isEntrada = isEntrada;
        this.operador = operador;
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

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }
}
