package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.Users;

import java.util.List;

public class HorariosFragment extends Fragment {

    private TableLayout tableLayoutHorarios;
    private TableAdapter tableAdapter;

    public HorariosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordutegia, container, false);
        requireActivity().setTitle(R.string.ordutegia);
        tableLayoutHorarios = view.findViewById(R.id.tableLayoutHorarios);
        Users loggedUser = Gen.getLoggedUser();
        Button buttonAtzera = view.findViewById(R.id.buttonAtzera);
        obtenerHorariosAsync(loggedUser.getId());

        buttonAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void obtenerHorariosAsync(int id) {
        new ObtenerHorariosTask().execute(id);
    }

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
                tableAdapter = new TableAdapter(getContext(), tableLayoutHorarios, horariosResult);
                tableAdapter.actualizarTabla();
            } else {
                Toast.makeText(getContext(), "Errorea ordutegiak lortzen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
