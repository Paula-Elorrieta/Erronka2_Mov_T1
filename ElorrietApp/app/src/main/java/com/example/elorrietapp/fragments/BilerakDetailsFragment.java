package com.example.elorrietapp.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BilerakDetailsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private ArrayList<Ikastetxeak> ikastetxeakList = new ArrayList<>();
    private Service service = new Service();
    private MapView mapaIkuspegia;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bilerak_details, container, false);
        requireActivity().setTitle(R.string.bilerakDetails);

        // Configurar OSMDroid
        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        // Inicializar MapView
        mapaIkuspegia = view.findViewById(R.id.mapa);
        mapaIkuspegia.setTileSource(TileSourceFactory.MAPNIK);
        mapaIkuspegia.setBuiltInZoomControls(true);
        mapaIkuspegia.setMultiTouchControls(true);
        mapaIkuspegia.getController().setZoom(15.0); // Zoom inicial

        // Inicializar ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Obtener la reunión desde el Bundle
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

        // Asignar valores a los TextView
        TextView textViewEgoera = view.findViewById(R.id.textViewEgoera);
        FrameLayout frameEgora = view.findViewById(R.id.FrameEgora);
        TextView textViewGaia = view.findViewById(R.id.textViewGaia);
        TextView textViewData = view.findViewById(R.id.textViewData);
        TextView textViewTituloa = view.findViewById(R.id.textViewTituloa);
        TextView textViewNorekin = view.findViewById(R.id.textViewNorekin);
        TextView textViewNon = view.findViewById(R.id.textViewNon);

        koloreaEzarri(frameEgora, reunion);
        textViewEgoera.setText("Egoera: " + reunion.getEstadoEus());
        textViewGaia.setText("Gaia: " + reunion.getAsunto());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        textViewData.setText("Data: " + sdf.format(reunion.getFecha()));
        textViewTituloa.setText("Tituloa: " + reunion.getTitulo());

        if (Gen.getLoggedUser().getTipos() == 3) {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByAlumnoId().getNombre());
        } else {
            textViewNorekin.setText("Norekin: " + reunion.getUsersByProfesorId().getNombre());
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


    public interface IkastetxeakCallback {
        void onIkastetxeakLoaded(Ikastetxeak ikastetxeak);
    }
}
