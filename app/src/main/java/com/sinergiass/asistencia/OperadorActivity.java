package com.sinergiass.asistencia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sinergiass.asistencia.controller.RestManager;
import com.sinergiass.asistencia.facerecog.AddPersonPreviewActivity;
import com.sinergiass.asistencia.model.Admin;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.TipoUsuario;
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
    long operadorId;
    List<TipoUsuario> listTipoUsuario;
    Button btnFace,guardar;
    TextView nombres, apellidos, cedula, telefono,guardando;
    Operador operador;
    ProgressBar progressBar;
    Switch swtActivo;
    private RestManager mManager;
    boolean fotosCapturadasConExito;
    Spinner spnTipoUsuario;

    public static final int NUMBER_OF_PICTURES = 5;


    List<String> fotosEncondings;

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
        swtActivo = (Switch) findViewById(R.id.swtActivo);
        spnTipoUsuario = (Spinner) findViewById(R.id.spnTipoUsuario);

        mManager = new RestManager();

        fotosEncondings = new ArrayList<>();

        List<Operador> ops = Operador.listAll(Operador.class, "id_Operador");

        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    operador = new Operador();
                    if (operadorId>0){
                        operador = Operador.findById(Operador.class,operadorId);
                        operador.setId(operadorId);
                        operador.setActualiza(0);
                    }else{
                        operador.setSync(0);
                    }
                    operador.setNombre(nombres.getText().toString().trim());
                    operador.setApellido(apellidos.getText().toString().trim());
                    operador.setCedula(cedula.getText().toString());
                    operador.setTelefono(telefono.getText().toString());
                    operador.setEstado(swtActivo.isChecked() ? "ACT":"INA");
                    operador.setIdTipoUsuario(listTipoUsuario.get(Integer.parseInt(""+spnTipoUsuario.getSelectedItemId())).getIdTipoUsuario());
                    if (!fotosEncondings.isEmpty())
                        operador.addFotos(fotosEncondings);
                    operador.save();
                    List<Operador> listOp = new ArrayList<>();
                    listOp.add(operador);

                    enviaOperadores(listOp);


                }


            }
        });


        cargaTipoOperador();
        operadorId=getIntent().getLongExtra("operadorId",0);
        if (operadorId>0)
            cargaOperador(operadorId);



    }
    public void cargaTipoOperador(){
        listTipoUsuario = TipoUsuario.find(TipoUsuario.class,"sync=1");
        String[] tipoUsuario= new String[listTipoUsuario.size()];
        for (int i=0;i<listTipoUsuario.size();i++){
            tipoUsuario[i]=listTipoUsuario.get(i).getNombre();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,tipoUsuario);
        spnTipoUsuario.setAdapter(adapter);

    }

    public void enviaOperadores(List<Operador> listOp){
        if (listOp.get(0).getActualiza()==1){
            Call<List<Operador>> listCall = mManager.getOperadorService().guardarOp(listOp);
            listCall.enqueue(new Callback<List<Operador>>() {
                @Override
                public void onResponse(Call<List<Operador>> call, Response<List<Operador>> response) {

                    if (response.isSuccessful()) {
                        operador.setSync(1);
                        operador.setIdOperador(response.body().get(0).getIdOperador());
                        operador.save();
                        guardando.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OperadorActivity.this, "Guardado y Sincronización Exitosos!", Toast.LENGTH_LONG).show();

                        int numOperadores = Operador.listAll(Operador.class).size();
                        SharedPreferences preferences = getSharedPreferences("preferencia",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("cant_operadores", numOperadores);
                        editor.commit();

                        onBackPressed();

                    }
                }

                @Override
                public void onFailure(Call<List<Operador>> call, Throwable t) {
                    guardando.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OperadorActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }



            });
        }else{

            Call<Operador> listCall = mManager.getOperadorService().actualizarOp(listOp.get(0).getIdOperador(),listOp.get(0));
            listCall.enqueue(new Callback<Operador>() {
                @Override
                public void onResponse(Call<Operador> call, Response<Operador> response) {

                    if (response.isSuccessful()) {
                        operador.setActualiza(1);
                        operador.save();
                        guardando.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OperadorActivity.this, "Guardado y Sincronización Exitosos!", Toast.LENGTH_LONG).show();

                        int numOperadores = Operador.listAll(Operador.class).size();
                        SharedPreferences preferences = getSharedPreferences("preferencia",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("cant_operadores", numOperadores);
                        editor.commit();

                        onBackPressed();

                    }
                }

                @Override
                public void onFailure(Call<Operador> call, Throwable t) {
                    guardando.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OperadorActivity.this, "Sin Conexión, Guardado Local Exitoso!", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }



            });

        }


    }

    public void cargaOperador(long operador){
        Operador operador1 = Operador.findById(Operador.class,operador);
        nombres.setText(operador1.getNombre());
        apellidos.setText(operador1.getApellido());
        cedula.setText(operador1.getCedula());
        telefono.setText(operador1.getTelefono());
        swtActivo.setChecked(operador1.getEstado().equals("ACT")? true : false);
        for (int i=0;i< listTipoUsuario .size();i++){
            if(operador1.getIdTipoUsuario()==listTipoUsuario.get(i).getIdTipoUsuario())
                spnTipoUsuario.setSelection(i);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            fotosCapturadasConExito = data.getBooleanExtra("exitoso", false);
            if (fotosCapturadasConExito){
                for (int i = 0; i < NUMBER_OF_PICTURES; i++){
                    byte[] faceBytes = data.getByteArrayExtra("foto"+i);

                    fotosEncondings.add(Base64.encodeToString(faceBytes, Base64.DEFAULT));
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
        } else if (!fotosCapturadasConExito && operadorId==0) {
            Toast.makeText(OperadorActivity.this, "Tome las fotos", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
