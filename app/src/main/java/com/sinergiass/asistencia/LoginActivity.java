package com.sinergiass.asistencia;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avera on 13/09/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button operador;
    private Button admin;
    private RestManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        operador = (Button)findViewById(R.id.operador);
        admin = (Button)findViewById(R.id.admin);

        mManager = new RestManager();

        cargarAdmins();
        cargarOperadores();


        admin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });

        operador.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });
    }

    private void cargarAdmins(){
        Call<List<Admin>> listCall = mManager.getOperadorService().getListaAdmins();
        listCall.enqueue(new Callback<List<Admin>>() {
            @Override
            public void onResponse(Call<List<Admin>> call, Response<List<Admin>> response) {

                if(response.isSuccess()){
                    Admin.deleteAll(Admin.class);
                    List<Admin> listaAdmin = response.body();

                    Log.d("El numero de la lista", ""+ listaAdmin.size());
                    for(int i=0; i<listaAdmin.size();i++){
                        final Admin admin1 = new Admin(listaAdmin.get(i).getUsuario(),
                                listaAdmin.get(i).getPassword());
//
                        Log.d("el item", ""+admin1.getUsuario());
                        admin1.save();
                    }

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

            }

            @Override
            public void onFailure(Call<List<Admin>> call, Throwable t) {

            }
        });
    }

    private void cargarOperadores(){
        Call<List<Operador>> listCall = mManager.getOperadorService().getListaOperadores();
        listCall.enqueue(new Callback<List<Operador>>() {
            @Override
            public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                if(response.isSuccess()){
                    Operador.deleteAll(Operador.class);
                    List<Operador> listaOp = response.body();
                    Log.d("El numero de la lista", ""+ listaOp.size());
                    for(int i=0; i<listaOp.size();i++){
                        final Operador operador1 = new Operador(listaOp.get(i).getIdOperador(),listaOp.get(i).getNombre(),
                                listaOp.get(i).getApellido(),listaOp.get(i).getCedula(),listaOp.get(i).getTelefono(),
                                null);
                        Log.d("el operador "+i + ":",""+operador1.getNombre());
                        operador1.save();
                    }

                }else{
                    int sc = response.code();
                    switch (sc){}
                }

            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(LoginActivity.this, AsistenciaActivity.class);
        startActivity(intent);
    }
}
