package com.sinergiass.asistencia;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sinergiass.asistencia.model.Asistencia;

/**
 * Created by avera on 13/09/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button operador;
    private Button admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        operador = (Button)findViewById(R.id.operador);
        admin = (Button)findViewById(R.id.admin);


        admin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });

        operador.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(LoginActivity.this, AsistenciaActivity.class);
        startActivity(intent);
    }
}
