package com.sinergiass.asistencia.model.callback;

import com.google.gson.JsonElement;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.Proyecto;
import com.sinergiass.asistencia.model.ProyectoOperador;
import com.sinergiass.asistencia.model.Reporte;
import com.sinergiass.asistencia.model.TipoUsuario;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Julio Alfredo on 19/9/2017.
 */

public interface OperadorService {

    @GET("tipousuario.json")
    Call<List<TipoUsuario>> getListaTipoUsuario();

    @GET("operadores.json")
    Call<List<Operador>> getListaOperadores();

    @GET("admins.json")
    Call<List<Admin>> getListaAdmins();

    @GET("proyecto.json")
    Call<List<Proyecto>> getListProyectos();

    @GET("asistencias.json")
    Call<List<Asistencia>> getListaAsistencias();

    @GET("proyectooperador.json")
    Call<List<ProyectoOperador>> getListaProyectoOperador();

    @POST("operadores/")
    Call<List<Operador>> guardarOp(@Body List<Operador> operador);

    @POST("asistencias/")
    Call<List<Asistencia>> guardarAsis(@Body List<Asistencia> asistencias);

    @POST("tipousuario/")
    Call<List<TipoUsuario>> guardarTipoUsuario(@Body List<TipoUsuario> tipoUsuarios);

    @POST("proyectooperador/")
    Call<List<ProyectoOperador>> guardarProyectoOperador(@Body List<ProyectoOperador> proyectoOperador);


    @PUT("tipousuario/{id}/")
    Call<TipoUsuario> actualizaTipoUsuario(@Path("id") int tipoUsuarioId , @Body TipoUsuario tipoUsuario);

    @PUT("operadores/{id}/")
    Call<Operador> actualizarOp(@Path("id") int tipoUsuarioId , @Body Operador operador);

    @PUT("proyectooperador/{id}/")
    Call<ProyectoOperador> actualizarProyectoOperador(@Path("id") int proyectoOperadorId , @Body ProyectoOperador proyectoOperador);

    @DELETE("proyectooperador/{id}/")
    Call<Void> borrarProyectoOperador(@Path("id") int proyectoOperadorId);


    @POST("proyecto/")
    Call<List<Proyecto>> guardarProyecto(@Body List<Proyecto> tipoUsuarios);

    @PUT("proyecto/{id}/")
    Call<Proyecto> actualizaProyecto(@Path("id") int proyectoId , @Body Proyecto proyecto);




}

