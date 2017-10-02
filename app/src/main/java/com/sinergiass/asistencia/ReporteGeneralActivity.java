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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Asistencia;
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


    TextView txtCorreos,guardar;
    EditText txtFechaInicio,txtFechaFin;
    Button btnEnviarReporte;
    RestManager mRestManager;
    ProgressBar progressBar;
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
        guardar = (TextView) findViewById(R.id.guard);
        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
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

                if (validaciones()){
                    guardar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    btnEnviarReporte.setEnabled(false);
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
    Call<Void> listCall = mRestManager.getOperadorService().enviarReporte(reporte);
        listCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    guardar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ReporteGeneralActivity.this, "Correo(s) enviado(s) con exito", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                guardar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReporteGeneralActivity.this, "Correo(s) no enviado(s) Conexion Fallida ", Toast.LENGTH_LONG).show();
                onBackPressed();
            }


        });
    }

    private boolean validaciones(){

        if (txtFechaInicio.getText().toString().isEmpty()){
            Toast.makeText(ReporteGeneralActivity.this, "Ingrese la Fecha de inicio", Toast.LENGTH_LONG).show();
            return false;
        } else if (txtFechaFin.getText().toString().isEmpty()){
            Toast.makeText(ReporteGeneralActivity.this, "Ingrese la Fecha de Fin", Toast.LENGTH_LONG).show();
            return false;
        } else if (!validarFechas()){
            Toast.makeText(ReporteGeneralActivity.this, "La fecha de inicio debe ser antes o igual de la de fin", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (txtCorreos.getText().toString().isEmpty() ){
            Toast.makeText(ReporteGeneralActivity.this, "Ingrese correos destinatarios", Toast.LENGTH_LONG).show();
            return false;
        }  else {
            return true;
        }
    }

    private  boolean validarFechas(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inicio = new Date();
        Date fin = new Date();
        try {
            inicio = simpleDateFormat.parse(txtFechaInicio.getText().toString());
            fin = simpleDateFormat.parse(txtFechaFin.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (!inicio.after(fin));
    }
}
