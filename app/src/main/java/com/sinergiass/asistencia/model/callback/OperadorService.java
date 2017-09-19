package com.sinergiass.asistencia.model.callback;

import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Julio Alfredo on 19/9/2017.
 */

public interface OperadorService {

    @GET("operadores.json")
    Call<List<Operador>> getListaOperadores();

    @GET("admins.json")
    Call<List<Admin>> getListaAdmins();

    @GET("asistencias.json")
    Call<List<Asistencia>> getListaAsistencias();
}
