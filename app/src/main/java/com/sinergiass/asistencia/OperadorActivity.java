package com.sinergiass.asistencia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinergiass.asistencia.ws.FaceDetectorWS;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class OperadorActivity extends AppCompatActivity {
    Button btnFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operador);
        btnFace = (Button) findViewById(R.id.btnFace);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
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
            String encodedImage = Base64.encodeToString(imagenByte, Base64.DEFAULT);

            new FaceDetectorWS().enviaImagen(encodedImage,"1");

        }
    }
}
