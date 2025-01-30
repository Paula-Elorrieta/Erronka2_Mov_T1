package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BilerakFragment extends Fragment {

    private ArrayList<Reuniones> bilerak = new ArrayList<>();
    private BilerakAdapter adapter;
    private Service service = new Service();
    private RecyclerView BilerakZerrendak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bilerak, container, false);
        requireActivity().setTitle(R.string.bilerak);
        BilerakZerrendak = view.findViewById(R.id.BilerakZerrendak);
        BilerakZerrendak.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BilerakAdapter(bilerak);
        BilerakZerrendak.setAdapter(adapter);

        Users userLog = Gen.getLoggedUser();
        bilerakKargatu(userLog);

        return view;
    }

    private void bilerakKargatu(Users userLog) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                if (userLog.getTipos() == 4) {
                    bilerak = service.getBilerakIkasleak(userLog.getId());
                } else {
                    bilerak = service.handleGetBilera(userLog.getId());
                }
                getActivity().runOnUiThread(() -> {
                    bilerak.addAll(bilerak);
                });

            } catch (Exception e) {
                Log.e("Bilerak", "Error cargando reuniones", e);
            }
        });
    }

    private void IrakasleBilerak(int userId) {
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
                }
            });
        });
    }




}
