package com.example.elorrietapp.db;

import static java.security.AccessController.getContext;

import android.util.Log;
import android.widget.Toast;

import com.example.elorrietapp.modelo.Users;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Service {
    private static final String ip = "10.5.104.41";
    private static final int port = 5000;

    public static Object login(String user, String password) {
        try {
            // Establecer la conexión con el servidor
            Log.i("Service", "Pre");
            Socket socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Log.i("Service", "out");
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Log.i("Service", "in");

            out.writeObject("LOGIN");
            out.writeObject(user);
            out.writeObject(password);
            out.flush();

            Object respuesta = in.readObject();
            Users respuesta2 =  (Users) in.readObject();
            Log.i("Service", "respuesta");
            Log.e("Service", respuesta.toString());
            Log.e("Service", respuesta2.toString());
            return respuesta;
            /*
            //String respuesta = "";
            if (respuesta instanceof String) {
                String respuestaStr = (String) respuesta;
                if (respuestaStr.startsWith("OK")) {
                    Users loggedUser = (Users) in.readObject();
                    if (loggedUser.getTipos().getId() == 3) {
                        return "Éxito";
                    } else {
                        return "Solo los usuarios con tipo 3 pueden iniciar sesión.";
                    }
                } else {
                    return respuestaStr;
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Service", "CATCH");
        return null;
    }
}
