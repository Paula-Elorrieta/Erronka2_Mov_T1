package com.example.elorrietapp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Reuniones;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BilerakAdapter extends RecyclerView.Adapter<BilerakAdapter.ViewHolder> {
    private ArrayList<Reuniones> reuniones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Reuniones reunion);
    }

    public BilerakAdapter(ArrayList<Reuniones> reuniones, OnItemClickListener listener) {
        this.reuniones = reuniones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bilerak, parent, false);
        return new ViewHolder(view);
    }

    public void setReuniones(ArrayList<Reuniones> nuevasReuniones) {
        this.reuniones.clear();
        this.reuniones.addAll(nuevasReuniones);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reuniones reunion = reuniones.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(reunion.getFecha());
        holder.textViewData.setText(formattedDate);
        holder.textViewTitulua.setText(reunion.getTitulo());
        holder.textViewAsuntoa.setText(reunion.getAsunto());

        int color;
        if (reunion.getEstadoEus().trim().equalsIgnoreCase("onartuta")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.onartuta);
        } else if (reunion.getEstadoEus().trim().equalsIgnoreCase("ezeztatuta")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.ezeztatuta);
        } else if (reunion.getEstadoEus().trim().equalsIgnoreCase("gatazka")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.gatazka);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.onartzeke);
        }
        holder.FrameEgoera.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(reunion);
            }
        });
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
            textViewAsuntoa = itemView.findViewById(R.id.textViewGaia);
            FrameEgoera = itemView.findViewById(R.id.FrameEgoera);
        }
    }
}
