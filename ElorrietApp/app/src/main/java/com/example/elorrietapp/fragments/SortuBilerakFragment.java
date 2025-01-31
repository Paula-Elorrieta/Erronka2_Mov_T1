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

        // Inicializar spinner antes de cargar los datos
        spinnerZentroa = view.findViewById(R.id.spinnerZentroa);
        cargarIkastetxeak();

        return view;
    }

    private void cargarIkastetxeak() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ikastetxeakList = service.handleGetAllIkastetxeak();

                if (ikastetxeakList != null && !ikastetxeakList.isEmpty()) {
                    ArrayList<String> ikastetxeakNombres = new ArrayList<>();
                    for (Ikastetxeak ikastetxeak : ikastetxeakList) {
                        ikastetxeakNombres.add(ikastetxeak.getNOM());
                    }

                    // Actualizar UI en el hilo principal
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, ikastetxeakNombres);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerZentroa.setAdapter(adapter);
                        });
                    }
                } else {
                    Log.e("SortuBilerakFragment", "Lista de ikastetxeak vacía o nula");
                }
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Error obteniendo ikastetxeak", e);
            }
        });
    }
}
