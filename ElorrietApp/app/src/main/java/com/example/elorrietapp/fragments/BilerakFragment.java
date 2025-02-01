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
import android.widget.Button;

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
        Button buttonSortuBilerak = view.findViewById(R.id.buttonSortuBilerak);
        BilerakZerrendak.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BilerakAdapter(bilerak, reunion -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("reunion", reunion);
            bundle.putSerializable("bilerak", bilerak);
            BilerakDetailsFragment bilerakDetailsFragment = new BilerakDetailsFragment();
            bilerakDetailsFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, bilerakDetailsFragment)
                    .addToBackStack(null).commit();


        });
        BilerakZerrendak.setAdapter(adapter);

        Users userLog = Gen.getLoggedUser();
        bilerakKargatu(userLog);

        buttonSortuBilerak.setOnClickListener(v -> {
            SortuBilerakFragment sortuBilerakFragment = new SortuBilerakFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, sortuBilerakFragment)
                    .addToBackStack(null).commit();
        });

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
                    adapter.setReuniones(bilerak);
                    adapter.notifyDataSetChanged();
                });


            } catch (Exception e) {
                Log.e("Bilerak", "Error cargando reuniones", e);
            }
        });
    }
}
