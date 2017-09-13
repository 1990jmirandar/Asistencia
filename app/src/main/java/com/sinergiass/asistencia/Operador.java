package com.sinergiass.asistencia;

import java.util.ArrayList;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Operador {

    String cedula = new String();
    String nombre = new String();
    String apellido = new String();
    String telefono = new String();
    double[] datosCara = new double[5];
    ArrayList<Asistencia> asistencia = new ArrayList<Asistencia>();


    public Operador(){

    }

    public Operador(String cedula, String nombre, String apellido, String telefono, double[] datosCara){
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.datosCara = datosCara;

    }

    public void RegistrarAsistencia(double[] coordenadas, String fecha, Boolean esEntrada){
        Asistencia asistencia = new Asistencia(coordenadas, fecha, esEntrada);
        this.asistencia.add(asistencia);

    }


}
