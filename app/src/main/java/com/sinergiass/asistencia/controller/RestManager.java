package com.sinergiass.asistencia.controller;

import com.sinergiass.asistencia.model.callback.OperadorService;
import com.sinergiass.asistencia.model.helper.Constants;
import com.sinergiass.asistencia.util.HttpUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Julio Alfredo on 19/9/2017.
 */

public class RestManager {

    private OperadorService mOperadorService;

    public OperadorService getOperadorService(){

        if(mOperadorService ==null){

            

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mOperadorService = retrofit.create(OperadorService.class);
        }

        return mOperadorService;

    }

    public OperadorService postOperadorService(){

        if(mOperadorService ==null){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            mOperadorService = retrofit.create(OperadorService.class);
        }

        return mOperadorService;

    }

}
