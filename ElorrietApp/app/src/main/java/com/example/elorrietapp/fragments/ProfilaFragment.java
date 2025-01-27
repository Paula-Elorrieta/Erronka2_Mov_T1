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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.elorrietapp.R;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// Fragment barruan bilatu ditut zenbait funtzioak deprekatuak ez diren, kodea optimizatzeko eta
// funtzionamendua hobetzeko.

public class ProfilaFragment extends Fragment {
    private ImageView imgAurreikuspena;
    private Uri argazkiUri;
    private Button btnArgazkiaAtera;
    private TextView textIzena;
    private TextView textTaldea;

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
        textIzena = view.findViewById(R.id.textIzena);
        textTaldea = view.findViewById(R.id.textTaldea);
        btnArgazkiaAtera = view.findViewById(R.id.btnArgazkiaAtera);
        imgAurreikuspena = view.findViewById(R.id.imgAurreikuspena);
        Button btnAtzera = view.findViewById(R.id.btnAtzera);

        // Textuak jarri
        Gen gen = new Gen();
        Users user = gen.getLoggedUser();

        textIzena.setText("Kaixo " + user.getNombre());

        String taldea = "";
        if (user.getTipos() == 3) {
            taldea = "Ikaslea";
        } else {
            taldea = "Irakaslea";
        }
        textTaldea.setText(taldea + " zara");

        //Img jarri
        //imgAurreikuspena.setImageURI(byteToUri(user.getArgazkia()));

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

    private Uri byteToUri(byte[] argazkia) {
        File file = new File(getContext().getCacheDir(), "argazkia.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(argazkia);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Va a dar unos problemas de la ostia
        return FileProvider.getUriForFile(getContext(), "com.example.elorrietapp.fileprovider", file);
    }
}