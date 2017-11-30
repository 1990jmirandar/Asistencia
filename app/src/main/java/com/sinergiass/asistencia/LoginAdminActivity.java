package com.sinergiass.asistencia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.helper.Constants;

import java.util.List;

public class LoginAdminActivity extends AppCompatActivity {

    private Button btn_entrar;
    private TextView username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        username = (TextView)findViewById(R.id.txt_usuario);
        password = (TextView)findViewById(R.id.txt_contrasena);
        btn_entrar = (Button)findViewById(R.id.btn_entrar);

        Log.d("La IP es: ",""+ Constants.HTTP.BASE_URL);

        btn_entrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                List<Admin> lista= Admin.find(Admin.class,"username=? and password=?",new String[]{username.getText().toString(),password.getText().toString()});
                if (lista.isEmpty()){
                    Toast.makeText(LoginAdminActivity.this,"Usuario o clave incorrectas",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(LoginAdminActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
    }


}
