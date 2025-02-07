package com.example.elorrietapp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Users;

import java.util.ArrayList;

public class ZerrendaAdapter extends RecyclerView.Adapter<ZerrendaAdapter.ViewHolder> {

    private ArrayList<Users> ikasleak;

    public ZerrendaAdapter(ArrayList<Users> ikasleak) {
        this.ikasleak = ikasleak;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = ikasleak.get(position);
        holder.textViewZbk.setText(String.valueOf(position + 1) + ".");
        holder.textViewNombre.setText(user.getNombre() + " " + user.getApellidos());
        holder.textViewUsername.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return ikasleak.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewZbk, textViewNombre, textViewUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewZbk = itemView.findViewById(R.id.textViewZbk);
            textViewNombre = itemView.findViewById(R.id.textViewIzena);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
        }
    }
}
