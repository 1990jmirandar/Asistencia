package com.sinergiass.asistencia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginAdminActivity extends AppCompatActivity {

    private Button btn_entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        btn_entrar = (Button)findViewById(R.id.btn_entrar);

        btn_entrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginAdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
