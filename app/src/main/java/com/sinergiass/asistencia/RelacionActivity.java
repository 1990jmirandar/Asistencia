package com.sinergiass.asistencia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sinergiass.asistencia.adapter.RelacionAdapter;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.Proyecto;
import com.sinergiass.asistencia.model.ProyectoOperador;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelacionActivity extends AppCompatActivity {
    ListView lstDatos;
    Button btnGuardar;
    List<Operador> lisaOperador;
    int idProyecto;
    private RestManager mManager;
    List<ProyectoOperador> listaOperadorProyecto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacion);
        mManager=new RestManager();
        idProyecto=getIntent().getIntExtra("idProyecto",0);
        btnGuardar=(Button) findViewById(R.id.btnGuardar);
        lstDatos=(ListView) findViewById(R.id.lstDatos);
        List<ProyectoOperador> list= ProyectoOperador.find(ProyectoOperador.class,"estado='ACT' and id_proyecto<>"+ idProyecto);
        lisaOperador= Operador.find(Operador.class,"estado='ACT'" );
        for (int i=0;i<lisaOperador.size();i++){
            for (int y=0;y<list.size();y++){
                if (lisaOperador.get(i).getIdOperador()==list.get(y).getIdOperador()){
                    lisaOperador.remove(i);
                    i--;
                    break;
                }
            }
        }

        list= ProyectoOperador.find(ProyectoOperador.class,"estado='ACT' and id_proyecto="+ idProyecto);
        for (int i=0;i<lisaOperador.size();i++){
            for (int y=0;y<list.size();y++){
                if (lisaOperador.get(i).getIdOperador()==list.get(y).getIdOperador()){
                    lisaOperador.get(i).setChecked(1);
                    break;
                }
            }
        }
        RelacionAdapter adapter = new RelacionAdapter(this,R.layout.adapter_relacion_operador,lisaOperador);
        lstDatos.setAdapter(adapter);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaOperadorProyecto= new ArrayList<ProyectoOperador>();
                List<ProyectoOperador> listBorrar= ProyectoOperador.find(ProyectoOperador.class,"id_proyecto="+ idProyecto);
                for (int i=0;i<listBorrar.size();i++){
                    listBorrar.get(i).setBorrar(0);
                    listBorrar.get(i).save();
                }
                for (int i=0;i<lisaOperador.size();i++){
                    if (lisaOperador.get(i).getChecked()==1){
                        ProyectoOperador proyectoOperador = new ProyectoOperador();
                        proyectoOperador.setEstado("ACT");
                        proyectoOperador.setIdOperador(lisaOperador.get(i).getIdOperador());
                        proyectoOperador.setIdProyecto(Integer.parseInt(""+idProyecto));
                        proyectoOperador.setSync(0);
                        proyectoOperador.save();
                        listaOperadorProyecto.add(proyectoOperador);
                    }
                }
                borrarDatos(listBorrar);

            }
        });
    }

    public void borrarDatos(List<ProyectoOperador> lista){
        if (lista.isEmpty()) enviarDatos(listaOperadorProyecto);
        else{
            for(final ProyectoOperador proyectoOperadors : lista){
                Call<Void> call = mManager.getOperadorService().borrarProyectoOperador(proyectoOperadors.getIdProyectoOperador());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            proyectoOperadors.delete();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RelacionActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            enviarDatos(listaOperadorProyecto);
        }

    }

    public void enviarDatos(final List<ProyectoOperador> lista){

        Call<List<ProyectoOperador>> call = mManager.getOperadorService().guardarProyectoOperador(lista);
        call.enqueue(new Callback<List<ProyectoOperador>>() {
            @Override
            public void onResponse(Call<List<ProyectoOperador>> call, Response<List<ProyectoOperador>> response) {
                if (response.isSuccessful()){
                    cargarProyectoOperador();

                }
            }

            @Override
            public void onFailure(Call<List<ProyectoOperador>> call, Throwable t) {
                Toast.makeText(RelacionActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

    }

    private void cargarProyectoOperador(){
        Call<List<ProyectoOperador>> listCall = mManager.getOperadorService().getListaProyectoOperador();
        listCall.enqueue(new Callback<List<ProyectoOperador>>() {
            @Override
            public void onResponse(Call<List<ProyectoOperador>> call, Response<List<ProyectoOperador>> response) {

                if(response.isSuccessful()){
                    ProyectoOperador.deleteAll(ProyectoOperador.class);
                    List<ProyectoOperador> listaAdmin = response.body();


                    for (ProyectoOperador proyectoOperador : listaAdmin){
                        proyectoOperador.save();
                    }

                    Toast.makeText(RelacionActivity.this, "Informacion guardada y sincronizada", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }


            }

            @Override
            public void onFailure(Call<List<ProyectoOperador>> call, Throwable t) {
                Toast.makeText(RelacionActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }
}
