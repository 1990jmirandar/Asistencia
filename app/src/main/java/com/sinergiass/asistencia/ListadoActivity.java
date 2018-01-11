package com.sinergiass.asistencia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sinergiass.asistencia.adapter.ListadoAsistenciaAdapter;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListadoActivity extends AppCompatActivity {
    ListView lstListado;
    TextView txtNoData;
    EditText txtFechaIni,txtFechaFin;
    Button btnConsultar;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    int dia, mes, anio;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        calendar= Calendar.getInstance();
        dia=calendar.get(Calendar.DAY_OF_MONTH);
        mes=calendar.get(Calendar.MONTH);
        anio=calendar.get(Calendar.YEAR);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        id = getIntent().getIntExtra("idOperador",0);
        lstListado = (ListView) findViewById(R.id.lstListado);
        txtNoData= (TextView) findViewById(R.id.txtNoData);
        List<Asistencia> lista = Asistencia.find(Asistencia.class,"id_operador="+id,null,null,"fecha",null);
        List<Operador> listaOperador = Operador.find(Operador.class,"id_operador="+id);
        setTitle(listaOperador.get(0).getNombre() + " " + listaOperador.get(0).getApellido());
        ListadoAsistenciaAdapter adapter = new ListadoAsistenciaAdapter(this,R.layout.adapter_listado_asistencia,lista);
        lstListado.setAdapter(adapter);

        txtFechaFin= (EditText) findViewById(R.id.txtFechaFin);
        txtFechaIni=(EditText)findViewById(R.id.txtFechaIni);
        btnConsultar=(Button) findViewById(R.id.btnConsultar);
        txtFechaIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog1();
            }
        });
        txtFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog2();
            }
        });
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultaAsistencia();
            }
        });
        txtNoData.setVisibility(View.GONE);
    }

    public void DateDialog1(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Date date = new Date();
                try {
                    date = simpleDateFormat.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                txtFechaIni.setText(simpleDateFormat.format(date));
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, anio, mes, dia);
        dpDialog.show();
    }

    public void DateDialog2(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Date date = new Date();
                try {
                    date = simpleDateFormat.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                txtFechaFin.setText(simpleDateFormat.format(date));
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, anio, mes, dia);
        dpDialog.show();
    }

    public void consultaAsistencia(){
        String[] values = new String[]{String.valueOf(id)};
        String fechaInicial = txtFechaIni.getText().toString().replace("-","");
        String fechaFinal = txtFechaFin.getText().toString().replace("-","");
        List<Asistencia> listaAsistencia = Asistencia.find(Asistencia.class, "id_Operador = ? and substr(fecha,1,4) || substr(fecha,6,2) || substr(fecha,9,2) between '"+fechaInicial + "' and '"+ fechaFinal + "'" , values);
        ListadoAsistenciaAdapter adapter = new ListadoAsistenciaAdapter(this,R.layout.adapter_listado_asistencia,listaAsistencia);
        lstListado.setAdapter(adapter);

        if (listaAsistencia.isEmpty()){
            txtNoData.setVisibility(View.VISIBLE);
        }else{
            txtNoData.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,AsistenciaActivity.class).putExtra("idOperador",id));
        this.finish();
        return true;
    }
}
