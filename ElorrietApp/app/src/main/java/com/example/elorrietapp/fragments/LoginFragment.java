package com.example.elorrietapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        requireActivity().setTitle("Login");
        setHasOptionsMenu(true);

        textErabiltzailea = view.findViewById(R.id.textErabiltzailea);
        textPasahitza = view.findViewById(R.id.textPasahitza);
        ImageView logo = view.findViewById(R.id.logo);
        aldatuPasahitza = view.findViewById(R.id.LinkPasahitzaAhaztuta);
        Button btnLogin = view.findViewById(R.id.btnLogin);

        Glide.with(this)
                .asGif()
                .load(R.drawable.eit)
                .into(logo);

        textPasahitza.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                loginBalidazioa(textErabiltzailea, textPasahitza);
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> {
            loginBalidazioa(textErabiltzailea, textPasahitza);
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

    private void loginBalidazioa(TextView textErabiltzailea, TextView textPasahitza) {

        if (textErabiltzailea.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Erabiltzaile ezin da hutsik egon", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textPasahitza.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Pasahitza ezin da hutsik egon", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Egiaztatzen...", Toast.LENGTH_SHORT).show();
        new LoginTask().execute(textErabiltzailea.getText().toString(), textPasahitza.getText().toString());
    }

    private class LoginTask extends AsyncTask<String, Void, Users> {

        @Override
        protected Users doInBackground(String... params) {
            String user = params[0];
            String password = params[1];
            Users logUser = Service.login(user, password);
            return logUser;
        }

        @Override
        protected void onPostExecute(Users result) {
            if (result != null) {
                loggedUser = result;

                if (loggedUser.getTipos() == 1 || loggedUser.getTipos() == 2) {
                    Toast.makeText(getContext(), "Bakarrik ikasleak eta irakasleak sar daitezke", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Ongi etorri", Toast.LENGTH_SHORT).show();
                    Bundle serializedUser = new Bundle();
                    serializedUser.putSerializable("loggedUser", loggedUser);
                    Gen gen = new Gen();
                    gen.setLoggedUser(loggedUser);

                    MenuFragment menuFragment = new MenuFragment();
                    menuFragment.setArguments(serializedUser);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, menuFragment)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Erabiltzaile edo pasahitza desegokia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String user = params[0];
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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }
}