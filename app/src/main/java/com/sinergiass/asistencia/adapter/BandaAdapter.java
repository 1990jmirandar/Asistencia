package com.sinergiass.asistencia.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinergiass.asistencia.R;
import com.sinergiass.asistencia.model.Banda;

/**
 * Created by Julio Alfredo on 13/9/2017.
 */

public class BandaAdapter extends ArrayAdapter<Banda>{
    Context context;
    int layoutResourceId;
    Banda data[] = null;

    public BandaAdapter(Context context, int layoutResourceId, Banda[] data){
        super(context,layoutResourceId,data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
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
            row.setTag(holder);


        }else{
            holder = (BandaHolder)row.getTag();

        }

        Banda banda = data[position];
        holder.text1.setText(banda.nombres);
        holder.text2.setText(banda.apellidos);
        holder.text3.setText(banda.cedula);




        return row;

    }

    static class BandaHolder{

        TextView text1;
        TextView text2;
        TextView text3;

    }

}
