package com.sinergiass.asistencia;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.orm.SugarRecord;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AsistenciaActivity extends  AppCompatActivity {

    private Button ubicarme;
    private Operador operador;
    private TextView nombre,apellido,cedula;
    private RadioButton rbtEntrada,rbtSalida;
    private FusedLocationProviderClient mFusedLocationClient;
    private RestManager mRestManager;
    private Asistencia asistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Registro de Asistencia");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRestManager = new RestManager();

        Bundle extras = getIntent().getExtras();
        operador = Operador.find(Operador.class, "id_Operador = ?", "" + extras.getInt("idOperador")).get(0);

        ubicarme = (Button)findViewById(R.id.ubicacion);
        nombre = (TextView)findViewById(R.id.nombres);
        apellido = (TextView)findViewById(R.id.apellidos);
        cedula = (TextView)findViewById(R.id.cedula);
        rbtEntrada = (RadioButton) findViewById(R.id.rbtEntrada);
        rbtSalida= (RadioButton) findViewById(R.id.rbtSalida);
        nombre.setText(operador.getNombre());
        apellido.setText(operador.getApellido());
        cedula.setText(operador.getCedula());


        ubicarme.setOnClickListener(new View.OnClickListener(){

            public void onClick(View arg0){
                mFusedLocationClient.getLastLocation().addOnSuccessListener(AsistenciaActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if (location != null) {

                            Intent intent = new Intent(AsistenciaActivity.this,MapsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putDouble("longitud",location.getLongitude());
                            extras.putDouble("latitud",location.getLatitude());
                            intent.putExtras(extras);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AsistenciaActivity.this, "GPS DESACTIVADO: No se puede obtener la informacion actual de la localizacion geografica, active el gps y vuelva a intentar", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

            }

        });

        validaCampos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_asistencia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_save) {
            List<Asistencia> asistencia = Asistencia.find(Asistencia.class , "id_Operador = ? and fecha = ?", new String[]{""+operador.getIdOperador(),new SimpleDateFormat("yyyy-MM-dd").format(new Date())});
            if (asistencia.size()==2){
                Toast.makeText(this,"No se puede agregar mas asistencias el dia de hoy", Toast.LENGTH_LONG).show();
                return true;
            }
            guardar();
        }

        return super.onOptionsItemSelected(item);
    }

    public void guardar(){

        mFusedLocationClient.getLastLocation().addOnSuccessListener(AsistenciaActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    asistencia = new Asistencia();
                    asistencia.setLatitud(""+location.getLatitude());
                    asistencia.setLongitud(""+location.getLongitude());
                    asistencia.setEntrada(rbtEntrada.isChecked() ? true : false);
                    asistencia.setFecha(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    asistencia.setHora(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    asistencia.setIdOperador(operador.getIdOperador());

                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put("idOperador", ""+asistencia.getIdOperador());
                    parameters.put("latitud", ""+asistencia.getLatitud());
                    parameters.put("longitud", ""+asistencia.getLongitud());
                    parameters.put("fecha", ""+asistencia.getFecha());
                    parameters.put("hora", ""+asistencia.getHora());
                    parameters.put("isEntrada", ""+asistencia.isEntrada());

                    enviarAsis(parameters);


                    //asistencia.save();

                } else {
                    Toast.makeText(AsistenciaActivity.this, "GPS DESACTIVADO: No se puede obtener la informacion actual de la localizacion geografica, active el gps y vuelva a intentar", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }

    public void validaCampos(){

        List<Asistencia> asistencia = Asistencia.find(Asistencia.class , "id_Operador = ? and fecha = ?", new String[]{""+operador.getIdOperador(),new SimpleDateFormat("yyyy-MM-dd").format(new Date())});
        if (!asistencia.isEmpty()){
            if (asistencia.size()==2){
                rbtEntrada.setEnabled(false);
                rbtSalida.setEnabled(false);
                rbtSalida.setChecked(true);

            }else{
                if (asistencia.get(0).isEntrada()){
                    rbtSalida.setChecked(true);
                    rbtSalida.setEnabled(true);
                }

                rbtEntrada.setEnabled(false);
            }



        }else{
            rbtEntrada.setChecked(true);
            rbtSalida.setEnabled(false);
        }


    }

    private void enviarAsis( HashMap<String, String> parameters) {
        Call<Asistencia> listCall = mRestManager.getOperadorService().guardarAsis(parameters);
        listCall.enqueue(new Callback<Asistencia>() {
            @Override
            public void onResponse(Call<Asistencia> call, Response<Asistencia> response) {

                if (response.isSuccess()) {
                    asistencia.setEstado(1);
                    asistencia.save();
                    Toast.makeText(AsistenciaActivity.this, "Registro guardado y sincronizado: " + asistencia.getIdOperador(), Toast.LENGTH_LONG).show();
                    onBackPressed();

                } else {
                }
            }

            @Override
            public void onFailure(Call<Asistencia> call, Throwable t) {
                asistencia.setEstado(0);
                asistencia.save();
                Toast.makeText(AsistenciaActivity.this, "Sin conexion, registro guardado local: " + asistencia.getIdOperador(), Toast.LENGTH_LONG).show();
                onBackPressed();

            }

        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

}
