package com.sinergiass.asistencia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.facerecog.AddPersonPreviewActivity;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.util.DatabaseHelper;
import com.sinergiass.asistencia.ws.FaceDetectorWS;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
    TextView nombres, apellidos, cedula, telefono,guardando;
    Operador operador;
    ProgressBar progressBar;
    private RestManager mManager;
    boolean fotosCapturadasConExito;

    public static final int NUMBER_OF_PICTURES = 10;

    public int nextLocalId;
    List<Mat> fotosMat;

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
        guardando = (TextView) findViewById(R.id.guardando);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2) ;

        mManager = new RestManager();

        fotosMat = new ArrayList<>();

        List<Operador> ops = Operador.listAll(Operador.class, "id_Operador");
        if (ops.size() != 0){
            nextLocalId = ops.get(0).getIdOperador() - 1;
        }else{ nextLocalId = 0;}

        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(OperadorActivity.this, FaceRecognitionActivity.class);
//                intent.putExtra("flag_value", FaceRecognitionActivity.FLAG_CAPTURAR_DATOS);
//                startActivityForResult(intent, 0);

                Intent intent = new Intent(OperadorActivity.this, AddPersonPreviewActivity.class);
                intent.putExtra("Method", AddPersonPreviewActivity.TIME);
                intent.putExtra("numberOfPictures", NUMBER_OF_PICTURES);
                startActivityForResult(intent, 0);


            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validaciones()) {

                    guardando.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    final Intent intent = new Intent(OperadorActivity.this, MainActivity.class);
                    operador = new Operador();
                    operador.setNombre(nombres.getText().toString().trim());
                    operador.setApellido(apellidos.getText().toString().trim());
                    operador.setCedula(cedula.getText().toString());
                    operador.setTelefono(telefono.getText().toString());
                    operador.setEncodedFaceData("");

                    operador.addFotos(fotosMat);

                    operador.setEstado(0);


                    operador.setIdOperador(nextLocalId);
                    nextLocalId -= 1;

                    List<Operador> listOp = new ArrayList<>();
                    listOp.add(operador);

                    Call<List<Operador>> listCall = mManager.getOperadorService().guardarOp(listOp);
                    listCall.enqueue(new Callback<List<Operador>>() {
                        @Override
                        public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                            if (response.isSuccessful()) {
                                operador.setEstado(1);
                                Log.d("El nuevo estado es: ", "" + operador.getEstado());
                                operador.setIdOperador(response.body().get(0).getIdOperador());
                                operador.save();
                                guardando.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
//                              startActivity(intent);
                                Toast.makeText(OperadorActivity.this, "Guardado y Sincronización Exitosos!", Toast.LENGTH_LONG).show();
                                onBackPressed();

                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Operador>> call, Throwable t) {
                            operador.setEstado(0);
                            operador.save();
                            guardando.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
//                           startActivity(intent);
                            Toast.makeText(OperadorActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                    });

                }


            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            fotosCapturadasConExito = data.getBooleanExtra("exitoso", false);
            if (fotosCapturadasConExito){
                for (int i = 0; i < 10; i++){
                    byte[] faceBytes = data.getByteArrayExtra("foto"+i);

                    Mat faceMat = new Mat(faceBytes.length, 1, CvType.CV_8UC1);
                    faceMat.put(0, 0, faceBytes);

                    fotosMat.add(i, faceMat);
                }
            }
        }
    }

    private boolean validaciones(){

        if (nombres.getText().toString().isEmpty()){
            Toast.makeText(OperadorActivity.this, "Ingrese los nombres", Toast.LENGTH_LONG).show();
            return false;
        } else if (apellidos.getText().toString().isEmpty()){
            Toast.makeText(OperadorActivity.this, "Ingrese los apellidos", Toast.LENGTH_LONG).show();
            return false;
        } else if (cedula.getText().toString().length() != 10 ){
            Toast.makeText(OperadorActivity.this, "La cedula debe tener 10 digitos", Toast.LENGTH_LONG).show();
            return false;
        } else if (telefono.getText().toString().isEmpty()){
            Toast.makeText(OperadorActivity.this, "Ingrese el teléfono", Toast.LENGTH_LONG).show();
            return false;
        } else if (!fotosCapturadasConExito) {
            Toast.makeText(OperadorActivity.this, "Tome al menos 10 fotos", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
