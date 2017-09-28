package com.sinergiass.asistencia.model.callback;

import com.google.gson.JsonElement;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.Reporte;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

    @POST("operadores/")
    Call<Operador> guardarOp(@Body Operador operador);

    @POST("asistencias/")
    Call<Asistencia> guardarAsis(@Body Asistencia asistencia);

    @POST("enviar_reporte/")
    Call<Reporte> enviarReporte(@Body Reporte reporte);



}
