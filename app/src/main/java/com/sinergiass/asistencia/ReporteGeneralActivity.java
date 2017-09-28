package com.sinergiass.asistencia;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.Reporte;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Julio Alfredo on 27/9/2017.
 */

public class ReporteGeneralActivity extends AppCompatActivity {


    TextView txtCorreos;
    EditText txtFechaInicio,txtFechaFin;
    Button btnEnviarReporte;
    RestManager mRestManager;
    Calendar calendar;
    int dia, mes, anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_general);
        mRestManager = new RestManager();
        calendar= Calendar.getInstance();
        txtFechaInicio = (EditText) findViewById(R.id.txtFechaInicio);
        txtFechaFin = (EditText) findViewById(R.id.txtFechaFin);
        txtCorreos = (TextView) findViewById(R.id.txtEmails);
        btnEnviarReporte = (Button) findViewById(R.id.enviarReporte);
        dia=calendar.get(Calendar.DAY_OF_MONTH);
        mes=calendar.get(Calendar.MONTH);
        anio=calendar.get(Calendar.YEAR);
        txtFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialogInicio();
            }
        });

        txtFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialogFin();
            }
        });

        btnEnviarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DateDialogFin();
                String cadena = txtCorreos.getText().toString();
                String[] correos = cadena.split(";");


                String fechaInicio = txtFechaInicio.getText().toString();
                String fechaFin = txtFechaFin.getText().toString();

                Reporte reporte = new Reporte();
                reporte.setFechaInicio(fechaInicio);
                reporte.setFechaFin(fechaFin);
                reporte.setDestinatarios(correos);

//                JsonObject json = new JsonObject();
//                JsonArray correos =

//                enviarData(fechaInicio,fechaFin,correos);
                enviarData(reporte);
            }
        });


    }

    public void DateDialogInicio(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = simpleDateFormat.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtFechaInicio.setText(simpleDateFormat.format(date));

            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, anio, mes, dia);
        dpDialog.show();
    }

    public void DateDialogFin(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = simpleDateFormat.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtFechaFin.setText(simpleDateFormat.format(date));
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, anio, mes, dia);
        dpDialog.show();
    }

//    public void enviarData(String fechaInicio, String fechaFin, String[] destinatarios){
        public void enviarData(Reporte reporte){
    Call<Reporte> listCall = mRestManager.getOperadorService().enviarReporte(reporte);
        listCall.enqueue(new Callback<Reporte>() {
            @Override
            public void onResponse(Call<Reporte> call, Response<Reporte> response) {

                if (response.isSuccessful()) {
                    Log.d("SUCCESS RESPONSE: ", response.body().toString());


                } else {
                }
            }

            @Override
            public void onFailure(Call<Reporte> call, Throwable t) {


            }

        });
    }


}
