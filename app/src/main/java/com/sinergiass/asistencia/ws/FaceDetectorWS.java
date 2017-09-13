package com.sinergiass.asistencia.ws;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinergiass.asistencia.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 13/09/17.
 */

public class FaceDetectorWS {

    public final String URL_SEND_FACE="https://api.kairos.com/enroll";

    public void enviaImagen(String imagen, String idOperador){
        RequestParams rp = new RequestParams();
        rp.add("image",imagen); rp.add("subject_id", idOperador); rp.add("gallery_name", "operadores");

        HttpUtils.post(URL_SEND_FACE, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
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
