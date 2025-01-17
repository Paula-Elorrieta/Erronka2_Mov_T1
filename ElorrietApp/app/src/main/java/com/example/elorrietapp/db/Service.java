package com.example.elorrietapp.db;

import static java.security.AccessController.getContext;

import android.util.Log;
import android.widget.Toast;

import com.example.elorrietapp.modelo.Users;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Service {
    private static final String ip = "10.5.104.41";
    private static final int port = 5000;

    public static Users login(String user, String password) {
        try (Socket socket = new Socket(ip, port);  // Crear la conexión al servidor
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Para enviar datos al servidor
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) { // Para recibir datos del servidor

            Log.i("Service", "Conexión establecida");

            // Enviar la solicitud de login al servidor
            out.writeObject("LOGIN");
            out.writeObject(user);  // Enviar el nombre de usuario
            out.writeObject(password);  // Enviar la contraseña
            out.flush();  // Asegúrate de que los datos se envíen

            // Leer la respuesta del servidor
            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    // Si la respuesta es OK, el servidor enviará el objeto de usuario
                    Object userResponse = in.readObject();
                    if (userResponse instanceof Users) {
                        Users userLog = (Users) userResponse;
                        Log.i("Client", "Usuario recibido: " + userLog.getUsername());
                        return userLog;  // Retornar el usuario recibido del servidor
                    } else {
                        Log.e("Client", "Error: Respuesta inesperada del servidor - No se recibió un usuario");
                    }
                } else {
                    Log.e("Client", "Error en la respuesta del servidor: " + responseMessage);
                }
            } else {
                Log.e("Client", "Error: La respuesta del servidor no es de tipo String");
            }

        } catch (IOException e) {
            Log.e("Service", "Error de conexión o de E/S", e);
        } catch (ClassNotFoundException e) {
            Log.e("Service", "Error de deserialización", e);
        }
        return null;
    }
}
