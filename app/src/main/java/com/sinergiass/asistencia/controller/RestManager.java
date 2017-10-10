package com.sinergiass.asistencia.controller;

import com.sinergiass.asistencia.model.callback.OperadorService;
import com.sinergiass.asistencia.model.callback.ReporteService;
import com.sinergiass.asistencia.model.helper.Constants;
import com.sinergiass.asistencia.util.HttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Julio Alfredo on 19/9/2017.
 */

public class RestManager {

    private OperadorService mOperadorService;
    private ReporteService mReporteService;

    public OperadorService getOperadorService(){

        if(mOperadorService ==null){

             Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mOperadorService = retrofit.create(OperadorService.class);
        }

        return mOperadorService;

    }

    public ReporteService getReporteService(){


        if(mReporteService ==null){

            OkHttpClient client = (new OkHttpClient()).newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            mReporteService = retrofit.create(ReporteService.class);
        }

        return mReporteService;

    }

}
