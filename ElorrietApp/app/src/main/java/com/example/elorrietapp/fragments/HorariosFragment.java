package com.example.elorrietapp.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Horarios;
import java.util.ArrayList;
import java.util.List;

public class HorariosFragment extends Fragment {

    private List<Horarios> horarios;
    private RecyclerView recyclerView;
    private HorariosAdapter horariosAdapter;
    private View loadingIndicator; // Indicador de carga (opcional)

    public HorariosFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordutegia, container, false);

        horarios = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view_horarios); // ID correcto para el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        horariosAdapter = new HorariosAdapter(horarios);
        recyclerView.setAdapter(horariosAdapter);


        // Llamamos a obtenerHorariosAsync para obtener los horarios en un hilo separado
        obtenerHorariosAsync(4); // Aquí puedes pasar el ID que necesites

        return view;
    }

    private void obtenerHorariosAsync(int id) {
        new ObtenerHorariosTask().execute(id);
    }

    // Clase AsyncTask para obtener horarios en segundo plano
    private class ObtenerHorariosTask extends AsyncTask<Integer, Void, List<Horarios>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostrar el indicador de carga antes de comenzar la tarea
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Horarios> doInBackground(Integer... params) {
            try {
                // Crear instancia del servicio y llamar a getHorariosProfeByid
                Service myService = new Service();
                return myService.getHorariosProfeByid(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Si ocurre algún error, devolvemos null
            }
        }

        @Override
        protected void onPostExecute(List<Horarios> horariosResult) {
            super.onPostExecute(horariosResult);

            // Ocultar el indicador de carga después de completar la tarea
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(View.GONE);
            }

            if (horariosResult != null) {
                horarios.clear();
                horarios.addAll(horariosResult);
                horariosAdapter.notifyDataSetChanged(); // Notificar al adapter que los datos han cambiado
            } else {
                Toast.makeText(getContext(), "Error al obtener los horarios", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
