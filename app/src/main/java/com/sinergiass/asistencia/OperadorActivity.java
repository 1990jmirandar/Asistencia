package com.sinergiass.asistencia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.ws.FaceDetectorWS;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class OperadorActivity extends AppCompatActivity {
    Button btnFace,guardar;
    TextView nombres, apellidos, cedula, telefono;
    Operador operador;
    private RestManager mManager;
    String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operador);

        btnFace = (Button) findViewById(R.id.btnFace);
        guardar = (Button) findViewById(R.id.guardar);
        nombres = (TextView) findViewById(R.id.txt_nombres);
        apellidos = (TextView) findViewById(R.id.txt_apellidos);
        cedula = (TextView) findViewById(R.id.txt_cedula);
        telefono = (TextView) findViewById(R.id.txt_telefono);

        mManager = new RestManager();

        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OperadorActivity.this, MainActivity.class);
                operador = new Operador();
                HashMap<String, String> parameters = new HashMap<>();
                operador.setNombre(nombres.getText().toString());
                operador.setApellido(apellidos.getText().toString());
                operador.setCedula(cedula.getText().toString());
                operador.setTelefono(telefono.getText().toString());
                operador.setEncodedFaceData("");
                operador.setEstado(0);

                operador.setIdOperador(-1);     // TODO - Posible solucion para la sincronizacion con el webservice

                parameters.put("nombre", ""+operador.getNombre());
                parameters.put("apellido", ""+operador.getApellido());
                parameters.put("cedula", ""+operador.getCedula());
                parameters.put("telefono", ""+operador.getTelefono());
                parameters.put("encodedFaceData", ""+operador.getEncodedFaceData());

                enviarOperador(parameters);


                startActivity(intent);
                Toast.makeText(OperadorActivity.this, "Guardado Exitoso!", Toast.LENGTH_LONG).show();
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imagenByte = baos.toByteArray();
            encodedImage = Base64.encodeToString(imagenByte, Base64.DEFAULT);

            new FaceDetectorWS().enviaImagen(encodedImage,"1");

        }
    }

    private void enviarOperador( HashMap<String, String> parameters) {
        Call<Operador> listCall = mManager.getOperadorService().guardarOp(parameters);
        listCall.enqueue(new Callback<Operador>() {
            @Override
            public void onResponse(Call<Operador> call, Response<Operador> response) {

                if (response.isSuccess()) {
                    operador.setEstado(1);
                    Log.d("El nuevo estado es: ",""+operador.getEstado());
                    operador.save();

                } else {
                }
            }

            @Override
            public void onFailure(Call<Operador> call, Throwable t) {
                operador.setEstado(0);
                operador.save();

            }

        });
    }
}
