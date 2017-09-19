package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.sinergiass.asistencia.model.Asistencia;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Operador extends SugarRecord implements Serializable{

    @Expose
    private int idOperador;
    @Expose
    private String nombre;
    @Expose
    private String apellido;
    @Expose
    private String cedula;
    @Expose
    private String telefono;
    @Expose
    private String faceData;

    public Operador() {
    }

    public Operador(int idOperador, String nombre, String apellido, String cedula, String telefono, String faceData){
        this.idOperador = idOperador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.faceData = faceData;


    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDatosCara() {
        return faceData;
    }

    public void setDatosCara(String datosCara) {
        this.faceData = datosCara;
    }

    public int getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(int idOperador) {
        this.idOperador = idOperador;
    }
}
