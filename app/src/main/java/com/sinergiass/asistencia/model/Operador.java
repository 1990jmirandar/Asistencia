package com.sinergiass.asistencia.model;

import com.sinergiass.asistencia.model.Asistencia;

import java.util.ArrayList;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Operador {

    private int idOperador;
    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private double[] datosCara;
    private ArrayList<Asistencia> asistencia;



    public Operador(String cedula, String nombre, String apellido, String telefono, double[] datosCara, int idOperador){
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.datosCara = datosCara;
        this.idOperador = idOperador;

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

    public double[] getDatosCara() {
        return datosCara;
    }

    public void setDatosCara(double[] datosCara) {
        this.datosCara = datosCara;
    }

    public ArrayList<Asistencia> getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(ArrayList<Asistencia> asistencia) {
        this.asistencia = asistencia;
    }

    public int getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(int idOperador) {
        this.idOperador = idOperador;
    }
}
