package com.sinergiass.asistencia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReporteAsistenciaActivity extends AppCompatActivity {
    int idOperador;
    Operador operador;
    EditText txtFechaReporte;
    TextView txtHoraEntrada, txtHoraSalida;
    Button btnEntrada,btnSalida;
    Calendar calendar;
    int dia, mes, anio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_asistencia);
        calendar= Calendar.getInstance();
        dia=calendar.get(Calendar.DAY_OF_MONTH);
        mes=calendar.get(Calendar.MONTH);
        anio=calendar.get(Calendar.YEAR);
        idOperador = getIntent().getExtras().getInt("idOperador");
        Log.d("Operador con id",""+idOperador);
        operador = Operador.find(Operador.class, "id_Operador = ?", "" + idOperador).get(0);
        txtFechaReporte= (EditText) findViewById(R.id.txtFechaReporte);
        txtHoraEntrada=(TextView) findViewById(R.id.txtHoraEntrada);
        txtHoraSalida=(TextView) findViewById(R.id.txtHoraSalida);
        btnEntrada=(Button) findViewById(R.id.btnEntrada);
        btnSalida=(Button) findViewById(R.id.btnSalida);
        txtFechaReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog();
            }
        });
    }

    public void DateDialog(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = simpleDateFormat.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                txtFechaReporte.setText(simpleDateFormat.format(date));
                consultaAsistencia();
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, anio, mes, dia);
        dpDialog.show();
    }

    public void consultaAsistencia(){
        String[] values = new String[]{String.valueOf(operador.getIdOperador()),txtFechaReporte.getText().toString()};
        Log.i("TAG-TAG", values[0]);
        Log.i("TAG-TAG", values[1]);

        List<Asistencia> listaAsistencia = Asistencia.find(Asistencia.class, "id_Operador = ? and fecha=?", values);
        if (listaAsistencia.isEmpty()){
            txtHoraEntrada.setText("No existe informacion");
            btnEntrada.setTag(null);
            txtHoraSalida.setText("No existe informacion");
            btnSalida.setTag(null);
            return;
        }
        for (int i=0; i<listaAsistencia.size();i++){
            if (listaAsistencia.get(i).isEntrada()){
                txtHoraEntrada.setText(listaAsistencia.get(i).getHora());
                btnEntrada.setTag(listaAsistencia.get(i));
            }else{
                txtHoraSalida.setText(listaAsistencia.get(i).getHora());
                btnSalida.setTag(listaAsistencia.get(i));
            }
        }
    }


    public void verMapaEntrada(View v){
        if (v.getTag()==null) return;
        callMapActivity(v);
    }

    public void verMapaSalida(View v){
        if (v.getTag()==null) return;
        callMapActivity(v);
    }

    public void callMapActivity(View v){
        if (v.getTag()==null) return;
        Intent intent = new Intent(ReporteAsistenciaActivity.this,MapsActivity.class);
        Bundle extras = new Bundle();
        extras.putDouble("longitud",Double.parseDouble(((Asistencia)v.getTag()).getLongitud()));
        extras.putDouble("latitud",Double.parseDouble(((Asistencia)v.getTag()).getLatitud()));
        intent.putExtras(extras);
        startActivity(intent);
    }
}

