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

        btn_entrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginAdminActivity.this, MainActivity.class);
                // TODO: Activar la validacion en el release
                if (Admin.find(Admin.class, "username = ? and password = ?", ""+username.getText(), ""+password.getText())
                               .isEmpty()) {
                    Toast.makeText(LoginAdminActivity.this, "Usuario y/o Contraseña incorrectas", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(intent);
                }
            }
        });
    }


}
