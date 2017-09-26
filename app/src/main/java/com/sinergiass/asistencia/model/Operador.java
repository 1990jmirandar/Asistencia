package com.sinergiass.asistencia.model;

import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.sinergiass.asistencia.model.Asistencia;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Julio Alfredo on 11/9/2017.
 */

public class Operador extends SugarRecord implements Serializable{

    // idOperador: El atributo id se hereda de SugarRecord, no es necesario redefinir un idOperador aqui.
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
    private String encodedFaceData;

    private int estado = 1;

    public Operador() {
    }

    public Operador(int idOperador, String nombre, String apellido, String cedula, String telefono, String encodedFaceData){
        this.idOperador = idOperador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.encodedFaceData = encodedFaceData;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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

    public String getEncodedFaceData () {return this.encodedFaceData;}

    public void setEncodedFaceData(String encodedFaceData) {
        this.encodedFaceData = encodedFaceData;
    }

    public int getIdOperador() {
        return idOperador;
    }
    public void setIdOperador(int idOperador) {
        this.idOperador = idOperador;
    }


    /**
     * Decodifica el String en Base64 que representa la foto del operador
     * @return Una Matriz (Mat) de OpenCV, que contiene los datos de la cara del operador
     */
    public Mat getFaceMat() {

        // Decodificar el String en Base64 para obtener el Array de Bytes que representa la foto de la cara
        byte[] faceData = Base64.decode(this.encodedFaceData, Base64.DEFAULT);

        // Instanciar una nueva Mat (matriz) del tamanio y tipo correcto
        // CvType.CV_8UC1 : 8 bits - Unsigned - One Channel
        Mat faceMat = new Mat(faceData.length, 1, CvType.CV_8UC1);

        // Llenar la Mat con los datos del Byte Array
        faceMat.put(0, 0, faceData);

        return faceMat;
    }
}
