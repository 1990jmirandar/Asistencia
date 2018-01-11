package com.sinergiass.asistencia.model.callback;

import com.sinergiass.asistencia.model.Reporte;
import com.sinergiass.asistencia.model.ReporteOperador;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by avera on 10/10/17.
 */

public interface ReporteService {

    @POST("enviar_reporte/")
    Call<Void> enviarReporte(@Body Reporte reporte);

    @POST("enviar_reporte_operador/")
    Call<Void> enviarReporteOperador(@Body ReporteOperador reporte);

}