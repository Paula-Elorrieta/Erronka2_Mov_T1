package com.example.elorrietapp.db;

import android.util.Log;

import com.example.elorrietapp.modelo.Ciclos;
import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.HorariosId;
import com.example.elorrietapp.modelo.Ikastetxeak;
import com.example.elorrietapp.modelo.Matriculaciones;
import com.example.elorrietapp.modelo.MatriculacionesId;
import com.example.elorrietapp.modelo.Modulos;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Users;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Service {
    //private static final String ip = "192.168.0.22";
    private static final String ip = "10.5.104.41";
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
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

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

                        List<Modulos> modulos = (List<Modulos>) in.readObject();
                        List<Users> users = (List<Users>) in.readObject();
                        List<HorariosId> horariosIda = (List<HorariosId>) in.readObject();

                        for (int i = 0; i < horarios.size(); i++) {
                            Horarios horario = horarios.get(i);

                            horario.setModulos(modulos.get(i));
                            horario.setUsers(users.get(i));
                            horario.setId(horariosIda.get(i));

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

    public List<Matriculaciones> getMatriculacionesByUser(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            Log.i("Service", "Conexión establecida");

            out.writeObject("MATRIKULAK");
            out.writeObject(id);
            out.flush();

            Object response = in.readObject();
            Log.e("noif", response + "");

            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {

                    Object matriculaciones = in.readObject();
                    if (matriculaciones instanceof List<?>) {
                        List<?> rawList = (List<?>) matriculaciones;

                        Log.e("Client", "Matriculaciones recibidas: " + rawList.size());

                        for (Object obj : rawList) {
                            if (!(obj instanceof Matriculaciones)) {
                                Log.e("Client", "Error: Uno de los elementos no es del tipo Matriculaciones");
                                return null;
                            }

                            List<Matriculaciones> matriculacionesList = (List<Matriculaciones>) rawList;

                            List<Ciclos> ciclos = (List<Ciclos>) in.readObject();
                            List<Users> users = (List<Users>) in.readObject();
                            List<MatriculacionesId> matriculacionesId = (List<MatriculacionesId>) in.readObject();

                            for (int i = 0; i < matriculacionesList.size(); i++) {
                                Matriculaciones matriculacion = matriculacionesList.get(i);

                                matriculacion.setCiclos(ciclos.get(i));
                                matriculacion.setUsers(users.get(i));
                                matriculacion.setId(matriculacionesId.get(i));

                                Log.i("Matriculaciones", "ID: " + matriculacion.getId().getClass() +
                                        ", Usuario: " + (matriculacion.getUsers() != null ? matriculacion.getUsers().getNombre() : "N/A") +
                                        ", Ciclo: " + (matriculacion.getCiclos().getNombre() != null ? matriculacion.getCiclos().getNombre() : "N/A") +
                                        ", MatriculacionesId: " + (matriculacion.getId() != null ? matriculacion.getId().getCurso() : "N/A"));
                            }

                            return matriculacionesList;
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("Service", "Error de conexión o de deserialización", e);
        }
        return null;
    }

    public ArrayList<Users> handleGetUsers() {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("GETUSERS");
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object users = in.readObject();
                    if (users instanceof List<?>) {
                        ArrayList<Users> userList = (ArrayList<Users>) users;
                        Log.i("Client", "Usuarios recibidos: " + userList);

                        for (Users user : userList) {
                            Log.i("Client", "Usuario: " + user.getTipos());
                        }

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

    public ArrayList<Reuniones> handleGetBilera(int idIrakasle) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("BILERA");
            out.writeObject(idIrakasle);
            out.flush();

            Object response = in.readObject();

            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object reunionesObj = in.readObject();
                    if (reunionesObj instanceof List<?>) {
                        ArrayList<Reuniones> reuniones = (ArrayList<Reuniones>) reunionesObj;
                        Log.i("Client", "Reuniones recibidas: " + reuniones);

                        Object usersObj = in.readObject();
                        if (usersObj instanceof List<?>) {
                            List<Users> ikasleak = (List<Users>) usersObj;
                            Log.i("Client", "Usuarios recibidos: " + ikasleak);

                            for (int i = 0; i < reuniones.size(); i++) {
                                reuniones.get(i).setUsersByAlumnoId(ikasleak.get(i));
                            }

                            return reuniones;
                        } else {
                            Log.e("Client", "Error: No se recibió una lista de usuarios válida.");
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

    public ArrayList<Reuniones> getBilerakIkasleak(int id) {
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
                    Object reunionesObj = in.readObject();
                    if (reunionesObj instanceof List<?>) {
                        ArrayList<Reuniones> reuniones = (ArrayList<Reuniones>) reunionesObj;
                        Log.i("Client", "Reuniones recibidas: " + reuniones);
                        Object irakasleakObj = in.readObject();
                        if (irakasleakObj instanceof List<?>) {
                            List<Users> irakasleak = (List<Users>) irakasleakObj;
                            Log.i("Client", "Irakasleak recibidos: " + irakasleak);

                            for (int i = 0; i < reuniones.size(); i++) {
                                reuniones.get(i).setUsersByProfesorId(irakasleak.get(i));
                            }

                            return reuniones;

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

    public ArrayList<Users> handleGetIkasleakByIrakasleak(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("IKASLEZERRENDA");
            out.writeObject(id);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object usersObj = in.readObject();
                    if (usersObj instanceof ArrayList<?>) {
                        ArrayList<Users> users = (ArrayList<Users>) usersObj;
                        Log.i("Client", "Usuarios recibidos: " + users);
                        return users;
                    } else {
                        Log.e("Client", "Error: No se recibió una lista de usuarios válida");
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

    public ArrayList<Users> handleGetIrakasleakByIkasleak(int ikasleId) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("IRAKASLEZERRENDA");
            out.writeObject(ikasleId);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object usersObj = in.readObject();
                    if (usersObj instanceof ArrayList<?>) {
                        ArrayList<Users> users = (ArrayList<Users>) usersObj;
                        Log.i("Client", "Usuarios recibidos: " + users);
                        return users;
                    } else {
                        Log.e("Client", "Error: No se recibió una lista de usuarios válida");
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

    public ArrayList<Horarios> handleGetHorariosByIkasle(int ikasleId) {
        Socket socket = null;
        ObjectOutputStream out = null;
        CustomObjectInputStream in = null;

        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new CustomObjectInputStream(socket.getInputStream());

            out.writeObject("IKASLEORDUTEGIA");
            out.writeObject(ikasleId);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object horariosObj = in.readObject();
                    if (!(horariosObj instanceof ArrayList<?>)) return null;
                    ArrayList<Horarios> horarios = (ArrayList<Horarios>) horariosObj;

                    Object modulosObj = in.readObject();
                    if (!(modulosObj instanceof ArrayList<?>)) return null;
                    ArrayList<Modulos> modulos = (ArrayList<Modulos>) modulosObj;

                    Object ciclosObj = in.readObject();
                    if (!(ciclosObj instanceof ArrayList<?>)) return null;
                    ArrayList<Ciclos> ciclos = (ArrayList<Ciclos>) ciclosObj;


                    Object usersObj = in.readObject();
                    if (!(usersObj instanceof ArrayList<?>)) return null;
                    ArrayList<Users> users = (ArrayList<Users>) usersObj;

                    Object matriculacionesObj = in.readObject();
                    if (!(matriculacionesObj instanceof ArrayList<?>)) return null;
                    ArrayList<Matriculaciones> matriculaciones = (ArrayList<Matriculaciones>) matriculacionesObj;

                    Object idsObj = in.readObject();
                    if (!(idsObj instanceof ArrayList<?>)) return null;
                    ArrayList<MatriculacionesId> ids = (ArrayList<MatriculacionesId>) idsObj;

                    Object hIdObj = in.readObject();
                    if (!(hIdObj instanceof ArrayList<?>)) return null;
                    ArrayList<HorariosId> hId = (ArrayList<HorariosId>) hIdObj;
                    for (int i = 0; i < horarios.size(); i++) {
                        modulos.get(i).setCiclos(ciclos.get(i));
                        horarios.get(i).setModulos(modulos.get(i));
                        horarios.get(i).setUsers(users.get(i));
                        horarios.get(i).setId(hId.get(i));
                    }
                    return horarios;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("Service", "Error", e);
        }
        return null;
    }

    public ArrayList<Ikastetxeak> handleGetIkastetxeak() {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("IKASTETXEAK");
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object ikastetxeakObj = in.readObject();
                    if (ikastetxeakObj instanceof ArrayList<?>) {
                        ArrayList<Ikastetxeak> ikastetxeak = (ArrayList<Ikastetxeak>) ikastetxeakObj;
                        Log.i("Client", "Ikastetxeak recibidos: " + ikastetxeak);
                        return ikastetxeak;
                    } else {
                        Log.e("Client", "Error: No se recibió una lista de ikastetxeak válida");
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
