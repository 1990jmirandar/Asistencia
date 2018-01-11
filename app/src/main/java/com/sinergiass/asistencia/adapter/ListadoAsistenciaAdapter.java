package com.sinergiass.asistencia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sinergiass.asistencia.MapsActivity;
import com.sinergiass.asistencia.R;
import com.sinergiass.asistencia.ReporteAsistenciaActivity;
import com.sinergiass.asistencia.model.Asistencia;

import java.util.List;

/**
 * Created by avera on 27/12/17.
 */

public class ListadoAsistenciaAdapter extends ArrayAdapter<Asistencia>{
    private Context context;
    private List<Asistencia> data;
    private int resource;
    public ListadoAsistenciaAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Asistencia> objects) {
        super(context, resource, objects);
        this.context=context;
        this.data=objects;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(resource,null);
        TextView fecha = v.findViewById(R.id.txtFecha);
        TextView hora = v.findViewById(R.id.txtHora);
        TextView estado = v.findViewById(R.id.txtEstado);
        ImageButton mapa = v.findViewById(R.id.btnVer);
        fecha.setText(data.get(position).getFecha());
        hora.setText(data.get(position).getHora());
        estado.setText(data.get(position).isEntrada() ? "Entrada":"Salida");
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MapsActivity.class);
                Bundle extras = new Bundle();
                extras.putDouble("longitud",Double.parseDouble(data.get(position).getLongitud()));
                extras.putDouble("latitud",Double.parseDouble(data.get(position).getLatitud()));
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
        return v;
    }
}
