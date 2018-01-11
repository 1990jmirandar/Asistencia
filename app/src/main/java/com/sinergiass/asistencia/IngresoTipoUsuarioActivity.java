package com.sinergiass.asistencia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.TipoUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngresoTipoUsuarioActivity extends AppCompatActivity {
    EditText txtTipoUsuario;
    Button btnGuardar;
    Switch swtActivo;
    private RestManager mManager;
    long tipoUsuarioId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = new RestManager();
        setContentView(R.layout.activity_ingreso_tipo_usuario);
        tipoUsuarioId = getIntent().getLongExtra("tipoUsuarioId",0);
        txtTipoUsuario = (EditText) findViewById(R.id.txtTipoUsuario);
        swtActivo = (Switch) findViewById(R.id.swtActivo);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtTipoUsuario.getText()!=null && !txtTipoUsuario.getText().toString().isEmpty()){
                    TipoUsuario tusuario;
                    if (tipoUsuarioId>0) {
                        tusuario = TipoUsuario.findById(TipoUsuario.class,tipoUsuarioId);
                        tusuario.setEstado(swtActivo.isChecked() ? "ACT":"INA");
                        tusuario.setNombre(txtTipoUsuario.getText().toString());
                        tusuario.setActualiza(0);
                        tusuario.save();
                    }else{
                        tusuario =new TipoUsuario(0,txtTipoUsuario.getText().toString(),swtActivo.isChecked() ? "ACT":"INA");
                        tusuario.setSync(0);
                        tusuario.save();
                    }
                    enviarTipoUsuario(TipoUsuario.find(TipoUsuario.class,"sync=?","0"));

                }else{
                    Toast.makeText(IngresoTipoUsuarioActivity.this,"Debe de agregar el tipo de usuario a registrar",Toast.LENGTH_LONG).show();
                }

            }
        });

        if (tipoUsuarioId>0) {
            TipoUsuario tusuario = TipoUsuario.findById(TipoUsuario.class,tipoUsuarioId);
            txtTipoUsuario.setText(tusuario.getNombre());
            swtActivo.setChecked(tusuario.getEstado().equals("ACT")?true:false);
        }
    }


    public void enviarTipoUsuario(final List<TipoUsuario> asisList){
        if (asisList.isEmpty())  actualizaTipoUsuario(TipoUsuario.find(TipoUsuario.class,"actualiza=?","0"));
        for (TipoUsuario tipoUsuario: asisList){
            Call<List<TipoUsuario>> listCall = mManager.getOperadorService().guardarTipoUsuario(asisList);
            listCall.enqueue(new Callback<List<TipoUsuario>>() {
                @Override
                public void onResponse(Call<List<TipoUsuario>> call, Response<List<TipoUsuario>> response) {
                    if (response.isSuccessful()) {
                        for (TipoUsuario usuario : asisList){
                            usuario.setSync(1);
                            usuario.update();
                        }
                        actualizaTipoUsuario(TipoUsuario.find(TipoUsuario.class,"actualiza=?","0"));
                    }
                }

                @Override
                public void onFailure(Call<List<TipoUsuario>> call, Throwable t) {
                    Toast.makeText(IngresoTipoUsuarioActivity.this, "Error al enviar los tipos de usuarios" , Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

            });
        }


    }

    public void actualizaTipoUsuario(final List<TipoUsuario> asisList){
        if (asisList.isEmpty()){
            Toast.makeText(IngresoTipoUsuarioActivity.this, "Registros de tipos de usuarios sincronizados" , Toast.LENGTH_LONG).show();
            cargarTipoUsuario();
        }


        for (final TipoUsuario tipoUsuario: asisList){
            Call<TipoUsuario> listCall = mManager.getOperadorService().actualizaTipoUsuario(tipoUsuario.getIdTipoUsuario(),tipoUsuario);
            listCall.enqueue(new Callback<TipoUsuario>() {
                @Override
                public void onResponse(Call<TipoUsuario> call, Response<TipoUsuario> response) {
                    if (response.isSuccessful()) {
                        tipoUsuario.setActualiza(1);
                        tipoUsuario.save();
                    }
                }

                @Override
                public void onFailure(Call<TipoUsuario> call, Throwable t) {
                    Toast.makeText(IngresoTipoUsuarioActivity.this, "Error al enviar los tipos de usuarios" , Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

            });
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cargarTipoUsuario();

    }

    private void cargarTipoUsuario(){
        Call<List<TipoUsuario>> listCall = mManager.getOperadorService().getListaTipoUsuario();
        listCall.enqueue(new Callback<List<TipoUsuario>>() {
            @Override
            public void onResponse(Call<List<TipoUsuario>> call, Response<List<TipoUsuario>> response) {

                if(response.isSuccessful()){
                    TipoUsuario.deleteAll(TipoUsuario.class,"sync=1 or actualiza=1");
                    List<TipoUsuario> listaAdmin = response.body();
                    for (TipoUsuario tipoUsuario : listaAdmin){tipoUsuario.save();}
                    onBackPressed();
                }else{
                    int sc = response.code();
                    switch (sc){}
                }

            }

            @Override
            public void onFailure(Call<List<TipoUsuario>> call, Throwable t) {
                Toast.makeText(IngresoTipoUsuarioActivity.this, "Conexion Fallida al cargar Tipos de usuario: " + t.getMessage(), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }
}
