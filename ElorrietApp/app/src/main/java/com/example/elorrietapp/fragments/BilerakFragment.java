package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Reuniones;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BilerakFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bilerak, container, false);

        if (Gen.getLoggedUser().getTipos() == 4) {
            fetchReuniones(Gen.getLoggedUser().getId());
        }

        return view;
    }

    private void fetchReuniones(int userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Service service = new Service();
            List<Reuniones> reuniones = service.getBilerakIkasleak(userId);

            handler.post(() -> {
                if (reuniones != null) {
                    for (Reuniones r : reuniones) {
                        Log.i("BilerakFragment", "Reunión: " + r.getAsunto() + " - " + r.getUsersByProfesorId());
                    }
                } else {
                    Log.e("BilerakFragment", "No se pudieron obtener las reuniones.");
                }
            });
        });
    }
}
