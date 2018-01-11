package com.sinergiass.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sinergiass.asistencia.model.Proyecto;

import java.util.List;

public class ProyectoMainActivity extends AppCompatActivity {
    ListView lstDatos;
    List<Proyecto> listaDatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto_main);

        lstDatos = (ListView) findViewById(R.id.lstDatos);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProyectoMainActivity.this, ProyectoActivity.class);
                startActivity(intent);
            }
        });

        cargaLista();
        lstDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ProyectoMainActivity.this, ProyectoActivity.class);
                intent.putExtra("idProyecto",listaDatos.get(i).getId());
                startActivity(intent);
            }
        });

    }

    public void cargaLista(){
        listaDatos = Proyecto.listAll(Proyecto.class);
        String[] datos = new String[listaDatos.size()];
        for (int i=0; i<listaDatos.size(); i++){
            datos[i]= listaDatos.get(i).getNombre() + "\n" + listaDatos.get(i).getEstado();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, datos);
        lstDatos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaLista();
    }
}
