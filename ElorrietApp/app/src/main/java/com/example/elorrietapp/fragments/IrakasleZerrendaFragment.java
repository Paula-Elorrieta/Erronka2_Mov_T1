package com.example.elorrietapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Users;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IrakasleZerrendaFragment extends Fragment {

    private Service service = new Service();
    private ArrayList<Users> irakasleak = new ArrayList<>();
    private ArrayList<Users> irakasleFiltrados = new ArrayList<>();
    private ZerrendaAdapter adapter;
    private RecyclerView zerrendaRecyclerView;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ikasle_zerrenda, container, false);
        requireActivity().setTitle(R.string.irakasle_datuak);

        zerrendaRecyclerView = view.findViewById(R.id.Zerrenda);
        zerrendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        adapter = new ZerrendaAdapter(irakasleFiltrados);
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
                        service.handleGetIrakasleakByIkasleak(userLog.getId());

                getActivity().runOnUiThread(() -> {
                    irakasleak.clear();
                    irakasleak.addAll(nuevaLista);
                    filtrarUsuarios("");
                });

            } catch (Exception e) {
                Log.e("IkasleZerrendaFragment", "Error cargando usuarios", e);
            }
        });
    }

    private void filtrarUsuarios(String texto) {
        irakasleFiltrados.clear();
        if (texto.isEmpty()) {
            irakasleFiltrados.addAll(irakasleak);
        } else {
            for (Users user : irakasleak) {
                if (user.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    irakasleFiltrados.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}