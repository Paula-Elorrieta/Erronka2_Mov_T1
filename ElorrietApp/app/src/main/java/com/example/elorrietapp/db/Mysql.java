package com.example.elorrietapp.db;

import com.example.elorrietapp.modelo.Horarios;
import com.example.elorrietapp.modelo.HorariosId;
import com.example.elorrietapp.modelo.Modulos;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;


public class Mysql {
    private static final String URL = "jdbc:mysql://10.5.104.41:3307/elorbase";
    private static final String USER = "remote_user";
    private static final String PASSWORD = "12345";

    public static void insertarReunion(Reuniones reunion) {
        new InsertReunionTask().execute(reunion);
    }
    public static ArrayList<Users> obtenerProfesores() {
        ArrayList<Users> profesores = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE tipo_id = 3";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Users user = new Users();
                user.setId(resultSet.getInt("id"));
                user.setTipos(resultSet.getInt("tipo_id"));
                user.setEmail(resultSet.getString("email"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setNombre(resultSet.getString("nombre"));
                user.setApellidos(resultSet.getString("apellidos"));
                user.setDni(resultSet.getString("dni"));
                user.setDireccion(resultSet.getString("direccion"));
                user.setTelefono1(resultSet.getObject("telefono1") != null ? resultSet.getInt("telefono1") : null);
                user.setTelefono2(resultSet.getObject("telefono2") != null ? resultSet.getInt("telefono2") : null);
                user.setArgazkia(resultSet.getBytes("argazkia"));

                profesores.add(user);
            }
        } catch (SQLException e) {
            Log.e("MYSQL", "Error al obtener los profesores: " + e.getMessage(), e);
        }

        return profesores;
    }
    // Método para obtener los horarios de un profesor
    public static ArrayList<Horarios> obtenerHorariosByProfesor(int profe_id) {
        ArrayList<Horarios> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horarios WHERE profe_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, profe_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Horarios horario = new Horarios();
                    HorariosId horarioId = new HorariosId();
                    horarioId.setDia(resultSet.getString("dia"));
                    horarioId.setHora(resultSet.getString("hora"));
                    horarioId.setModuloId(resultSet.getInt("modulo_id"));
                    horarioId.setProfeId(resultSet.getInt("profe_id"));
                    horario.setId(horarioId);
                    horarios.add(horario);
                }
            }
        } catch (SQLException e) {
            Log.e("MYSQL", "Error al obtener los horarios: " + e.getMessage(), e);
        }
        return horarios;
    }

    private static class InsertReunionTask extends AsyncTask<Reuniones, Void, Void> {
        @Override
        protected Void doInBackground(Reuniones... reuniones) {
            Reuniones reunion = reuniones[0];
            if (reunion == null || reunion.getUsersByProfesorId() == null || reunion.getUsersByAlumnoId() == null) {
                Log.e("MYSQL", "Error: La reunión o los usuarios están vacíos.");
                return null;
            }
            reunion.setEstado("pendiente");
            reunion.setEstadoEus("onartzeke");

            String sql = "INSERT INTO reuniones (profesor_id, alumno_id, estado, estado_eus, id_centro, titulo, asunto, aula, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, reunion.getUsersByProfesorId().getId());
                preparedStatement.setInt(2, reunion.getUsersByAlumnoId().getId());
                preparedStatement.setString(3, reunion.getEstado());
                preparedStatement.setString(4, reunion.getEstadoEus());
                preparedStatement.setString(5, reunion.getIdCentro());
                preparedStatement.setString(6, reunion.getTitulo());
                preparedStatement.setString(7, reunion.getAsunto());
                preparedStatement.setString(8, reunion.getAula());
                preparedStatement.setTimestamp(9, reunion.getFecha());

                int filasAfectadas = preparedStatement.executeUpdate();
                Log.d("MYSQL", "Reunión insertada correctamente. Filas afectadas: " + filasAfectadas);

            } catch (SQLException e) {
                Log.e("MYSQL", "Error al insertar la reunión: " + e.getMessage(), e);
            }
            return null;
        }
    }
}
