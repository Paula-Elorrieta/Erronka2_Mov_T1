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

        //btnIkasleDatuak.setVisibility();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // recibir la informacion enviada con un bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Users user = (Users) bundle.getSerializable("loggedUser");
            Toast.makeText(getContext(), "Ongi etorri " + user.getNombre() + " Zure tipo: " + user.getTipos() , Toast.LENGTH_SHORT).show();
            if (user.getTipos() == 4) {
                /*
                btnBilerak.setVisibility(View.INVISIBLE);
                btnOrdutegiPropioa.setVisibility(View.VISIBLE);
                btnOrdutegiIkasle.setVisibility(View.VISIBLE);
                btnIkasleDatuak.setVisibility(View.VISIBLE);*/

            } else if (user.getTipos() == 3) {
                /*
                btnBilerak.setVisibility(View.VISIBLE);
                btnOrdutegiPropioa.setVisibility(View.VISIBLE);
                btnOrdutegiIkasle.setVisibility(View.VISIBLE);
                btnIkasleDatuak.setVisibility(View.VISIBLE);*/
            } else {

            }

        } else {
            Toast.makeText(getContext(), "Ez da ondo berreskuratu erabiltzailea", Toast.LENGTH_SHORT).show();
            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        }

    }
}