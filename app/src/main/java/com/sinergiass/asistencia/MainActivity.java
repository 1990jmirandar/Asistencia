package com.sinergiass.asistencia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sinergiass.asistencia.adapter.BandaAdapter;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.TipoUsuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lista;
    List<Operador> listaOp, operadores;
    List<Asistencia> asistencias;
    List<TipoUsuario> listaTipoUsuario;
    private LinearLayout header, layoutLista, progress;
    private RestManager mManager;
    private ArrayList<Operador> listaOperadores =  new ArrayList<Operador>();
    private Operador operador;
    boolean salir = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header = (LinearLayout) findViewById(R.id.header);
        layoutLista = (LinearLayout) findViewById(R.id.lista);
        progress = (LinearLayout) findViewById(R.id.layout_progress1);

        mManager = new RestManager();


        //Adquiriendo los datos de un json a una lista
        listaOp = Operador.listAll(Operador.class);
        Log.d("el numero es",""+listaOp.size());


        //Llenando la listView
        BandaAdapter adapter = new BandaAdapter(this,R.layout.listview_item_row,listaOp);
        lista = (ListView)findViewById(R.id.listaOperador1);
        lista.setAdapter(adapter);
        //



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

     }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (salir==true){
                super.onBackPressed();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "Vuelva a presionar para salir al menu principal", Toast.LENGTH_LONG).show();
                salir = true;
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;
        switch (id){

            case R.id.nav_tipousuario:
                intent= new Intent(this, TipoUsuarioActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_operador:
                intent= new Intent(this, OperadorActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_reporte:
                intent = new Intent(this, ReporteGeneralActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_sync:
                progress.setVisibility(View.VISIBLE);
                header.setVisibility(View.GONE);
                layoutLista.setVisibility(View.GONE);

                operadores = Operador.find(Operador.class,"estado = ?", "0");
                asistencias = Asistencia.find(Asistencia.class,"estado = ?", "0");
                listaTipoUsuario = TipoUsuario.find(TipoUsuario.class,"estado=?","0");

                enviarOperadores(operadores);
                break;

            case R.id.nav_salir:
                this.finishAffinity();
                break;
            default:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    public void enviarOperadores(List<Operador> operadores){

        Call<List<Operador>> listCall = mManager.getOperadorService().guardarOp(operadores);
        listCall.enqueue(new Callback<List<Operador>>() {
            @Override
            public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                if (response.isSuccessful()) {


                    cargarOperadores();

                }
            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error al enviar los operadores: " + t.getMessage() , Toast.LENGTH_LONG).show();
            }

        });

    }

    public void enviarAsistencias(List<Asistencia> asisList){

        Call<List<Asistencia>> listCall = mManager.getOperadorService().guardarAsis(asisList);
        listCall.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {

                if (response.isSuccessful()) {


                    cargarAsistencias();
                }
            }

            @Override
            public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error al enviar las asistencias: " + t.getMessage() , Toast.LENGTH_LONG).show();

            }

        });

    }

    public void cargarOperadores(){
        Call<List<Operador>> listCall = mManager.getOperadorService().getListaOperadores();
        listCall.enqueue(new Callback<List<Operador>>() {
            @Override
            public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                if(response.isSuccessful()){

                    List<Operador> listaOp = response.body();
                    if (listaOp.size()==0){
                        Toast.makeText(MainActivity.this, "No hay registros de Operador y/o problema del server", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Operador.deleteAll(Operador.class,"estado=1");

                        for (Operador op : listaOp){

                            for (Asistencia a : asistencias){
                                if (a.getIdOperador() <= 0 && a.cedulaOperador.equals(op.getCedula())){
                                    a.setIdOperador(op.getIdOperador());
                                    a.save();
                                }
                            }

                            op.save();

                        }

                    }

                    enviarAsistencias(asistencias);
                }


            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error al enviar los operadores: " + t.getMessage() , Toast.LENGTH_LONG).show();
            }
        });

    }

    public void cargarAsistencias(){

        Call<List<Asistencia>> listCall = mManager.getOperadorService().getListaAsistencias();
        listCall.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {

                if(response.isSuccessful()){

                    List<Asistencia> listaAsistencias = response.body();
                    if(listaAsistencias.size() == 0){
                        Toast.makeText(MainActivity.this, "No hay registros de Asistencias y/o problema del server", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Asistencia.deleteAll(Asistencia.class,"estado=1");
                        for(int i=0; i<listaAsistencias.size();i++){
                            final Asistencia asistencia = new Asistencia(listaAsistencias.get(i).getIdOperador(),
                                    listaAsistencias.get(i).getLatitud(),listaAsistencias.get(i).getLongitud(),
                                    listaAsistencias.get(i).getFecha(),listaAsistencias.get(i).getHora(),
                                    listaAsistencias.get(i).isEntrada());
                            asistencia.save();
                        }
                    }

                    enviarTipoUsuario(listaTipoUsuario);
                }



            }

            @Override
            public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error al cargar las asistencias: " + t.getMessage() , Toast.LENGTH_LONG).show();


            }
        });

    }

    public void enviarTipoUsuario(List<TipoUsuario> asisList){
        if (asisList.isEmpty())  actualizaTipoUsuario(TipoUsuario.find(TipoUsuario.class,"actualiza=?","0"));
        Call<List<TipoUsuario>> listCall = mManager.getOperadorService().guardarTipoUsuario(asisList);
        listCall.enqueue(new Callback<List<TipoUsuario>>() {
            @Override
            public void onResponse(Call<List<TipoUsuario>> call, Response<List<TipoUsuario>> response) {

                if (response.isSuccessful()) {
                    actualizaTipoUsuario(TipoUsuario.find(TipoUsuario.class,"actualiza=?","0"));


                }
            }

            @Override
            public void onFailure(Call<List<TipoUsuario>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error al enviar los tipos de usuarios" , Toast.LENGTH_LONG).show();

            }

        });

    }

    public void actualizaTipoUsuario(final List<TipoUsuario> asisList){
        if (asisList.isEmpty()){
            cargarTipoUsuario();
        }


        for (TipoUsuario tipoUsuario: asisList){
            Call<TipoUsuario> listCall = mManager.getOperadorService().actualizaTipoUsuario(tipoUsuario.getIdTipoUsuario(),tipoUsuario);
            listCall.enqueue(new Callback<TipoUsuario>() {
                @Override
                public void onResponse(Call<TipoUsuario> call, Response<TipoUsuario> response) {
                    if (response.isSuccessful()) {
                        cargarTipoUsuario();
                    }
                }

                @Override
                public void onFailure(Call<TipoUsuario> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    layoutLista.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Error al enviar los tipos de usuarios" , Toast.LENGTH_LONG).show();
                }

            });
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


                    for (TipoUsuario tipoUsuario : listaAdmin){tipoUsuario.save();}

                }else{
                    int sc = response.code();
                    switch (sc){}
                }
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Sincronización Exitosa", Toast.LENGTH_LONG).show();

                onResume();

            }

            @Override
            public void onFailure(Call<List<TipoUsuario>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Conexion Fallida al cargar Tipos de usuario: " + t.getMessage(), Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);

                int numOperadores = Operador.listAll(Operador.class).size();
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putInt("cant_operadores", numOperadores);
                editor.commit();

                onResume();

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        listaOp = Operador.listAll(Operador.class);
        BandaAdapter adapter = new BandaAdapter(this,R.layout.listview_item_row,listaOp);
        lista = (ListView)findViewById(R.id.listaOperador1);
        lista.setAdapter(adapter);

    }

}
