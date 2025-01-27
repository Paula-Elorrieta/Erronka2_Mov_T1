package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Users;

public class LoginFragment extends Fragment {

    TextView textErabiltzailea;
    TextView textPasahitza;
    private Users loggedUser;
    private TextView aldatuPasahitza;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textErabiltzailea = view.findViewById(R.id.textErabiltzailea);
        textPasahitza = view.findViewById(R.id.textPasahitza);
        ImageView logo = view.findViewById(R.id.logo);
        aldatuPasahitza = view.findViewById(R.id.LinkPasahitzaAhaztuta);

        aldatuPasahitza.setEnabled(false);

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

        aldatuPasahitza.setOnClickListener(v -> {
            resetPassword(textErabiltzailea.getText().toString());
        });

        return view;
    }

    private void resetPassword(String erabiltzailea) {
        Toast.makeText(getContext(), "Pasahitza aldatzen...", Toast.LENGTH_SHORT).show();
        new ResetPasswordTask().execute(erabiltzailea);
    }

    // Función de validación de login
    private void validarLogin(TextView textErabiltzailea, TextView textPasahitza) {
        Toast.makeText(getContext(), "Komprobatzen...", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Login ondo", Toast.LENGTH_SHORT).show();

                // Reemplazar el fragmento con el nuevo fragmento si es de tipo 3 o 4
                if (loggedUser.getTipos() == 1 || loggedUser.getTipos() == 2) {
                    Toast.makeText(getContext(), "Bakarrik Ikasleak eta Irakasleak sar daitezke", Toast.LENGTH_SHORT).show();

                } else {
                    // Bidali informazioa MenuFragment-era
                    Bundle serializedUser = new Bundle();
                    serializedUser.putSerializable("loggedUser", loggedUser);

                    Gen gen = new Gen();
                    gen.setLoggedUser(loggedUser);
                    Log.i("LoginTask", "Usuario guardado: " + gen.getLoggedUser().getUsername() + " ID: " + gen.getLoggedUser().getId());

                    MenuFragment menuFragment = new MenuFragment();
                    menuFragment.setArguments(serializedUser);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, menuFragment)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                // Si no se recibe un usuario válido, mostrar error
                Toast.makeText(getContext(), "Erabiltzaile edo pasahitza desegokia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String user = params[0];

            // Llamar al servicio para intentar hacer login
            return Service.resetPassword(user);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getContext(), "Pasahitza aldatu da", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Erabiltzailea ez da existitzen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
