package com.example.elorrietapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import java.io.InputStream;

// Fragment barruan bilatu ditut zenbait funtzioak deprekatuak ez diren, kodea optimizatzeko eta
// funtzionamendua hobetzeko.

public class ProfilaFragment extends Fragment {
    private ImageView imgAurreikuspena;
    private Uri argazkiUri;
    private Button btnArgazkiaAtera;
    private TextView textIzena;
    private TextView textTaldea;

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

        // Verifica permisos
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Solicita permisos si no están concedidos
            permissionLauncher.launch(Manifest.permission.CAMERA);
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        textIzena = view.findViewById(R.id.textIzena);
        textTaldea = view.findViewById(R.id.textTaldea);
        btnArgazkiaAtera = view.findViewById(R.id.btnArgazkiaAtera);
        imgAurreikuspena = view.findViewById(R.id.imgAurreikuspena);
        Button btnAtzera = view.findViewById(R.id.btnAtzera);

        // Obtén datos del usuario
        Gen gen = new Gen();
        Users user = gen.getLoggedUser();

        // Configura textos
        textIzena.setText("Kaixo " + user.getNombre());

        String taldea = (user.getTipos() == 3) ? "Ikaslea" : "Irakaslea";
        textTaldea.setText(taldea + " zara");

        // Carga imagen del usuario o una predeterminada
        if (user.getArgazkia() != null && user.getArgazkia().length > 0) {
            imgAurreikuspena.setImageURI(byteToUri(user.getArgazkia()));
        } else {
            imgAurreikuspena.setImageResource(R.drawable.default_user);
        }

        btnAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnArgazkiaAtera.setOnClickListener(v -> kameraZabaldu());

        return view;
    }

    private void kameraZabaldu() {
        // Crea un archivo para guardar la imagen
        File fotoArchivo = new File(getActivity().getExternalFilesDir(null), "foto_" + System.currentTimeMillis() + ".jpg");

        // Crea un Uri para el archivo
        argazkiUri = FileProvider.getUriForFile(getContext(),
                "com.example.elorrietapp.fileprovider", fotoArchivo);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, argazkiUri);

        kameraLauncher.launch(intent); // Lanza el intent para capturar la imagen
    }

    // Modifica el ActivityResultLauncher para devolver la imagen y asignarla al usuario
    private final ActivityResultLauncher<Intent> kameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    // La imagen fue capturada correctamente
                    imgAurreikuspena.setImageURI(argazkiUri);

                    // Asigna la imagen capturada al usuario
                    try {
                        byte[] imagenBytes = uriToBytes(argazkiUri); // Convierte la URI a byte[]
                        Gen gen = new Gen();
                        Users user = gen.getLoggedUser();

                        user.setArgazkia(imagenBytes); // Guarda la imagen en el usuario

                        // TODO: llamar al servicio para guardar la imagen

                        Toast.makeText(getContext(), "Argazkia gorde da erabiltzailearentzat", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Errorea irudia gordetzean", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si no se tomó ninguna imagen
                    Toast.makeText(getContext(), "Ez da egin argazkirik", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private Uri byteToUri(byte[] argazkia) {
        File file = new File(getContext().getCacheDir(), "argazkia.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(argazkia);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(getContext(), "com.example.elorrietapp.fileprovider", file);
    }

    // Método para convertir un Uri a byte[]
    private byte[] uriToBytes(Uri uri) throws IOException {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}