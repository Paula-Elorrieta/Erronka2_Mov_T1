package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

        textErabiltzailea = view.findViewById(R.id.textErabiltzailea);
        textPasahitza = view.findViewById(R.id.textPasahitza);
        ImageView logo = view.findViewById(R.id.logo);

        // Animazioa kargatu
        Glide.with(this)
                .asGif()
                .load(R.drawable.eit)
                .into(logo);

        // Login balidazioa
        textPasahitza.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                validarLogin(textErabiltzailea, textPasahitza);
                return true;
            }
            return false;
        });

        return view;
    }

    // Función de validación de login
    private void validarLogin(TextView textErabiltzailea, TextView textPasahitza) {
        Toast.makeText(getContext(), "Validando login...", Toast.LENGTH_SHORT).show();
        new LoginTask().execute(textErabiltzailea.getText().toString(), textPasahitza.getText().toString());
    }

    private class LoginTask extends AsyncTask<String, Void, Users> {

        @Override
        protected Users doInBackground(String... params) {
            String user = params[0];
            String password = params[1];

            // Llamar al servicio para intentar hacer login
            Users logUser = Service.login(user, password);
            return logUser;
        }

        @Override
        protected void onPostExecute(Users result) {
            if (result != null) {
                loggedUser = result;
                Log.i("LoginTask", "Usuario recibido: " + loggedUser.getUsername() + " ID: " + loggedUser.getId());
                // Si el usuario se ha autenticado correctamente, mostrar mensaje y navegar
                Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
                // Reemplazar el fragmento con el nuevo fragmento
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new ProfilaFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                // Si no se recibe un usuario válido, mostrar error
                Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
