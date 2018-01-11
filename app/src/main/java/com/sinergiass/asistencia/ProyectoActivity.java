package com.sinergiass.asistencia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Proyecto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ProyectoActivity extends AppCompatActivity {
    Button btnGuardar,btnRelacion;
    EditText txtNombre;
    TextView guardando;
    Switch swtEstado;
    ProgressBar progressBar;

    long idProyecto;
    private RestManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto);
        mManager= new RestManager();
        idProyecto = getIntent().getLongExtra("idProyecto",0);
        btnGuardar=(Button) findViewById(R.id.btnGuardar);
        btnRelacion=(Button) findViewById(R.id.btnRelacion);
        txtNombre=(EditText) findViewById(R.id.txtNombre);
        swtEstado=(Switch) findViewById(R.id.swtEstado);

        guardando = (TextView) findViewById(R.id.guardando);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2) ;

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtNombre.getText()==null || txtNombre.getText().toString().isEmpty()) {
                    Toast.makeText(ProyectoActivity.this,"Debe ingresar el nombre del proyecto",Toast.LENGTH_LONG).show();
                    return;
                }

                guardando.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Proyecto proyecto= new Proyecto();
                if(idProyecto>0){
                    proyecto = Proyecto.findById(Proyecto.class,idProyecto);
                    proyecto.setActualiza(0);
                }else{
                    proyecto.setSync(0);
                }
                proyecto.setNombre(txtNombre.getText().toString());
                proyecto.setEstado(swtEstado.isChecked() ? "ACT":"INA");
                proyecto.save();
                enviaProyectos(Proyecto.find(Proyecto.class, "sync=0"));
            }
        });
        btnRelacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idProyecto>0) {
                    Proyecto proyecto = Proyecto.findById(Proyecto.class,idProyecto);
                    if (proyecto.getIdProyecto()==0) {
                        Toast.makeText(ProyectoActivity.this, "Para cargar los operadores a este proyecto, primero debe de sincronizar el proyecto", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(ProyectoActivity.this,RelacionActivity.class);
                    intent.putExtra("idProyecto",proyecto.getIdProyecto());
                    startActivity(intent);
                }

            }
        });
        if (idProyecto>0)
            cargaDatos();
    }

    public void cargaDatos(){
        Proyecto proyecto = Proyecto.findById(Proyecto.class,idProyecto);
        txtNombre.setText(proyecto.getNombre());
        swtEstado.setChecked(proyecto.getEstado().equals("ACT")?true:false);
    }

    public void enviaProyectos(final List<Proyecto> listOp){
        if (listOp.isEmpty()) {
            actualizaProyectos(Proyecto.find(Proyecto.class,"actualiza=0"));
        }else{
            Call<List<Proyecto>> listCall = mManager.getOperadorService().guardarProyecto(listOp);
            listCall.enqueue(new Callback<List<Proyecto>>() {
                @Override
                public void onResponse(Call<List<Proyecto>> call, retrofit2.Response<List<Proyecto>> response) {

                    if (response.isSuccessful()) {
                        for (Proyecto proyecto: listOp){
                            proyecto.setSync(1);
                            proyecto.save();
                        }
                        actualizaProyectos(Proyecto.find(Proyecto.class,"actualiza=0"));
                    }
                }

                @Override
                public void onFailure(Call<List<Proyecto>> call, Throwable t) {
                    Toast.makeText(ProyectoActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }



            });
        }
    }

    public void actualizaProyectos(final List<Proyecto> listOp){
        if (listOp.isEmpty()){
            onBackPressed();
            return;
        }
        for (final Proyecto proyecto: listOp){
            Call<Proyecto> listCall = mManager.getOperadorService().actualizaProyecto(proyecto.getIdProyecto(),proyecto);
            listCall.enqueue(new Callback<Proyecto>() {
                @Override
                public void onResponse(Call<Proyecto> call, retrofit2.Response<Proyecto> response) {

                    if (response.isSuccessful()) {
                        proyecto.setActualiza(1);
                        proyecto.save();


                    }
                }

                @Override
                public void onFailure(Call<Proyecto> call, Throwable t) {
                    Toast.makeText(ProyectoActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }



            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cargarProyecto();

    }

    private void cargarProyecto(){
        Call<List<Proyecto>> listCall = mManager.getOperadorService().getListProyectos();
        listCall.enqueue(new Callback<List<Proyecto>>() {
            @Override
            public void onResponse(Call<List<Proyecto>> call, retrofit2.Response<List<Proyecto>> response) {

                if(response.isSuccessful()){
                    Proyecto.deleteAll(Proyecto.class);
                    List<Proyecto> listaAdmin = response.body();


                    for (Proyecto proyecto : listaAdmin){proyecto.save();}
                    Toast.makeText(ProyectoActivity.this, "Guardado y Sincronización Exitosos!", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }else{
                    int sc = response.code();
                    switch (sc){}
                }


            }

            @Override
            public void onFailure(Call<List<Proyecto>> call, Throwable t) {
                Toast.makeText(ProyectoActivity.this, "Conexion Fallida al cargar los proyectos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }


}
