package com.example.elorrietapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Users;

public class MenuFragment extends Fragment {
    Button btnBilerak;
    Button btnOrdutegiPropioa;
    Button btnOrdutegiIkasle;
    Button btnIkasleDatuak;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        btnBilerak = view.findViewById(R.id.btnBilerak);
        btnOrdutegiPropioa = view.findViewById(R.id.btnNire_ordutegia);
        btnOrdutegiIkasle = view.findViewById(R.id.btnIkasleen_ordutegiak);
        btnIkasleDatuak = view.findViewById(R.id.btnIkasleen_datuak);



        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // recibir la informacion enviada con un bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // recibir la informacion enviada con un bundle
            Users user = (Users) bundle.getSerializable("loggedUser");
            Toast.makeText(getContext(), "Ongi etorri " + user.getNombre(), Toast.LENGTH_SHORT).show();
        } else {
            // si no se recibe nada, se redirige al login
            Toast.makeText(getContext(), "Ez da ondo berreskuratu erabiltzailea", Toast.LENGTH_SHORT).show();
            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        }

    }
}