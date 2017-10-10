package com.sinergiass.asistencia.model;

import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.sinergiass.asistencia.model.Asistencia;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String foto1;
    @Expose
    private String foto2;
    @Expose
    private String foto3;
    @Expose
    private String foto4;
    @Expose
    private String foto5;

    private int estado = 1;

    // Fotos recortadas centradas en la cara, de tipo OpenCV Mat


    public Operador() {}

    public Operador(int idOperador, String nombre, String apellido, String cedula, String telefono,
                    String foto1, String foto2, String foto3, String foto4, String foto5){
        this.idOperador = idOperador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
        this.foto4 = foto4;
        this.foto5 = foto5;
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

    public int getIdOperador() {
        return idOperador;
    }
    public void setIdOperador(int idOperador) {
        this.idOperador = idOperador;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getFoto4() {
        return foto4;
    }

    public void setFoto4(String foto4) {
        this.foto4 = foto4;
    }

    public String getFoto5() {
        return foto5;
    }

    public void setFoto5(String foto5) {
        this.foto5 = foto5;
    }

    /**
     * Decodifica el String en Base64 que representa la foto del operador
     * @return Una Matriz (Mat) de OpenCV, que contiene los datos de la cara del operador
     */
//    public Mat faceMat() {
//
//        // Decodificar el String en Base64 para obtener el Array de Bytes que representa la foto de la cara
//        byte[] faceData = Base64.decode(this.encodedFaceData, Base64.DEFAULT);
//
//        // Instanciar una nueva Mat (matriz) del tamanio y tipo correcto
//        // CvType.CV_8UC1 : 8 bits - Unsigned - One Channel
//        Mat faceMat = new Mat(faceData.length, 1, CvType.CV_8UC1);
//
//        // Llenar la Mat con los datos del Byte Array
//        faceMat.put(0, 0, faceData);
//
//        return faceMat;
//    }

    public void addFotos(List<String> encodings ){
        foto1 = encodings.get(0);
        foto2 = encodings.get(1);
        foto3 = encodings.get(2);
        foto4 = encodings.get(3);
        foto5 = encodings.get(4);
    }

    public List<Mat> fotos (){

        List<Mat> l = new ArrayList<>();

        // Decodificar el String en Base64 para obtener el Array de Bytes que representa la foto de la cara
        byte[] fotoBytes1 = Base64.decode(this.foto1, Base64.DEFAULT);
        byte[] fotoBytes2 = Base64.decode(this.foto2, Base64.DEFAULT);
        byte[] fotoBytes3 = Base64.decode(this.foto3, Base64.DEFAULT);
        byte[] fotoBytes4 = Base64.decode(this.foto4, Base64.DEFAULT);
        byte[] fotoBytes5 = Base64.decode(this.foto5, Base64.DEFAULT);

        // Instanciar una nueva Mat (matriz) del tamanio y tipo correcto
        Mat mat1 = new Mat(160, 160, CvType.CV_8UC4);
        Mat mat2 = new Mat(160, 160, CvType.CV_8UC4);
        Mat mat3 = new Mat(160, 160, CvType.CV_8UC4);
        Mat mat4 = new Mat(160, 160, CvType.CV_8UC4);
        Mat mat5 = new Mat(160, 160, CvType.CV_8UC4);

        // Llenar la Mat con los datos del Byte Array
        mat1.put(0, 0, fotoBytes1);
        mat2.put(0, 0, fotoBytes2);
        mat3.put(0, 0, fotoBytes3);
        mat4.put(0, 0, fotoBytes4);
        mat5.put(0, 0, fotoBytes5);

        l.add(mat1);
        l.add(mat2);
        l.add(mat3);
        l.add(mat4);
        l.add(mat5);

        return l;

    }
}
