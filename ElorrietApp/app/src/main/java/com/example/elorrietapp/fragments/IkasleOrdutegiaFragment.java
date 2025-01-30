package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Horarios;

import java.util.List;

public class IkasleOrdutegiaFragment extends Fragment {

    private TableLayout tableLayoutHorarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_ikasle_ordutegia, container, false);

        tableLayoutHorarios = view.findViewById(R.id.tableLayoutHorarios);

        obtenerHorariosAsync(3);

        return view;
    }

    private void obtenerHorariosAsync(int ikasleId) {
        new ObtenerHorariosTask().execute(ikasleId);
    }

    // Clase AsyncTask para obtener horarios en segundo plano
    private class ObtenerHorariosTask extends AsyncTask<Integer, Void, List<Horarios>> {

        @Override
        protected List<Horarios> doInBackground(Integer... params) {
            try {
                // Aquí realizas la llamada al servicio para obtener los horarios
                Service myService = new Service();
                return myService.handleGetHorariosByIkasle(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Horarios> horariosResult) {
            super.onPostExecute(horariosResult);

            if (horariosResult != null) {
                tableLayoutHorarios.removeAllViews();  // Limpiar la tabla antes de llenarla

                // Agregar encabezado de la tabla
                TableRow headerRow = new TableRow(getContext());
                TextView horaHeader = new TextView(getContext());
                horaHeader.setText("Hora");
                headerRow.addView(horaHeader);

                TextView diaHeader = new TextView(getContext());
                diaHeader.setText("Día");
                headerRow.addView(diaHeader);

                tableLayoutHorarios.addView(headerRow);

                // Llenar la tabla con los horarios
                for (Horarios horario : horariosResult) {
                    TableRow row = new TableRow(getContext());

                    TextView hora = new TextView(getContext());
                    hora.setText(String.valueOf(horario.getId().getHora()));
                    row.addView(hora);

                    TextView dia = new TextView(getContext());
                    dia.setText(horario.getId().getDia());
                    row.addView(dia);

                    tableLayoutHorarios.addView(row);
                }
            } else {
                Toast.makeText(getContext(), "Error al obtener los horarios", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
