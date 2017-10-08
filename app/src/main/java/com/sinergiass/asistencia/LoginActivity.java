package com.sinergiass.asistencia;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.facerecog.RecognitionActivity;
import com.sinergiass.asistencia.facerecog.Training;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.util.DatabaseHelper;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button operador;
    private Button admin;
    private RestManager mManager;
    private ProgressBar progressBar;
    private LinearLayout layout,layoutP;

    private static final boolean IMPORT_ASSETS_DB = false; // true para cargar la DB desde assets, false para cargar desde el Servidor
    private List<Operador> mOperadores;

    private Training.TrainTask trainTask;


    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        operador = (Button)findViewById(R.id.operador);
        admin = (Button)findViewById(R.id.admin);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        layout = (LinearLayout) findViewById(R.id.layout_login) ;
        layoutP = (LinearLayout) findViewById(R.id.layout_progress);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mManager = new RestManager();

        mOperadores = new ArrayList<>();

        operador.setEnabled(false);

        new DownloadDataTask().execute();

        trainTask = new Training.TrainTask(getApplicationContext(), trainTaskCallback);



        admin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });

        FileHelper fh = new FileHelper();

        if(!((new File(fh.DATA_PATH)).exists())) ;
        operador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RecognitionActivity.class));
            }
        });
    }

    class DownloadDataTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            if (IMPORT_ASSETS_DB) {
                // CARGAR LA DB ubicada en assets/databases
                DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
                dbHelper.getWritableDatabase();
            }
            else {
                cargarAdmins();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            layoutP.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);

            trainTask.execute();
        }
    }

    private void cargarAdmins(){
        Call<List<Admin>> listCall = mManager.getOperadorService().getListaAdmins();
        listCall.enqueue(new Callback<List<Admin>>() {
            @Override
            public void onResponse(Call<List<Admin>> call, Response<List<Admin>> response) {

                if(response.isSuccessful()){
                    Admin.deleteAll(Admin.class);
                    List<Admin> listaAdmin = response.body();

                    //Log.d("El numero de la lista", ""+ listaAdmin.size());

                    for (Admin admin : listaAdmin){admin.save();}

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

                cargarOperadores();
            }

            @Override
            public void onFailure(Call<List<Admin>> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Conexion Fallida al cargar admins", Toast.LENGTH_LONG).show();
                cargarOperadores();
            }
        });
    }

    private void cargarOperadores(){
        Call<List<Operador>> listCall = mManager.getOperadorService().getListaOperadores();
        listCall.enqueue(new Callback<List<Operador>>() {
            @Override
            public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                if(response.isSuccessful()){
                    Operador.deleteAll(Operador.class,"estado = ?","1");
                    mOperadores = response.body();
                    Log.d("Size Lista Operadores", ""+ mOperadores.size());
//

                    for (Operador operador : mOperadores){
                        operador.save();
                    }

//                    Toast.makeText(LoginActivity.this, "Cargado Operadores Exitosa", Toast.LENGTH_LONG).show();

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

                cargarAsistencias();

            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Conexion Fallida al cargar operadores", Toast.LENGTH_LONG).show();

                cargarAsistencias();

            }
        });
    }

    private void cargarAsistencias(){
        Call<List<Asistencia>> listCall = mManager.getOperadorService().getListaAsistencias();
        listCall.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {

                if(response.isSuccessful()){
                    Asistencia.deleteAll(Asistencia.class,"estado = ?","1");
                    List<Asistencia> listaAsistencias = response.body();
                    Log.d("Size Lista Asist ", ""+ listaAsistencias.size());
//                    for(int i=0; i<listaAsistencias.size();i++){
//                        final Asistencia asistencia = new Asistencia(listaAsistencias.get(i).getIdOperador(),
//                                listaAsistencias.get(i).getLatitud(),listaAsistencias.get(i).getLongitud(),
//                                listaAsistencias.get(i).getFecha(),listaAsistencias.get(i).getHora(),
//                                listaAsistencias.get(i).isEntrada());
//                        //Log.d("asistencia del operador:"+i + ":",""+asistencia.getIdOperador());
//                        asistencia.save();
//                    }
                    for (Asistencia asistencia : listaAsistencias){
                        for (Operador op : mOperadores){
                            if (asistencia.getIdOperador() == op.getIdOperador()) asistencia.cedulaOperador = op.getCedula();
                        }
                        asistencia.save();
                    }
//                    Toast.makeText(LoginActivity.this, "Cargado Asistencias Exitosa", Toast.LENGTH_LONG).show();

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

            }

            @Override
            public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "No se pudo sincronizar con el Servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(LoginActivity.this, AsistenciaActivity.class);
        startActivity(intent);
    }

    private Training.TrainTask.Callback trainTaskCallback = new Training.TrainTask.Callback() {
        @Override
        public void onTrainTaskComplete(boolean result) {
            if (result) operador.setEnabled(true);
        }
    };
}
