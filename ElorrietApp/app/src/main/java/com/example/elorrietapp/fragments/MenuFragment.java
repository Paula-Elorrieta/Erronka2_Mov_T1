package com.example.elorrietapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.elorrietapp.R;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Users;

public class MenuFragment extends Fragment {

    Button btnBilerak;
    Button btnOrdutegiPropioa;
    Button btnIrakasleOrdutegiak;
    Button btnZerrendak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        requireActivity().invalidateOptionsMenu();
        requireActivity().setTitle("Menu");

        btnBilerak = view.findViewById(R.id.btnBilerak);
        btnOrdutegiPropioa = view.findViewById(R.id.btnNire_ordutegia);
        btnIrakasleOrdutegiak = view.findViewById(R.id.btnIrakasle_ordutegiak);
        btnIrakasleOrdutegiak.setVisibility(View.INVISIBLE);
        btnZerrendak = view.findViewById(R.id.btnZerrendak);

        Gen gen = new Gen();
        Users userlog = gen.getLoggedUser();

        if (userlog.getTipos() == 3) {
            btnZerrendak.setText(R.string.ikasleen_datuak);
            btnZerrendak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new IkasleZerrendaFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            btnZerrendak.setText(R.string.irakasleen_datuak);
            btnIrakasleOrdutegiak.setVisibility(View.VISIBLE);
            btnZerrendak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new IrakasleZerrendaFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });

//            btnIrakasleOrdutegiak.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragmentContainerView, new IrakasleOrdutegiakFragment())
//                            .addToBackStack(null)
//                            .commit();
//                }
//            });
        }

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

        return view;
    }
}
