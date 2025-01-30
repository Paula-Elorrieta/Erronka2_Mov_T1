package com.example.elorrietapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Horarios;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableAdapter {
    private Context context;
    private TableLayout tableLayout;
    private List<Horarios> horariosList;

    public TableAdapter(Context context, TableLayout tableLayout, List<Horarios> horariosList) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.horariosList = horariosList;
    }

    public void actualizarTabla() {
        tableLayout.removeAllViews(); // Limpiar la tabla antes de agregar nuevas filas

        // Encabezado de la tabla
        TableRow headerRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.item_tabla, tableLayout, false);
        ((TextView) headerRow.findViewById(R.id.textViewHora)).setText("Hora");
        ((TextView) headerRow.findViewById(R.id.textViewDia1)).setText("L/A");
        ((TextView) headerRow.findViewById(R.id.textViewDia2)).setText("M/A");
        ((TextView) headerRow.findViewById(R.id.textViewDia3)).setText("X");
        ((TextView) headerRow.findViewById(R.id.textViewDia4)).setText("J/O");
        ((TextView) headerRow.findViewById(R.id.textViewDia5)).setText("V/O");

        tableLayout.addView(headerRow);

        // Mapeo de días
        Map<String, Integer> diasMap = new HashMap<>();
        diasMap.put("L/A", 1);
        diasMap.put("M/A", 2);
        diasMap.put("X", 3);
        diasMap.put("J/O", 4);
        diasMap.put("V/O", 5);

        // Agregar filas de datos
        for (int i = 1; i <= 5; i++) {
            TableRow row = (TableRow) LayoutInflater.from(context).inflate(R.layout.item_tabla, tableLayout, false);

            ((TextView) row.findViewById(R.id.textViewHora)).setText(String.valueOf(i));

            // Inicializar celdas vacías
            String[] rowData = new String[5];
            Arrays.fill(rowData, "");

            for (Horarios horario : horariosList) {
                String dia = horario.getId().getDia().trim();
                if (diasMap.containsKey(dia) && Integer.parseInt(horario.getId().getHora()) == i) {
                    int columnIndex = diasMap.get(dia) - 1;  // Índice (0-4)
                    rowData[columnIndex] = horario.getModulos().getNombre();
                }
            }

            // Rellenar la fila con los datos
            ((TextView) row.findViewById(R.id.textViewDia1)).setText(rowData[0]);
            ((TextView) row.findViewById(R.id.textViewDia2)).setText(rowData[1]);
            ((TextView) row.findViewById(R.id.textViewDia3)).setText(rowData[2]);
            ((TextView) row.findViewById(R.id.textViewDia4)).setText(rowData[3]);
            ((TextView) row.findViewById(R.id.textViewDia5)).setText(rowData[4]);

            tableLayout.addView(row);
        }
    }
}
