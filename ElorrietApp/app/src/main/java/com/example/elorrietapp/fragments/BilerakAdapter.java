package com.example.elorrietapp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Reuniones;

import java.util.ArrayList;

public class BilerakAdapter extends RecyclerView.Adapter<BilerakAdapter.ViewHolder> {
    private ArrayList<Reuniones> reuniones;

    public BilerakAdapter(ArrayList<Reuniones> reuniones) {
        this.reuniones = reuniones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bilerak, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reuniones reunion = reuniones.get(position);

        holder.textViewData.setText(reunion.getFecha().toString());
        holder.textViewTitulua.setText(reunion.getTitulo());
        holder.textViewAsuntoa.setText(reunion.getAsunto());

        if (reunion.getEstado().equalsIgnoreCase(String.valueOf(R.string.onartuta))) {
            holder.FrameEgoera.setBackgroundColor(holder.itemView.getResources().getColor(R.color.onartuta));
        } else if (reunion.getEstado().equalsIgnoreCase(String.valueOf(R.string.ezeztatuta))) {
            holder.FrameEgoera.setBackgroundColor(holder.itemView.getResources().getColor(R.color.ezeztatuta));
        } else if (reunion.getEstado().equalsIgnoreCase(String.valueOf(R.string.gatazka))) {
            holder.FrameEgoera.setBackgroundColor(holder.itemView.getResources().getColor(R.color.gatazka));
        } else {
            holder.FrameEgoera.setBackgroundColor(holder.itemView.getResources().getColor(R.color.onartzeke));
        }

    }

    @Override
    public int getItemCount() {
        return reuniones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewData, textViewTitulua, textViewAsuntoa;
        FrameLayout FrameEgoera;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewTitulua = itemView.findViewById(R.id.textViewTitulua);
            textViewAsuntoa = itemView.findViewById(R.id.textViewAsuntoa);
            FrameEgoera = itemView.findViewById(R.id.FrameEgoera);
        }
    }


}
