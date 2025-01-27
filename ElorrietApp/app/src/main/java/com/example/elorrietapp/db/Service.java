package com.example.elorrietapp.db;

import android.util.Log;

import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.Users;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Service {
    private static final String ip = "10.5.104.41";
    //private static final String ip = "10.5.104.41";
    private static final int port = 5000;

    public static Users login(String user, String password) {
        try (Socket socket = new Socket(ip, port);  // Crear la conexión al servidor
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Para enviar datos al servidor
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) { // Para recibir datos del servidor

            Log.i("Service", "Conexión establecida");

            out.writeObject("LOGIN");
            out.writeObject(user);
            out.writeObject(password);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object userResponse = in.readObject();
                    if (userResponse instanceof Users) {
                        Users userLog = (Users) userResponse;
                        Log.i("Client", "Usuario recibido: " + userLog.getUsername());
                        Log.i("Client", "Tipo de usuario recibido: " + userLog.getTipos());
                        Object tipo = in.readObject();
                        Log.i("Client", "Tipo de usuario como obj: " + tipo);
                        if (tipo instanceof Integer) {
                            Log.i("Client", "Tipo de usuario: " + tipo);
                            userLog.setTipos((int) tipo);
                            Log.i("Client", "Tipo de usuario: " + userLog.getTipos());
                            return userLog;
                        } else {
                            Log.e("Client", "Error: Respuesta inesperada del servidor - No se recibió un tipo de usuario");
                        }
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

    public static boolean resetPassword(String user) {
        Log.e("Service", "Cambiando contraseña para el usuario: " + user);
        try (Socket socket = new Socket(ip, port);  // Crear la conexión al servidor
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Para enviar datos al servidor
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) { // Para recibir datos del servidor

            Log.i("Service", "Conexión establecida");

            // Enviar la solicitud de login al servidor
            out.writeObject("ALDATUPASS");
            out.writeObject(user);  // Enviar el nombre de usuario
            out.flush();  // Asegúrate de que los datos se envíen

            // Leer la respuesta del servidor
            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Log.e("Client", "Contraseña cambiada correctamente");
                    return true;
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
        return false;
    }

    public List<Horarios> getHorariosProfeByid(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            Log.i("Service", "Conexión establecida");

            // Enviar la solicitud al servidor
            out.writeObject("ORDUTEGIA");
            out.writeObject(id);
            out.flush();

            // Leer la respuesta del servidor
            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    // La respuesta fue OK, ahora intentamos leer la lista de horarios
                    Object hora = in.readObject();
                    if (hora instanceof List<?>) {
                        List<Horarios> horarios = (List<Horarios>) hora;
                        Log.i("Client", "Horarios recibidos: " + horarios);
                        return horarios; // Devolver la lista de horarios
                    } else {
                        Log.e("Client", "Error: Respuesta inesperada del servidor - No se recibió una lista de horarios válida");
                    }
                } else {
                    Log.e("Client", "Error en la respuesta del servidor: " + responseMessage);
                }
            } else {
                Log.e("Client", "Error: La respuesta del servidor no es un String como se esperaba");
            }

        } catch (IOException e) {
            Log.e("Service", "Error de conexión o de E/S", e);
        } catch (ClassNotFoundException e) {
            Log.e("Service", "Error de deserialización", e);
        } catch (Exception e) {
            Log.e("Service", "Error inesperado", e);
        }
        return null;
    }

}
