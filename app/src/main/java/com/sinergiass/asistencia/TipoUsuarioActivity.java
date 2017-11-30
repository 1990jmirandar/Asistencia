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

import com.sinergiass.asistencia.model.TipoUsuario;

import java.util.List;

public class TipoUsuarioActivity extends AppCompatActivity {
    ListView lstTiposUsuarios;
    List<TipoUsuario> listaTipoUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_usuario);
        lstTiposUsuarios = (ListView) findViewById(R.id.lstTiposUsuarios);
        cargaTipoUsuario();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TipoUsuarioActivity.this,IngresoTipoUsuarioActivity.class);
                startActivity(intent);
            }
        });
        lstTiposUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TipoUsuarioActivity.this,IngresoTipoUsuarioActivity.class);
                intent.putExtra("tipoUsuarioId",listaTipoUsuario.get(i).getId());
                startActivity(intent);
            }});
    }


    public void cargaTipoUsuario(){
        listaTipoUsuario=TipoUsuario.listAll(TipoUsuario.class);
        String[] tusuarios = new String[listaTipoUsuario.size()];
        for (int i=0; i<listaTipoUsuario.size();i++){
            tusuarios[i]=listaTipoUsuario.get(i).getNombre();

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tusuarios);
        lstTiposUsuarios.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaTipoUsuario();
    }
}
