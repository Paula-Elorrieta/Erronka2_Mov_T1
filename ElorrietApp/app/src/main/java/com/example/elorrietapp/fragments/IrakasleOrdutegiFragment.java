package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.Users;
import com.example.elorrietapp.db.Mysql;

import java.util.ArrayList;
import java.util.List;

public class IrakasleOrdutegiFragment extends Fragment {

    private Spinner spinnerProfesores;
    private TableLayout tableLayoutHorarios;
    private List<Users> profesoresList;
    private Button btnAtzera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irakasle_ordutegi, container, false);
        requireActivity().setTitle(R.string.ordutegiIrakasleak);

        spinnerProfesores = view.findViewById(R.id.spinnerProfesores);
        tableLayoutHorarios = view.findViewById(R.id.tableLayoutHorarios);

        new ObtenerProfesoresTask().execute();
        btnAtzera = view.findViewById(R.id.buttonAtzera);
        btnAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private class ObtenerProfesoresTask extends AsyncTask<Void, Void, ArrayList<Users>> {

        @Override
        protected ArrayList<Users> doInBackground(Void... voids) {
            return Mysql.irakasleakKargatu();
        }

        @Override
        protected void onPostExecute(ArrayList<Users> profesores) {
            if (profesores != null && !profesores.isEmpty()) {
                profesoresList = profesores;

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getProfesoresNombres(profesoresList)
                );

                spinnerProfesores.setAdapter(adapter);

                spinnerProfesores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Users profesorSeleccionado = profesoresList.get(position);
                        obtenerHorariosProfesorAsync(profesorSeleccionado.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Ez egin ezer
                    }
                });

            } else {
                Toast.makeText(getContext(), "Ez dira kargatu irakasleak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerHorariosProfesorAsync(int profesorId) {
        new ObtenerHorariosTask().execute(profesorId);
    }

    private class ObtenerHorariosTask extends AsyncTask<Integer, Void, List<Horarios>> {

        @Override
        protected List<Horarios> doInBackground(Integer... params) {
            return Service.getHorariosProfeByid(params[0]);
        }

        @Override
        protected void onPostExecute(List<Horarios> horariosResult) {
            if (horariosResult != null && !horariosResult.isEmpty()) {
                TableAdapter tableAdapter = new TableAdapter(getContext(), tableLayoutHorarios, horariosResult);
                tableAdapter.actualizarTabla();
            } else {
                Toast.makeText(getContext(), "Irakasle hori ez dauka ordutegirik", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getProfesoresNombres(List<Users> profesores) {
        String[] nombres = new String[profesores.size()];
        for (int i = 0; i < profesores.size(); i++) {
            nombres[i] = profesores.get(i).getNombre();
        }
        return nombres;
    }


}
