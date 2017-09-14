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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sinergiass.asistencia.adapter.BandaAdapter;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Banda;
import com.sinergiass.asistencia.model.Operador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lista;
    private ArrayList<Operador> listaOperadores =  new ArrayList<Operador>();
    private Operador operador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //llenado lista de operadores desde la base
        //List<Operador> operadores = Operador.find(Operador.class , "id_Operador = ?", new String[]{""+operador.getIdOperador()});

        for(int x = 0; x<12; x++){
            operador = new Operador("0950676395","Julio Alfredo","Larrea Sanchez","0992108894","2.38,5.12,6",x+1);
            listaOperadores.add(operador);
        }





        Banda bandas[] = new Banda[listaOperadores.size()];

        for(int x=0;x<listaOperadores.size();x++) {
            operador = listaOperadores.get(x);
            bandas[x] = new Banda(operador.getNombre(),operador.getApellido(),operador.getCedula());

        }




        BandaAdapter adapter = new BandaAdapter(this,R.layout.listview_item_row,bandas);

        lista = (ListView)findViewById(R.id.listaOperador);



        lista.setAdapter(adapter);



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
