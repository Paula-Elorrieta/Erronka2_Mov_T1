package com.example.elorrietapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Matriculaciones;
import com.example.elorrietapp.modelo.Users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfilaFragment extends Fragment {
    private ImageView imgAurreikuspena;
    private TextView TextVusername, textVnombreApellidos, textVdni, textVtelefono1, textVCurso;
    private Service service;
    private Users user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profila, container, false);
        requireActivity().setTitle("Profila");

        imgAurreikuspena = view.findViewById(R.id.imgAurreikuspena);
        Button btnAtzera = view.findViewById(R.id.btnAtzera);
        TextVusername = view.findViewById(R.id.TextVusername);
        textVnombreApellidos = view.findViewById(R.id.textVnombreApellidos);
        textVdni = view.findViewById(R.id.textVdni);
        textVtelefono1 = view.findViewById(R.id.textVtelefono1);
        textVCurso = view.findViewById(R.id.textVCurso);

        service = new Service();
        user = new Gen().getLoggedUser();

        TextVusername.setText(user.getUsername());
        textVnombreApellidos.setText(user.getNombre() + " " + user.getApellidos());
        textVdni.setText(user.getDni());

        if (user.getTelefono1() != null) {
            textVtelefono1.setText(user.getTelefono1() + "");
        } else {
            textVtelefono1.setText("Ez dago telefonoa");
        }


        if (user.getTipos() == 3) {
            textVCurso.setVisibility(View.INVISIBLE);
        } else {
            cargarMatriculaciones();
        }

        if (user.getArgazkia() != null && user.getArgazkia().length > 0) {
            imgAurreikuspena.setImageURI(byteToUri(user.getArgazkia()));
        } else {
            imgAurreikuspena.setImageResource(R.drawable.default_user);
        }

        btnAtzera.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        return view;
    }

    private void cargarMatriculaciones() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                List<Matriculaciones> matriculaciones = service.getMatriculacionesByUser(user.getId());
                requireActivity().runOnUiThread(() -> {
                    if (!matriculaciones.isEmpty()) {
                        textVCurso.setText(matriculaciones.get(0).getCiclos().getNombre()
                                + " - " + matriculaciones.get(0).getId().getCurso());
                    }
                });
            } catch (Exception e) {
                Log.e("ProfilaFragment", "Error cargando matriculaciones", e);
            }
        });
    }

    private Uri byteToUri(byte[] argazkia) {
        File file = new File(getContext().getCacheDir(), "argazkia.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(argazkia);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(getContext(), "com.example.elorrietapp.fileprovider", file);
    }
}
