package com.sinergiass.asistencia;

import java.util.Date;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Asistencia {

    double[] coordenadas = new double[2];
    String fecha = new String();
    Boolean esEntrada = true;

    public Asistencia(){}

    public Asistencia(double[] coordenadas, String fecha, Boolean esEntrada){

        this.coordenadas = coordenadas;
        this.fecha = fecha;
        this.esEntrada = esEntrada;
    }

}
