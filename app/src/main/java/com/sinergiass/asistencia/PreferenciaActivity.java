package com.sinergiass.asistencia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinergiass.asistencia.model.helper.Constants;

/**
 * Created by Julio Alfredo on 8/11/2017.
 */

public class PreferenciaActivity extends AppCompatActivity {

    TextView ip;
    Button btn_cambiar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedpref);
        ip = (TextView)findViewById(R.id.txt_ip);
        preferences = getSharedPreferences("preferencia",Context.MODE_PRIVATE);
        ip.setText(preferences.getString("IP", Constants.HTTP.BASE_URL));
        btn_cambiar = (Button)findViewById(R.id.btn_cambiar);


        btn_cambiar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(PreferenciaActivity.this, LoginActivity.class);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("IP", ""+ip.getText());
                editor.commit();

                startActivity(intent);
//
            }
        });



    }
}
