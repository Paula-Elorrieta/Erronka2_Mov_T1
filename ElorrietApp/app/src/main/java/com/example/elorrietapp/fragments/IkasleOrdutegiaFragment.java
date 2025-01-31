package com.example.elorrietapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.AsyncTask;
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
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.Modulos;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class IkasleOrdutegiaFragment extends Fragment {

    private TableLayout tableLayoutHorarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_ikasle_ordutegia, container, false);

        tableLayoutHorarios = view.findViewById(R.id.tableLayoutHorarios);

        obtenerHorariosAsync(Gen.getLoggedUser().getId());

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
                // Crear un adaptador de tabla con los horarios obtenidos
                TableAdapter tableAdapter = new TableAdapter(getContext(), tableLayoutHorarios, horariosResult);
                tableAdapter.actualizarTabla();
            } else {
                Toast.makeText(getContext(), "Ezin izan dira ikaslearen ordutegiak kargatu", Toast.LENGTH_SHORT).show();
            }

        }
    }
}