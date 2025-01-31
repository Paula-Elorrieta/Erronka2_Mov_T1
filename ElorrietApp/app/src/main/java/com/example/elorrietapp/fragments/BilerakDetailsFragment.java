package com.example.elorrietapp.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Reuniones;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class BilerakDetailsFragment extends Fragment {

    FrameLayout FrameEgora;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bilerak_details, container, false);
        requireActivity().setTitle(R.string.bilerakDetails);

        Bundle bundle = getArguments();
        Reuniones reunion = (Reuniones) bundle.getSerializable("reunion");

        TextView textViewEgoera = view.findViewById(R.id.textViewEgoera);
        FrameLayout FrameEgora = view.findViewById(R.id.FrameEgora);
        koloreaEzarri(FrameEgora, reunion);
        TextView textViewGaia = view.findViewById(R.id.textViewGaia);
        TextView textViewData = view.findViewById(R.id.textViewData);
        TextView textViewTituloa = view.findViewById(R.id.textViewTituloa);
        TextView textViewNorekin = view.findViewById(R.id.textViewNorekin);
        TextView textViewNon = view.findViewById(R.id.textViewNon);

        textViewEgoera.setText("Egoera: " + reunion.getEstadoEus());
        textViewGaia.setText("Gaia: " + reunion.getAsunto());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(reunion.getFecha());
        textViewData.setText("Data: " + formattedDate);
        textViewTituloa.setText("Tituloa: " + reunion.getTitulo());

        if (Gen.getLoggedUser().getTipos() == 3) {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByAlumnoId().getNombre());
        } else {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByProfesorId().getNombre());
        }


        return view;
    }

    private void koloreaEzarri(FrameLayout FrameEgora, Reuniones reunion) {
        int color;
        if (reunion.getEstadoEus().trim().equalsIgnoreCase("onartuta")) {
            color = ContextCompat.getColor(FrameEgora.getContext(), R.color.onartuta);
        } else if (reunion.getEstadoEus().trim().equalsIgnoreCase("ezeztatuta")) {
            color = ContextCompat.getColor(FrameEgora.getContext(), R.color.ezeztatuta);
        } else if (reunion.getEstadoEus().trim().equalsIgnoreCase("gatazka")) {
            color = ContextCompat.getColor(FrameEgora.getContext(), R.color.gatazka);
        } else {
            color = ContextCompat.getColor(FrameEgora.getContext(), R.color.onartzeke);
        }
        FrameEgora.setBackgroundColor(color);
    }
}