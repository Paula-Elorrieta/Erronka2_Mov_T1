package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IkasleZerrendaFragment extends Fragment {

    private Service service = new Service();
    private ArrayList<Users> ikasleak = new ArrayList<>();
    private ArrayList<Users> ikasleakFiltrados = new ArrayList<>();
    private ZerrendaAdapter adapter;
    private RecyclerView zerrendaRecyclerView;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ikasle_zerrenda, container, false);
        requireActivity().setTitle(R.string.ikasle_datuak);

        zerrendaRecyclerView = view.findViewById(R.id.Zerrenda);
        zerrendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Button buttonAtzera = view.findViewById(R.id.buttonAtzera);

        buttonAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarUsuarios(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarUsuarios(newText);
                return true;
            }
        });


        adapter = new ZerrendaAdapter(ikasleakFiltrados);
        zerrendaRecyclerView.setAdapter(adapter);

        Gen gen = new Gen();
        Users userLog = gen.getLoggedUser();
        ikasleakKargatu(userLog);

        return view;
    }

    private void ikasleakKargatu(Users userLog) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ArrayList<Users> nuevaLista = (ArrayList<Users>)
                        service.handleGetIkasleakByIrakasleak(userLog.getId());

                getActivity().runOnUiThread(() -> {
                    ikasleak.clear();
                    ikasleak.addAll(nuevaLista);
                    filtrarUsuarios("");
                });

            } catch (Exception e) {
                Log.e("IkasleZerrendaFragment", "Error cargando usuarios", e);
            }
        });
    }

    private void filtrarUsuarios(String texto) {
        ikasleakFiltrados.clear();
        if (texto.isEmpty()) {
            ikasleakFiltrados.addAll(ikasleak);
        } else {
            for (Users user : ikasleak) {
                if (user.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    ikasleakFiltrados.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
