package com.example.elorrietapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.modelo.Users;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SortuBilerakFragment extends Fragment {

    ArrayList<Users> erabiltzaileakGuztiak = new ArrayList<>();
    ArrayList<Users> irakasleak = new ArrayList<>();
    ArrayList<Users> ikasleak = new ArrayList<>();
    Service service = new Service();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sortu_bilerak, container, false);


        return view;
    }

    private void erabiltzaileakKargatu(Users userLog) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                erabiltzaileakGuztiak = service.handleGetUsers();

                for (Users user : erabiltzaileakGuztiak) {
                    if (user.getTipos() == 3) {
                        irakasleak.add(user);
                    } else if (user.getTipos() == 4) {
                        ikasleak.add(user);
                    }
                }


            } catch (Exception e) {
                Log.e("Bilerak", "Error cargando reuniones", e);
            }
        });
    }
}