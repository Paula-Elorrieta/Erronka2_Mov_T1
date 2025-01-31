package com.example.elorrietapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Ikastetxeak;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Users;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SortuBilerakFragment extends Fragment {

    private Spinner spinnerZentroa, spinnerNorekin;
    private EditText editTextGela, editTextTitulua, editTextAsuntoa, editTextData;
    private Service service = new Service();
    private ArrayList<Ikastetxeak> ikastetxeakList = new ArrayList<>();
    private ArrayList<Users> erabiltzaileakGuztiak = new ArrayList<>();
    private ArrayList<Users> irakasleak = new ArrayList<>();
    private ArrayList<Users> ikasleak = new ArrayList<>();

    private Users selectedProfesor;
    private Users selectedAlumno;
    private Ikastetxeak selectedCentro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sortu_bilerak, container, false);
        Button buttonSortuBilerak = view.findViewById(R.id.buttonGordeBileraSortu);
        Button buttonAtzera = view.findViewById(R.id.buttonAtzeraBileraSortu);
        spinnerZentroa = view.findViewById(R.id.spinnerZentroa);
        spinnerNorekin = view.findViewById(R.id.spinnerNorekin);
        editTextGela = view.findViewById(R.id.editTextGela);
        editTextTitulua = view.findViewById(R.id.editTextTitulua);
        editTextAsuntoa = view.findViewById(R.id.editTextAsuntoa);
        editTextData = view.findViewById(R.id.editTextData);

        cargarUsuarios();
        cargarIkastetxeak();

        buttonSortuBilerak.setOnClickListener(v -> {

        });

        buttonAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new BilerakFragment())
                    .addToBackStack(null).commit();
        });

        return view;
    }
    private void lortuBilera(){

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

    private void cargarUsuarios() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Obtener los usuarios desde el servidor
                erabiltzaileakGuztiak = service.handleGetUsers();

                if (erabiltzaileakGuztiak != null && !erabiltzaileakGuztiak.isEmpty()) {
                    ArrayList<String> usuariosNombres = new ArrayList<>();
                    int tipoUsuarioLogueado = Gen.getLoggedUser().getTipos();

                    for (Users user : erabiltzaileakGuztiak) {
                        if (tipoUsuarioLogueado == 4) {
                            if (user.getTipos() == 3) {
                                irakasleak.add(user);
                                usuariosNombres.add(user.getNombre());
                            }
                        } else if (tipoUsuarioLogueado == 3) {
                            if (user.getTipos() == 4) {
                                ikasleak.add(user);
                                usuariosNombres.add(user.getNombre());
                            }
                        }
                    }

                    if (!usuariosNombres.isEmpty()) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_spinner_item, usuariosNombres);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerNorekin.setAdapter(adapter);
                            });
                        }
                    } else {
                        Log.e("SortuBilerakFragment", "No hay usuarios para mostrar.");
                    }
                } else {
                    Log.e("SortuBilerakFragment", "Lista de usuarios vacía o nula");
                }
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Error obteniendo usuarios", e);
            }
        });
    }
}
