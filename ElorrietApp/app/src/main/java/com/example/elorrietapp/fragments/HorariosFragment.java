package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorariosFragment extends Fragment {

    private TableLayout tableLayoutHorarios;

    public HorariosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordutegia, container, false);

        tableLayoutHorarios = view.findViewById(R.id.tableLayoutHorarios);

        obtenerHorariosAsync(4);

        return view;
    }

    private void obtenerHorariosAsync(int id) {
        new ObtenerHorariosTask().execute(id);
    }

    // Clase AsyncTask para obtener horarios en segundo plano
    private class ObtenerHorariosTask extends AsyncTask<Integer, Void, List<Horarios>> {
        @Override
        protected List<Horarios> doInBackground(Integer... params) {
            try {
                Service myService = new Service();
                return myService.getHorariosProfeByid(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Horarios> horariosResult) {
            super.onPostExecute(horariosResult);

            if (horariosResult != null) {
                tableLayoutHorarios.removeAllViews(); // Limpiar tabla

                // Agregar fila de encabezado
                TableRow headerRow = new TableRow(getContext());

                // Colocar encabezados "Hora", "Día"...
                TextView textViewHoraHeader = new TextView(getContext());
                textViewHoraHeader.setText("Hora");
                headerRow.addView(textViewHoraHeader);

                String[] dias = {"L/A", "M/A", "X", "J/O", "V/O"};
                for (String dia : dias) {
                    TextView textViewDiaHeader = new TextView(getContext());
                    textViewDiaHeader.setText(dia);
                    headerRow.addView(textViewDiaHeader);
                }

                tableLayoutHorarios.addView(headerRow);

                // Crear un mapa de los días completos a números
                Map<String, Integer> diasMap = new HashMap<>();
                diasMap.put("L/A", 1);    // Lunes
                diasMap.put("M/A", 2);   // Martes
                diasMap.put("X", 3);     // Miércoles
                diasMap.put("J/O", 4);   // Jueves
                diasMap.put("V/O", 5);   // Viernes

                // Recorrer las horas del día (1 a 5)
                for (int i = 1; i <= 5; i++) {
                    TableRow row = new TableRow(getContext());

                    // Mostrar la hora
                    TextView textViewHora = new TextView(getContext());
                    textViewHora.setText(String.valueOf(i));  // Muestra la hora
                    row.addView(textViewHora);

                    // Inicializar las celdas vacías para los días
                    String[] rowData = new String[5];
                    Arrays.fill(rowData, "");

                    // Recorrer los horarios para asignar los módulos a las celdas correspondientes
                    for (Horarios horario : horariosResult) {
                        String dia = horario.getId().getDia().trim();
                        if (diasMap.containsKey(dia) && Integer.parseInt(horario.getId().getHora()) == i) {
                            int columnIndex = diasMap.get(dia) - 1;  // Calcular el índice de la columna (0-4)
                            rowData[columnIndex] = horario.getModulos().getNombre();
                        }
                    }

                    // Agregar los módulos a las celdas correspondientes
                    for (String modulo : rowData) {
                        TextView textViewModulo = new TextView(getContext());
                        textViewModulo.setText(modulo);
                        row.addView(textViewModulo);
                    }

                    // Agregar la fila a la tabla
                    tableLayoutHorarios.addView(row);
                }

            } else {
                Toast.makeText(getContext(), "Error al obtener los horarios", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
