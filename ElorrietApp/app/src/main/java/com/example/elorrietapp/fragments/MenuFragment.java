package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.modelo.Users;

public class MenuFragment extends Fragment {

    Button btnBilerak;
    Button btnOrdutegiPropioa;
    Button btnOrdutegiIkasle;
    Button btnIkasleDatuak;
    Button btnIrakasleenDatuak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        requireActivity().invalidateOptionsMenu();
        requireActivity().setTitle("Menu");

        btnBilerak = view.findViewById(R.id.btnBilerak);
        btnOrdutegiPropioa = view.findViewById(R.id.btnNire_ordutegia);
        btnOrdutegiIkasle = view.findViewById(R.id.btnIkasleen_ordutegiak);
        btnIkasleDatuak = view.findViewById(R.id.btnIkasleen_datuak);
        btnIrakasleenDatuak = view.findViewById(R.id.btnIrakasleen_datuak);

        btnOrdutegiPropioa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new HorariosFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnBilerak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new BilerakFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnIkasleDatuak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new IkasleZerrendaFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnIrakasleenDatuak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new IrakasleZerrendaFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Users user = (Users) bundle.getSerializable("loggedUser");
            Toast.makeText(getContext(), "Ongi etorri " + user.getNombre() + " Zure tipo: " + user.getTipos(), Toast.LENGTH_SHORT).show();
//            if (user.getTipos() == 4) {
            btnBilerak.setVisibility(View.VISIBLE);
            btnOrdutegiPropioa.setVisibility(View.VISIBLE);
            btnOrdutegiIkasle.setVisibility(View.VISIBLE);
            btnIkasleDatuak.setVisibility(View.VISIBLE);
//            } else if (user.getTipos() == 3) {
//                btnBilerak.setVisibility(View.VISIBLE);
//                btnOrdutegiPropioa.setVisibility(View.VISIBLE);
//                btnOrdutegiIkasle.setVisibility(View.VISIBLE);
//                btnIkasleDatuak.setVisibility(View.VISIBLE);
//            }
        } else {
            Toast.makeText(getContext(), "Ez da ondo berreskuratu erabiltzailea", Toast.LENGTH_SHORT).show();
        }
    }
}
