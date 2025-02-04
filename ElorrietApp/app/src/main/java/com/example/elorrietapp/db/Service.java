package com.example.elorrietapp.db;

import android.util.Log;

import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Ciclos;
import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.HorariosId;
import com.example.elorrietapp.modelo.Ikastetxeak;
import com.example.elorrietapp.modelo.Matriculaciones;
import com.example.elorrietapp.modelo.MatriculacionesId;
import com.example.elorrietapp.modelo.Modulos;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Tipos;
import com.example.elorrietapp.modelo.Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Service {
    private static final String ip = "10.5.104.41";
    private static final int port = 5000;

    public static Users login(String user, String password) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {
            Log.i("Service", "Konexioa eginda");

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
                        Object tipo = in.readObject();
                        if (tipo instanceof Integer) {
                            userLog.setTipos((int) tipo);
                            return userLog;
                        } else {
                            Log.e("LOGIN", "Errorea: ez da erabiltzailearen mota jaso");
                        }
                    } else {
                        Log.e("LOGIN", "Erroea: ez da erabiltzailea jaso");
                    }
                } else {
                    Log.e("LOGIN", "Zerbitzataria konexioa ez da ondo egin: " + responseMessage);
                }
            } else {
                Log.e("LOGIN", "Errorea: ez da string bat jaso");
            }

        } catch (IOException e) {
            Log.e("LOGIN", "Errorea konexioarekin", e);
        } catch (ClassNotFoundException e) {
            Log.e("LOGIN", "Desirelixasio errorea", e);
        }
        return null;
    }

    public static boolean resetPassword(String user) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("ALDATUPASS");
            out.writeObject(user);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    return true;
                } else {
                    Log.e("Client", "Errorea zerbitzariarekin: " + responseMessage);
                }
            } else {
                Log.e("Client", "Errorea: ez da string bat jaso");
            }

        } catch (IOException e) {
            Log.e("ResetPassword", "Errorea konexioarekin", e);
        } catch (ClassNotFoundException e) {
            Log.e("Service", "Desirelixasio errorea", e);
        }
        return false;
    }

    public static List<Horarios> getHorariosProfeByid(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("ORDUTEGIA");
            out.writeObject(id);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if (responseMessage.equals("OK")) {
                    Object ordua = in.readObject();
                    if (ordua instanceof List<?>) {
                        List<?> listaLortuta = (List<?>) ordua;

                        for (Object obj : listaLortuta) {
                            if (!(obj instanceof Horarios)) {
                                Log.e("Client", "Errorea: Elementu bat ez da Horarios klasekoa");
                                return null;
                            }
                        }

                        List<Horarios> ordutegiak = (List<Horarios>) listaLortuta;
                        List<Modulos> moduluak = (List<Modulos>) in.readObject();
                        List<Users> erabiltzaileak = (List<Users>) in.readObject();
                        List<HorariosId> ordutegiID = (List<HorariosId>) in.readObject();

                        for (int i = 0; i < ordutegiak.size(); i++) {
                            Horarios ordutegia = ordutegiak.get(i);

                            ordutegia.setModulos(moduluak.get(i));
                            ordutegia.setUsers(erabiltzaileak.get(i));
                            ordutegia.setId(ordutegiID.get(i));
                        }

                        return ordutegiak;
                    } else {
                        Log.e("getHorariosProfeByid", "Errorea: Elementu bat ez da lista bat");
                    }
                } else {
                    Log.e("getHorariosProfeByid", "Erroea: Zerbitzari erantzuna ez zen ondo " + responseMessage);
                }
            } else {
                Log.e("getHorariosProfeByid", "Error: ez da string bat jaso");
            }

        } catch (IOException | ClassNotFoundException e) {
            Log.e("Service", "Desirelixasio errorea", e);
        }
        return null;
    }

    public List<Matriculaciones> getMatrikulazioakByErabiltzaileak(int id) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("MATRIKULAK");
            out.writeObject(id);
            out.flush();

            Object erantzuna = in.readObject();
            if (erantzuna instanceof String) {
                String erantzunaMezua = (String) erantzuna;
                if (erantzunaMezua.equals("OK")) {

                    Object matrikulazioak = in.readObject();
                    if (matrikulazioak instanceof List<?>) {
                        List<?> listaLortuta = (List<?>) matrikulazioak;

                        for (Object obj : listaLortuta) {
                            if (!(obj instanceof Matriculaciones)) {
                                Log.e("Client", "Errorea: Elementu bat ez da Matriculaciones klasekoa");
                                return null;
                            }

                            List<Matriculaciones> matrikulazioaZerrenda = (List<Matriculaciones>) listaLortuta;
                            List<Ciclos> zikloak = (List<Ciclos>) in.readObject();
                            List<Users> erabiltzaileak = (List<Users>) in.readObject();
                            List<MatriculacionesId> matrikulazioakID = (List<MatriculacionesId>) in.readObject();

                            for (int i = 0; i < matrikulazioaZerrenda.size(); i++) {
                                Matriculaciones matriculacion = matrikulazioaZerrenda.get(i);
                                matriculacion.setCiclos(zikloak.get(i));
                                matriculacion.setUsers(erabiltzaileak.get(i));
                                matriculacion.setId(matrikulazioakID.get(i));
                            }

                            return matrikulazioaZerrenda;
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
                        Object tipos = in.readObject();
                        if (tipos instanceof List<?>) {
                            ArrayList<Tipos> tiposList = (ArrayList<Tipos>) tipos;
                            for (int i = 0; i < userList.size(); i++) {
                                userList.get(i).setTipos(tiposList.get(i).getId());
                            }
                        }

                        return userList;
                    } else {
                        Log.e("handleGetUsers", "Errorea: Elementu bat ez da lista bat");
                    }
                } else {
                    Log.e("handleGetUsers", "Errorea zerbitzari konexioan: " + responseMessage);
                }
            } else {
                Log.e("handleGetUsers", "Error: La respuesta del servidor no es un String como se esperaba");
            }

        } catch (IOException e) {
            Log.e("Service", "Errorea konexioan", e);
        } catch (ClassNotFoundException e) {
            Log.e("Service", "Desirelixasio errorea", e);
        } catch (Exception e) {
            Log.e("Service", "Errorea", e);
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
                        Object usersObj = in.readObject();
                        if (usersObj instanceof List<?>) {
                            List<Users> ikasleak = (List<Users>) usersObj;

                            for (int i = 0; i < reuniones.size(); i++) {
                                reuniones.get(i).setUsersByAlumnoId(ikasleak.get(i));
                            }

                            return reuniones;
                        } else {
                            Log.e("handleGetBilera", "Errorea: Ez da ikasleak jaso");
                        }
                        return reuniones;
                    } else {
                        Log.e("handleGetBilera", "Errorea: Ez da bilera jaso");
                    }
                } else {
                    Log.e("handleGetBilera", "Errorea konexioan: " + responseMessage);
                }
            } else {
                Log.e("handleGetBilera", "Errorea: Ez da string bat jaso");
            }
        } catch (IOException e) {
            Log.e("Service", "Errorea konexioan", e);
        } catch (ClassNotFoundException e) {
            Log.e("Service", "Desirelixasio errorea", e);
        } catch (Exception e) {
            Log.e("Service", "Errorea", e);
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
                        Object irakasleakObj = in.readObject();
                        if (irakasleakObj instanceof List<?>) {
                            List<Users> irakasleak = (List<Users>) irakasleakObj;
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

    public String handleUpdateReunion(int id, String estadoEus, String estado, String estadoEn) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("BILERA_UPDATE_ANDROID");
            out.writeObject(id);
            out.writeObject(estadoEus);
            out.writeObject(estado);
            out.writeObject(estadoEn);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                String responseMessage = (String) response;
                if ("OK".equals(responseMessage)) {
                    return "Bilera eguneratu da";
                } else {
                    Log.e("Client", "Error: " + responseMessage);
                }
            } else {
                Log.e("Client", "Error: La respuesta del servidor no es un String como se esperaba.");
            }

        } catch (IOException e) {
            Log.e("Client", "Error de conexión o de E/S", e);
        } catch (ClassNotFoundException e) {
            Log.e("Client", "Error de deserialización", e);
        } catch (Exception e) {
            Log.e("Client", "Error inesperado", e);
        }

        return "Errorea";
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

    public ArrayList<Ikastetxeak> handleGetAllIkastetxeak() {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             CustomObjectInputStream in = new CustomObjectInputStream(socket.getInputStream())) {

            out.writeObject("ALLIKASTETXEAK"); // Nueva petición para obtener todos los ikastetxeak
            out.flush();
            Log.i("Service", "Solicitud enviada para obtener todos los ikastetxeak");

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
                        Log.e("Client", "Error: No se recibió una lista válida de ikastetxeak.");
                    }
                } else {
                    Log.e("Client", "Error en la respuesta del servidor: " + responseMessage);
                }
            } else {
                Log.e("Client", "Error: La respuesta del servidor no es un String esperado.");
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

    public void handleEmailBilera(Reuniones bilera, String ikastetxea) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            if (socket.isConnected() && !socket.isClosed()) {
                out.writeObject("EMAILBILERA");

                if (Gen.getLoggedUser().getTipos() == 3) {
                    out.writeObject(bilera.getUsersByProfesorId().getEmail());
                    Log.e("Client", "Email: " + bilera.getUsersByProfesorId().getEmail());
                } else {
                    out.writeObject(bilera.getUsersByAlumnoId().getEmail());
                    Log.e("Client", "Email: " + bilera.getUsersByAlumnoId().getEmail());
                }

                out.writeObject(bilera.getAsunto());
                out.writeObject(bilera.getFecha());
                out.writeObject("Ikastetxea: " + ikastetxea + " eta aula: " + bilera.getAula());
                out.flush();

                Object response = in.readObject();
                if (response instanceof String) {
                    String responseMessage = (String) response;
                    if ("OK".equals(responseMessage)) {
                        Log.i("Client", "El email se ha enviado correctamente.");
                    } else {
                        Log.e("Client", "Error: " + responseMessage);
                    }
                } else {
                    Log.e("Client", "Error: La respuesta del servidor no es un String como se esperaba.");
                }
            } else {
                Log.e("Client", "El socket no está conectado.");
            }

        } catch (IOException e) {
            Log.e("Client", "Error de conexión o de E/S: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("Client", "Error de deserialización: " + e.getMessage());
        } catch (Exception e) {
            Log.e("Client", "Error inesperado: " + e.getMessage());
        }
    }
}
