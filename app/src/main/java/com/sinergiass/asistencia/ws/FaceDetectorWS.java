package com.sinergiass.asistencia.ws;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinergiass.asistencia.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 13/09/17.
 */

public class FaceDetectorWS {


    public final String URL_SEND_FACE="operadores.json";

    public void enviaImagen(String imagen, String idOperador) {
        RequestParams rp = new RequestParams();


        HttpUtils.get(URL_SEND_FACE, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    JSONArray serverResp = new JSONArray(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    throw new Exception(throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                try {
                    throw new Exception(throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    }


