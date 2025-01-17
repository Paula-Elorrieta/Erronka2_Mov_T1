package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Users;


public class LoginFragment extends Fragment {

    TextView textErabiltzailea;
    TextView textPasahitza;
    private Users loggedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView textErabiltzailea = view.findViewById(R.id.textErabiltzailea);
        TextView textPasahitza = view.findViewById(R.id.textPasahitza);
        ImageView logo = view.findViewById(R.id.logo);


        // Animazioa kargatu
        Glide.with(this)
                .asGif()
                .load(R.drawable.eit)
                .into(logo);

        // Login balidazioa
        textPasahitza.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Llama a la función de validación
                validarLogin(textErabiltzailea, textPasahitza);
                return true;
            }
            return false;
        });

        return view;
    }

    // Función de validación de login
    private void validarLogin(TextView textErabiltzailea, TextView textPasahitza) {

        Toast.makeText(getContext(), textErabiltzailea.getText().toString() + " " + textPasahitza.getText().toString(), Toast.LENGTH_SHORT).show();
        new LoginTask().execute(textErabiltzailea.getText().toString(), textPasahitza.getText().toString());
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        // String... luzeera aldagaia, nahi denean zenbaki baten bat edo gehiago jaso ditzakeen metodoak
        @Override
        protected Boolean doInBackground(String... params) {
            String user = params[0];
            String password = params[1];

            Object mezua = Service.login(user, password);
/*
            if (mezua == null) {
                Toast.makeText(getContext(), "Errorea egon da zerbitzariarekin", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mezua instanceof String) {
                String respuestaStr = (String) mezua;
                Log.i("LoginFragment", mezua.toString());

                if (respuestaStr.startsWith("OK")) {
                    Log.i("LoginFragment", mezua.toString());
                    loggedUser = (Users) mezua;

                    if (loggedUser.getTipos().getId() == 3) {
                        Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
                        /*
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new ProfilaFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "Solo los usuarios con tipo 3 pueden iniciar sesión.", Toast.LENGTH_SHORT).show();
                    }
                }
            }*/
            return false; // Call the login method
        }

        @Override
        protected void onPostExecute(Boolean egokia) {
            if (egokia) {
                Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}