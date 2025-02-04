package com.example.elorrietapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.elorrietapp.fragments.LoginFragment;
import com.example.elorrietapp.fragments.ProfilaFragment;
import com.example.elorrietapp.gen.Gen;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return Gen.getLoggedUser() != null;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.profila) {
            ProfilaFragment fragment = new ProfilaFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.hizkuntza) {
            toggleLanguage();
            return true;
        } else if (id == R.id.logout) {
            Gen.getLoggedUser().setNombre("");
            Gen.getLoggedUser().setId(0);
            Gen.getLoggedUser().setTipos(0);
            Gen.getLoggedUser().setPassword("");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void toggleLanguage() {
        String currentLanguage = Locale.getDefault().getLanguage();
        String newLanguage;
        if ("es".equals(currentLanguage)) {
            newLanguage = "";
        } else {
            newLanguage = "es";
        }
        changeLanguage(newLanguage);
    }

    private void changeLanguage(String languageCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        saveLanguagePreference(languageCode);
        recreate();
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }


}