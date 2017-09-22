package com.sinergiass.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListView;

import com.sinergiass.asistencia.adapter.BandaAdapter;
import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Banda;
import com.sinergiass.asistencia.model.Operador;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lista;
    private RecyclerView lista1;
    List<Operador> listaOp;
    private RestManager mManager;
    private ArrayList<Operador> listaOperadores =  new ArrayList<Operador>();
    private Operador operador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Adquiriendo los datos de un json a una lista
        listaOp = Operador.listAll(Operador.class);
        Log.d("el numero es",""+listaOp.size());
        //Llenando la listView
        /*Banda bandas[] = new Banda[listaOp.size()];

        for(int x=0;x<listaOp.size();x++) {
            operador = listaOp.get(x);
            bandas[x] = new Banda(operador.getNombre(),operador.getApellido(),operador.getCedula());
        }*/

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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_operador) {
            Intent intent = new Intent(this, OperadorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_asistencia) {
            Intent intent = new Intent(this, AsistenciaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_reporte) {

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
