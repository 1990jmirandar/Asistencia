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

import com.sinergiass.asistencia.model.Operador;

import java.util.List;

public class OperadorMainActivity extends AppCompatActivity {
    ListView lstOperadores;
    List<Operador> listaOperador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operador_main);
        lstOperadores =  (ListView)findViewById(R.id.lstOperadores);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnNuevoOperador);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OperadorMainActivity.this, OperadorActivity.class);
                startActivity(intent);
            }
        });
        cargaOperadores();

        lstOperadores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OperadorMainActivity.this, OperadorActivity.class);
                intent.putExtra("operadorId", listaOperador.get(position).getId());
                startActivity(intent);
            }
        });

    }

    public void cargaOperadores(){
        listaOperador = Operador.listAll(Operador.class, "nombre");

        String[] operadores = new String[listaOperador.size()];
        for (int i=0;i<listaOperador.size();i++){
            operadores[i]=listaOperador.get(i).getNombre() + " " + listaOperador.get(i).getApellido() + "\n" +listaOperador.get(i).getCedula() + "\n" +listaOperador.get(i).getEstado();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,operadores);
        lstOperadores.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaOperadores();
    }
}
