package com.sinergiass.asistencia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceActivity;
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
import com.sinergiass.asistencia.model.TipoUsuario;
import com.sinergiass.asistencia.model.helper.Constants;
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

import static com.sinergiass.asistencia.R.xml.preferences;

public class LoginActivity extends AppCompatActivity {



    private Button operador,cambiar;
    private Button admin;
    private RestManager mManager;
    private ProgressBar progressBar;
    private LinearLayout layout,layoutP;

    private static final boolean IMPORT_ASSETS_DB = false; // true para cargar la DB desde assets, false para cargar desde el Servidor
    private List<Operador> mOperadores;

    private Training.TrainTask trainTask;

    private SharedPreferences mSharedPreferences;

    private int mNumOperadores;
    private int mNumOperadoresEnPreferences;


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
        cambiar = (Button)findViewById(R.id.preference);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        layout = (LinearLayout) findViewById(R.id.layout_login) ;
        layoutP = (LinearLayout) findViewById(R.id.layout_progress);

        PreferenceManager.setDefaultValues(this, preferences, false);

        mSharedPreferences = getSharedPreferences("preferencia",Context.MODE_PRIVATE);

        mManager = new RestManager();

        mOperadores = new ArrayList<>();


        mNumOperadores = Operador.listAll(Operador.class).size();
        mNumOperadoresEnPreferences = mSharedPreferences.getInt("cant_operadores", 0);

        Log.d("Cant Op DB: ", Integer.toString(mNumOperadores));
        Log.d("Cant Op Pref: ", Integer.toString(mNumOperadoresEnPreferences));

        String ip = mSharedPreferences.getString("IP",Constants.HTTP.BASE_URL);
        Log.d("la ip nueva: ", ip);

        Constants.HTTP.BASE_URL = ip;

        operador.setEnabled(false);

        layoutP.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

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
                Intent intent = new Intent(LoginActivity.this, RecognitionActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PreferenciaActivity.class);
                startActivityForResult(intent, 0);
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
                cargarTipoUsuario();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }


    private void cargarTipoUsuario(){
        Call<List<TipoUsuario>> listCall = mManager.getOperadorService().getListaTipoUsuario();
        listCall.enqueue(new Callback<List<TipoUsuario>>() {
            @Override
            public void onResponse(Call<List<TipoUsuario>> call, Response<List<TipoUsuario>> response) {

                if(response.isSuccessful()){
                    TipoUsuario.deleteAll(TipoUsuario.class);
                    List<TipoUsuario> listaAdmin = response.body();

                    //Log.d("El numero de la lista", ""+ listaAdmin.size());

                    for (TipoUsuario tipoUsuario : listaAdmin){tipoUsuario.save();}

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

                cargarAdmins();
            }

            @Override
            public void onFailure(Call<List<TipoUsuario>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Conexion Fallida al cargar Tipos de usuario: " + t.getMessage(), Toast.LENGTH_LONG).show();
                layoutP.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

            }
        });
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
                Toast.makeText(LoginActivity.this, "Conexion Fallida al cargar admins: " + t.getMessage(), Toast.LENGTH_LONG).show();
                layoutP.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

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
                Toast.makeText(LoginActivity.this, "Conexion Fallida al cargar operadores: " + t.getMessage(), Toast.LENGTH_LONG).show();

                layoutP.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cargarAsistencias(){
        Call<List<Asistencia>> listCall = mManager.getOperadorService().getListaAsistencias();
        listCall.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {

                layoutP.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);



                if (mNumOperadores != mNumOperadoresEnPreferences){
                    trainTask.execute();

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt("cant_operadores", mNumOperadores);
                    editor.commit();
                } else if (mNumOperadoresEnPreferences > 0){
                    operador.setEnabled(true);
                }

                if(response.isSuccessful()){
                    Asistencia.deleteAll(Asistencia.class,"estado = ?","1");
                    List<Asistencia> listaAsistencias = response.body();
                    Log.d("Size Lista Asist ", ""+ listaAsistencias.size());
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
                Toast.makeText(LoginActivity.this, "No se pudo sincronizar con el Servidor: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                layoutP.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String cedula = data.getStringExtra("cedula");

            Intent intent = new Intent(LoginActivity.this, AsistenciaActivity.class);
            intent.putExtra("metodo_query", AsistenciaActivity.FROM_CEDULA);
            intent.putExtra("cedula", cedula);

            startActivity(intent);
        }
    }

    private Training.TrainTask.Callback trainTaskCallback = new Training.TrainTask.Callback() {
        @Override
        public void onTrainTaskComplete(boolean result) {
            if (result) operador.setEnabled(true);
        }
    };
}
