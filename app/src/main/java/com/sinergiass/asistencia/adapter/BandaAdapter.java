package com.sinergiass.asistencia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinergiass.asistencia.AsistenciaActivity;
import com.sinergiass.asistencia.R;
import com.sinergiass.asistencia.ReporteAsistenciaActivity;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Banda;
import com.sinergiass.asistencia.model.Operador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Julio Alfredo on 13/9/2017.
 */

public class BandaAdapter extends ArrayAdapter<Operador>{
    Context context;
    int layoutResourceId;
    List<Operador> listaOperador;
    private Asistencia asistencia;

    public BandaAdapter(Context context, int layoutResourceId, List<Operador> data){
        super(context,layoutResourceId,data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listaOperador = data;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        BandaHolder holder = null;

        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);



            holder = new BandaHolder();
            holder.text1 = (TextView)row.findViewById(R.id.nombres);
            holder.text2 = (TextView)row.findViewById(R.id.apellidos);
            holder.text3 = (TextView)row.findViewById(R.id.cedula);
            holder.revisarAsistencia = (Button) row.findViewById(R.id.revisarAsistencia);
            holder.registrar = (Button) row.findViewById(R.id.registrar);
            row.setTag(holder);


        }else{
            holder = (BandaHolder)row.getTag();

        }

        final Operador operador = listaOperador.get(position);

        //Log.d("idOperador",""+operador.getIdOperador());

        List<Asistencia> asistencias = Asistencia.find(Asistencia.class , "id_Operador = ? and fecha = ?", new String[]{""+operador.getIdOperador(),new SimpleDateFormat("yyyy-MM-dd").format(new Date())});

        if(asistencias.size()==0){
            row.setBackgroundColor(Color.parseColor("#ff6347"));
        }else{
            row.setBackgroundColor(Color.parseColor("#bdd4de"));
        }

        if(asistencias.size()==2){
            holder.registrar.setEnabled(false);
        }else{
            holder.registrar.setEnabled(true);
        }


        holder.text1.setText(operador.getNombre());
        holder.text2.setText(operador.getApellido());
        holder.text3.setText(operador.getCedula());
        holder.revisarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReporteAsistenciaActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("idOperador",operador.getIdOperador());
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
        holder.registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AsistenciaActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("idOperador",operador.getIdOperador());
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });




        return row;

    }

    static class BandaHolder{

        TextView text1;
        TextView text2;
        TextView text3;
        Button registrar;
        Button revisarAsistencia;

    }

}
