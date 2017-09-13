package com.sinergiass.asistencia.model;

import com.orm.SugarRecord;


public class Admin extends SugarRecord {

    private int idAdmin;
    private String usuario ;
    private String password ;

    public Admin(){}

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

