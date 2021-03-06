package com.sinergiass.asistencia.model;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.io.Serializable;


public class Admin extends SugarRecord implements Serializable {

    @Expose
    private String username ;
    @Expose
    private String password ;


    public Admin(){}

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }



    public String getUsuario() {
        return username;
    }

    public void setUsuario(String usuario) {
        this.username = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

