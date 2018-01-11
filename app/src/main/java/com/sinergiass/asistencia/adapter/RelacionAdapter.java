package com.sinergiass.asistencia.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sinergiass.asistencia.R;
import com.sinergiass.asistencia.model.Operador;
import com.sinergiass.asistencia.model.ProyectoOperador;

import java.util.List;

/**
 * Created by avera on 05/12/17.
 */

public class RelacionAdapter extends ArrayAdapter<Operador> {
    private int resourceId;
    private List<Operador> data;
    private Context context;

    public RelacionAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Operador> objects) {
        super(context, resource, objects);
        this.context=context;
        this.data=objects;
        this.resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        Holder holder= new Holder();
        if (v==null){
            v= LayoutInflater.from(context).inflate(resourceId,null);
            holder.txtNombre=(TextView) v.findViewById(R.id.txtNombre);
            holder.chk=(CheckBox) v.findViewById(R.id.chk);
            v.setTag(holder);
        }else{
            holder=(Holder) v.getTag();
        }
        holder.txtNombre.setText(data.get(position).getNombre());
        holder.chk.setChecked(data.get(position).getChecked()==1 ?true: false);
        holder.chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.get(position).setChecked(b ? 1 : 0);
            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    class Holder{
        TextView txtNombre;
        CheckBox chk;
    }
}
