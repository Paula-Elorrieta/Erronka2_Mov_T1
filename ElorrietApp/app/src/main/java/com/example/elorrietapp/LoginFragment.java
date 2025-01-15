package com.example.elorrietapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

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
                validarLogin();
                return true;
            }
            return false;
        });

        return view;
    }

    // Función de validación de login
    private void validarLogin() {
        // Lógica de validación de ejemplo
        String usuario = "usuarioPrueba";
        String contraseña = "contraseñaPrueba";
        String inputUsuario = "usuarioPrueba"; // Reemplaza con el valor ingresado por el usuario
        String inputContraseña = "contraseñaPrueba"; // Reemplaza con el valor ingresado por el usuario

        if (usuario.equals(inputUsuario) && contraseña.equals(inputContraseña)) {
            Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}