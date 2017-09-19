package com.sinergiass.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Operador;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Julio Alfredo on 19/9/2017.
 */

public class IntroActivity extends AppCompatActivity {
    private RestManager mManager;
    private ProgressBar progressBar;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void publish(LogRecord logRecord) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        mManager = new RestManager();
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
                                listaOp.get(i).getDatosCara());
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
        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
