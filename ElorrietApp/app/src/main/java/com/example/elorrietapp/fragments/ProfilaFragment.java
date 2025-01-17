package com.example.elorrietapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.elorrietapp.R;

import java.io.File;

// Fragment barruan bilatu ditut zenbait funtzioak deprekatuak ez diren, kodea optimizatzeko eta
// funtzionamendua hobetzeko.

public class ProfilaFragment extends Fragment {
    private ImageView imgAurreikuspena;
    private Uri argazkiUri;

    // Argazkia ateratzeko ActivityResultLauncher
    private final ActivityResultLauncher<Intent> kameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    // ImageView elementuan erakutsi argazkia
                    imgAurreikuspena.setImageURI(argazkiUri);
                } else {
                    Toast.makeText(getContext(), "Ez da egin argazkirik", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Baimenak eskatzeko ActivityResultLauncher (kamera eta memoria)
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Toast.makeText(getContext(), "Baimenak gaitutak", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profila, container, false);

        // Egiaztatu baimenak eskuratu direla
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Ez badaude baimenik, eskatu baimenak
            permissionLauncher.launch(Manifest.permission.CAMERA);
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        Button btnArgazkiaAtera = view.findViewById(R.id.btnArgazkiaAtera);
        imgAurreikuspena = view.findViewById(R.id.imgAurreikuspena);
        Button btnAtzera = view.findViewById(R.id.btnAtzera);

        btnAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnArgazkiaAtera.setOnClickListener(v -> kameraZabaldu());

        return view;
    }

    private void kameraZabaldu() {
        // Sortu fitxategi bat argazkiak gordetzeko
        File fotoArchivo = new File(getActivity().getExternalFilesDir(null), "foto_" + System.currentTimeMillis() + ".jpg");

        // Sortu Uri bat fitxategiari buruz
        argazkiUri = FileProvider.getUriForFile(getContext(),
                "com.example.komunikazioaksarbideak_kamera.fileprovider", fotoArchivo);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, argazkiUri);

        kameraLauncher.launch(intent);
    }
}