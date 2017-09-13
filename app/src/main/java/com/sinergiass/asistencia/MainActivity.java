package com.sinergiass.asistencia;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends  AppCompatActivity {

    private Button ubicarme;
    private double[] datosCara = {5.32,6.84,3.21};
    private Operador operador = new Operador("0950676395","Julio Alfredo","Larrea Sanchez","0992108894",datosCara);
    private TextView nombre,apellido,cedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_asistencia);

        ubicarme = (Button)findViewById(R.id.ubicacion);
        nombre = (TextView)findViewById(R.id.nombres);
        apellido = (TextView)findViewById(R.id.apellidos);
        cedula = (TextView)findViewById(R.id.cedula);

        nombre.setText(operador.nombre);
        apellido.setText(operador.apellido);
        cedula.setText(operador.cedula);

        ubicarme.setOnClickListener(new View.OnClickListener(){
           public void onClick(View arg0){
               Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }

        });

    }
}
