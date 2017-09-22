package com.sinergiass.asistencia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

/**
 * Created by Julio Alfredo on 13/9/2017.
 */

public class BandaAdapter extends ArrayAdapter<Operador>{
    Context context;
    int layoutResourceId;
    List<Operador> listaOperador;

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
        holder.text1.setText(operador.getNombre());
        holder.text2.setText(operador.getApellido());
        holder.text3.setText(operador.getCedula());
        holder.revisarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReporteAsistenciaActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("idOperador",operador.getId());
                context.startActivity(intent);
            }
        });
        holder.registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AsistenciaActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("idOperador",operador.getId());
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
