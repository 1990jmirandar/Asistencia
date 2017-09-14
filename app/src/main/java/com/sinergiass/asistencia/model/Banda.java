package com.sinergiass.asistencia.model;

/**
 * Created by Julio Alfredo on 13/9/2017.
 */

public class Banda {

    public String nombres;
    public String apellidos;
    public String cedula;

    public Banda() {
        super();
    }

    public Banda(String nombres, String apellidos, String cedula) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
    }
}
