package com.example.elorrietapp.fragments;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Ikastetxeak;
import com.example.elorrietapp.modelo.Reuniones;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BilerakDetailsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private ArrayList<Ikastetxeak> ikastetxeakList = new ArrayList<>();
    private ArrayList<Reuniones> reunionesList = new ArrayList<>();
    private Service service = new Service();
    private MapView mapaIkuspegia;
    private FusedLocationProviderClient fusedLocationClient;
    private FrameLayout frameEgora;
    private TextView textViewEgoera;
    private Button btnOnartu;
    private Button btnEzeztatu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bilerak_details, container, false);
        requireActivity().setTitle(R.string.bilerakDetails);

        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        mapaIkuspegia = view.findViewById(R.id.mapa);
        mapaIkuspegia.setTileSource(TileSourceFactory.MAPNIK);
        mapaIkuspegia.setBuiltInZoomControls(true);
        mapaIkuspegia.setMultiTouchControls(true);
        mapaIkuspegia.getController().setZoom(15.0);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("reunion")) {
            Log.e("BilerakDetailsFragment", "No se encontró la reunión en el bundle.");
            return view;
        }
        Reuniones reunion = (Reuniones) bundle.getSerializable("reunion");

        if (reunion == null) {
            Log.e("BilerakDetailsFragment", "Error: La reunión es nula.");
            return view;
        }

        reunionesList = (ArrayList<Reuniones>) bundle.getSerializable("bilerak");

        textViewEgoera = view.findViewById(R.id.textViewEgoera);
        frameEgora = view.findViewById(R.id.FrameEgora);
        TextView textViewGaia = view.findViewById(R.id.textViewGaia);
        TextView textViewData = view.findViewById(R.id.textViewData);
        TextView textViewTituloa = view.findViewById(R.id.textViewTituloa);
        TextView textViewNorekin = view.findViewById(R.id.textViewNorekin);
        TextView textViewNon = view.findViewById(R.id.textViewNon);
        btnOnartu = view.findViewById(R.id.btnOnartu);
        btnEzeztatu = view.findViewById(R.id.btnEzeztatu);

        koloreaEzarri(frameEgora, reunion);
        textViewEgoera.setText("Egoera: " + reunion.getEstadoEus());
        textViewGaia.setText("Gaia: " + reunion.getAsunto());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        textViewData.setText("Data: " + sdf.format(reunion.getFecha()));
        textViewTituloa.setText("Tituloa: " + reunion.getTitulo());

        if (Gen.getLoggedUser().getTipos() == 3) {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByAlumnoId().getNombre());
            btnOnartu.setVisibility(View.VISIBLE);
            btnEzeztatu.setVisibility(View.VISIBLE);

            if (reunion.getEstadoEus().equals("onartuta")) {
                btnOnartu.setEnabled(false);
            }

            if (reunion.getEstadoEus().equals("ezeztatuta")) {
                btnEzeztatu.setEnabled(false);
            }

            btnOnartu.setOnClickListener(v -> {
                bileraOnartu(reunion);

            });

            btnEzeztatu.setOnClickListener(v -> {
                bileraEzeztatu(reunion);
            });


        } else {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByProfesorId().getNombre());
            btnEzeztatu.setVisibility(View.GONE);

            if (reunion.getEstadoEus().equals("onartzeke")) {
                btnOnartu.setVisibility(View.VISIBLE);
                btnOnartu.setOnClickListener(v -> {
                    bileraOnartu(reunion);

                });
            } else {
                btnOnartu.setVisibility(View.GONE);
            }

        }

        IkastetxeakKargatu(reunion.getIdCentro(), ikastetxeak -> {
            textViewNon.setText("Gela: " + reunion.getAula() + " eta ikastetxea: " + ikastetxeak.getNOM());
        });

        ubikazioaEskatu();

        return view;
    }

    private void ubikazioaEskatu() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                            markadoreaEzarri(userLocation, "Nire Egungo Kokapena");
                            mapaIkuspegia.getController().setCenter(userLocation);
                        } else {
                            Log.i("BilerakDetailsFragment", "No se pudo obtener la ubicación.");
                        }
                    }
                });
    }

    private void markadoreaEzarri(GeoPoint punto, String titulo) {
        Marker marcador = new Marker(mapaIkuspegia);
        marcador.setPosition(punto);
        marcador.setTitle(titulo);
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapaIkuspegia.getOverlays().add(marcador);
        mapaIkuspegia.invalidate();
    }

    private void koloreaEzarri(FrameLayout frameEgora, Reuniones reunion) {
        int color;
        switch (reunion.getEstadoEus().trim().toLowerCase()) {
            case "onartuta":
                color = ContextCompat.getColor(frameEgora.getContext(), R.color.onartuta);
                break;
            case "ezeztatuta":
                color = ContextCompat.getColor(frameEgora.getContext(), R.color.ezeztatuta);
                break;
            case "gatazka":
                color = ContextCompat.getColor(frameEgora.getContext(), R.color.gatazka);
                break;
            default:
                color = ContextCompat.getColor(frameEgora.getContext(), R.color.onartzeke);
                break;
        }
        frameEgora.setBackgroundColor(color);
    }

    private void IkastetxeakKargatu(String idCentro, IkastetxeakCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ikastetxeakList = service.handleGetAllIkastetxeak();
                for (Ikastetxeak ikastetxeak : ikastetxeakList) {
                    if (ikastetxeak.getCCEN().equals(idCentro)) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                callback.onIkastetxeakLoaded(ikastetxeak);

                                double latitud = ikastetxeak.getLATITUD();
                                double longitud = ikastetxeak.getLONGITUD();

                                if (latitud != 0 && longitud != 0) {
                                    GeoPoint centroUbicacion = new GeoPoint(longitud, latitud);

                                    mapaIkuspegia.getTileProvider().clearTileCache();

                                    markadoreaEzarri(centroUbicacion, ikastetxeak.getNOM());

                                    mapaIkuspegia.getController().setCenter(centroUbicacion);
                                    mapaIkuspegia.getController().setZoom(19.0);
                                    mapaIkuspegia.invalidate();
                                } else {
                                    Log.e("BilerakDetailsFragment",
                                            "Coordenadas inválidas para: " + ikastetxeak.getNOM());
                                }
                            });
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                Log.e("BilerakDetailsFragment", "Error obteniendo ikastetxeak", e);
            }
        });
    }

    public void bileraOnartu(Reuniones reunion) {
        for (Reuniones r : reunionesList) {
            if (r.getFecha().equals(reunion.getFecha())) {
                reunion.setEstadoEus("gatazka");
                reunion.setEstado("conflicto");
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        String mezua = service.handleUpdateReunion(reunion.getIdReunion(), reunion.getEstadoEus(), reunion.getEstado());
                        getActivity().runOnUiThread(() -> {
                            koloreaEzarri(frameEgora, reunion);
                            textViewEgoera.setText("Egoera: gatazka");
                            Toast.makeText(requireActivity(), mezua, Toast.LENGTH_SHORT).show();
                            btnOnartu.setEnabled(true);
                            btnEzeztatu.setEnabled(true);
                        });
                    } catch (Exception e) {
                        Log.e("BilerakDetailsFragment", "Error actualizando reunión", e);
                    }
                });
                break;
            } else {
                reunion.setEstadoEus("onartuta");
                reunion.setEstado("aceptada");
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        String mezua = service.handleUpdateReunion(reunion.getIdReunion(), reunion.getEstadoEus(), reunion.getEstado());
                        getActivity().runOnUiThread(() -> {
                            koloreaEzarri(frameEgora, reunion);
                            textViewEgoera.setText("Egoera: onartuta");
                            Toast.makeText(requireActivity(), mezua, Toast.LENGTH_SHORT).show();
                            btnOnartu.setEnabled(false);
                            btnEzeztatu.setEnabled(true);
                        });
                    } catch (Exception e) {
                        Log.e("BilerakDetailsFragment", "Error actualizando reunión", e);
                    }
                });
                break;
            }
        }
    }

    public void bileraEzeztatu(Reuniones reunion) {
        reunion.setEstadoEus("ezeztatuta");
        reunion.setEstado("denegada");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String mezua = service.handleUpdateReunion(reunion.getIdReunion(), reunion.getEstadoEus(), reunion.getEstado());
                getActivity().runOnUiThread(() -> {
                    koloreaEzarri(frameEgora, reunion);
                    textViewEgoera.setText("Egoera: ezeztatuta");
                    Toast.makeText(requireActivity(), mezua, Toast.LENGTH_SHORT).show();
                    btnEzeztatu.setEnabled(false);
                    btnOnartu.setEnabled(true);
                });
            } catch (Exception e) {
                Log.e("BilerakDetailsFragment", "Error actualizando reunión", e);
            }
        });
    }

    public interface IkastetxeakCallback {
        void onIkastetxeakLoaded(Ikastetxeak ikastetxeak);
    }
}
