package com.example.elorrietapp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Horarios;

import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorariosViewHolder> {

    private List<Horarios> horariosList;

    public HorariosAdapter(List<Horarios> horariosList) {
        this.horariosList = horariosList;
    }

    @Override
    public HorariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout para cada ítem en el RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new HorariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorariosViewHolder holder, int position) {
        Horarios horario = horariosList.get(position);
        holder.textViewDia.setText(horario.getId().getDia());
        holder.textViewHora.setText(horario.getId().getHora());
    }

    @Override
    public int getItemCount() {
        return horariosList.size();
    }

    public static class HorariosViewHolder extends RecyclerView.ViewHolder {

        // Elementos del layout
        TextView textViewDia;
        TextView textViewHora;

        public HorariosViewHolder(View itemView) {
            super(itemView);
            textViewDia = itemView.findViewById(R.id.textViewDia);
            textViewHora = itemView.findViewById(R.id.textViewHora);
        }
    }
}

