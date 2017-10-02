package com.sinergiass.asistencia;

import android.content.Intent;
import android.os.Bundle;
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

        if (id == R.id.nav_operador) {
            Intent intent = new Intent(this, OperadorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reporte) {

            Intent intent = new Intent(this, ReporteGeneralActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sync) {

            progress.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
            layoutLista.setVisibility(View.GONE);

            operadores = Operador.find(Operador.class,"estado = ?", "0");
            asistencias = Asistencia.find(Asistencia.class,"estado = ?", "0");


            enviarOperadores(operadores);



        }else if(id == R.id.nav_salir){
            this.finishAffinity();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        listaOp = Operador.listAll(Operador.class);
        BandaAdapter adapter = new BandaAdapter(this,R.layout.listview_item_row,listaOp);
        lista = (ListView)findViewById(R.id.listaOperador1);
        lista.setAdapter(adapter);

    }

    public void enviarOperadores(List<Operador> operadores){

        Call<List<Operador>> listCall = mManager.getOperadorService().guardarOp(operadores);
        listCall.enqueue(new Callback<List<Operador>>() {
            @Override
            public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                if (response.isSuccessful()) {

//                    Toast.makeText(MainActivity.this, "Actualizado de Operadores Exitosa!", Toast.LENGTH_LONG).show();
                    cargarOperadores();

                } else {
                }
            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {
                cargarOperadores();
            }

        });

    }

    public void enviarAsistencias(List<Asistencia> asisList){

        Call<List<Asistencia>> listCall = mManager.getOperadorService().guardarAsis(asisList);
        listCall.enqueue(new Callback<List<Asistencia>>() {
            @Override
            public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {

                if (response.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "Registros de asistencias sincronizados" , Toast.LENGTH_LONG).show();

                    cargarAsistencias();
                }
            }

            @Override
            public void onFailure(Call<List<Asistencia>> call, Throwable t) {

                cargarAsistencias();

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
                        Operador.deleteAll(Operador.class);
                        for(int i=0; i<listaOp.size();i++){
                            final Operador operador1 = new Operador(listaOp.get(i).getIdOperador(),listaOp.get(i).getNombre(),
                                    listaOp.get(i).getApellido(),listaOp.get(i).getCedula(),listaOp.get(i).getTelefono(),
                                    listaOp.get(i).getEncodedFaceData());
                            Log.d("operador "+i + ":",""+operador1.getNombre()+","+operador1.getIdOperador());

                            for (Asistencia a : asistencias){
                                if (a.getIdOperador() <= 0 && a.cedulaOperador.equals(operador1.getCedula())){
                                    a.setIdOperador(operador1.getIdOperador());
                                    a.save();
                                }
                            }

                            operador1.save();
                        }

                    }

                    enviarAsistencias(asistencias);
                }


            }

            @Override
            public void onFailure(Call<List<Operador>> call, Throwable t) {

                enviarAsistencias(asistencias);
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
                        Asistencia.deleteAll(Asistencia.class);
                        for(int i=0; i<listaAsistencias.size();i++){
                            final Asistencia asistencia = new Asistencia(listaAsistencias.get(i).getIdOperador(),
                                    listaAsistencias.get(i).getLatitud(),listaAsistencias.get(i).getLongitud(),
                                    listaAsistencias.get(i).getFecha(),listaAsistencias.get(i).getHora(),
                                    listaAsistencias.get(i).isEntrada());
                            asistencia.save();
                        }
                    }


                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Sincronización Exitosa", Toast.LENGTH_LONG).show();
                onResume();
                }



            }

            @Override
            public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Conexion Fallida, Intente más tarde", Toast.LENGTH_LONG).show();

                progress.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.VISIBLE);
                onResume();
            }
        });

    }




}
