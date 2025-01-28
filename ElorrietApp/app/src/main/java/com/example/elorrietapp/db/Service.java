package com.example.elorrietapp.db;

import android.util.Log;

import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.HorariosId;
import com.example.elorrietapp.modelo.Modulos;
import com.example.elorrietapp.modelo.Reuniones;
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

    public static List<Horarios> getHorariosProfeByid(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            Log.i("Service", "Conexión establecida");

            out.writeObject("ORDUTEGIA");
            out.writeObject(id);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    // Leer la lista de horarios
                    Object hora = in.readObject();
                    if (hora instanceof List<?>) {
                        List<?> rawList = (List<?>) hora;

                        // Asegúrate de que los elementos son de tipo Horarios
                        for (Object obj : rawList) {
                            if (!(obj instanceof Horarios)) {
                                Log.e("Client", "Error: Uno de los elementos no es del tipo Horarios");
                                return null;
                            }
                        }

                        @SuppressWarnings("unchecked")
                        List<Horarios> horarios = (List<Horarios>) rawList;
                        Log.i("Client", "Horarios recibidos: " + horarios.size());

                        // Leer las listas adicionales de Modulos, Users, y HorariosId en el mismo orden que el servidor
                        List<Modulos> modulos = (List<Modulos>) in.readObject();
                        List<Users> users = (List<Users>) in.readObject();
                        List<HorariosId> horariosIda = (List<HorariosId>) in.readObject();

                        // Ahora asignamos los datos recibidos a los objetos Horarios
                        for (int i = 0; i < horarios.size(); i++) {
                            Horarios horario = horarios.get(i);

                            // Rellenamos el objeto Horarios con los datos correspondientes
                            horario.setModulos(modulos.get(i));
                            horario.setUsers(users.get(i));
                            horario.setId(horariosIda.get(i));

                            // Imprimir detalles de los horarios
                            Log.i("Horarios", "ID: " + horario.getId().getClass() +
                                    ", Usuario: " + (horario.getUsers() != null ? horario.getUsers().getNombre() : "N/A") +
                                    ", Módulo: " + (horario.getModulos().getNombre() != null ? horario.getModulos().getNombre() : "N/A") +
                                    ", HorariosId: " + (horario.getId().getDia() != null ? horario.getId().getHora() : "N/A"));
                        }

                        return horarios;
                    } else {
                        Log.e("Client", "Error: No se recibió una lista válida de horarios");
                    }
                } else {
                    Log.e("Client", "Error: Respuesta del servidor no fue OK: " + responseMessage);
                }
            } else {
                Log.e("Client", "Error: Respuesta inesperada del servidor");
            }

        } catch (IOException | ClassNotFoundException e) {
            Log.e("Service", "Error de conexión o de deserialización", e);
        }
        return null;
    }

    public List<Users> getUserGuztiak() {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("ERABILTZAILEAK");
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object users = in.readObject();
                    if (users instanceof List<?>) {
                        List<Users> userList = (List<Users>) users;
                        Log.i("Client", "Usuarios recibidos: " + userList);
                        return userList;
                    } else {
                        Log.e("Client", "Error: Respuesta inesperada del servidor - No se recibió una lista de usuarios válida");
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

    public List<Reuniones> getBilerakIkasleak(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("BILAREIKASLE");
            out.writeObject(id);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    // Si la respuesta es "OK", entonces esperamos recibir las listas
                    Object reunionesObj = in.readObject();
                    if (reunionesObj instanceof List<?>) {
                        List<Reuniones> reuniones = (List<Reuniones>) reunionesObj;
                        Log.i("Client", "Reuniones recibidas: " + reuniones);

                        Object irakasleakObj = in.readObject();
                        if (irakasleakObj instanceof List<?>) {
                            List<Users> irakasleak = (List<Users>) irakasleakObj;
                            Log.i("Client", "Irakasleak recibidos: " + irakasleak);

                            for (int i = 0; i < reuniones.size(); i++) {
                                reuniones.get(i).setUsersByProfesorId(irakasleak.get(i));
                            }

                        } else {
                            Log.e("Client", "Error: No se recibió una lista de profesores válida.");
                        }

                        return reuniones;
                    } else {
                        Log.e("Client", "Error: No se recibió una lista de reuniones válida");
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
