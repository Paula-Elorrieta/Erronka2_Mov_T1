package com.example.elorrietapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Ikastetxeak;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SortuBilerakFragment extends Fragment {

    private Spinner spinnerZentroa;
    private Service service = new Service();
    private ArrayList<Ikastetxeak> ikastetxeakList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sortu_bilerak, container, false);

        spinnerZentroa = view.findViewById(R.id.spinnerZentroa); // Asegúrate de tener este Spinner en tu XML

        cargarIkastetxeak();

        return view;
    }

    private void cargarIkastetxeak() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ikastetxeakList = service.handleGetIkastetxeak();
                if (ikastetxeakList != null) {
                    ArrayList<String> nombres = new ArrayList<>();
                    for (Ikastetxeak ikastetxea : ikastetxeakList) {
                        nombres.add(ikastetxea.getNOM());
                    }

                    // Actualizar UI en el hilo principal
                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, nombres);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerZentroa.setAdapter(adapter);
                    });
                } else {
                    Log.e("SortuBilerakFragment", "Error: ikastetxeakList es null");
                }
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Error obteniendo ikastetxeak", e);
            }
        });
    }
}
