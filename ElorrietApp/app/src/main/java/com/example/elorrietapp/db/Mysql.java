package com.example.elorrietapp.db;

import com.example.elorrietapp.modelo.Reuniones;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import android.os.AsyncTask;
import android.util.Log;


public class Mysql {
    private static final String URL = "jdbc:mysql://10.5.104.41:3307/elorbase";
    private static final String USER = "remote_user";
    private static final String PASSWORD = "12345";

    public static void insertarReunion(Reuniones reunion) {
        new InsertReunionTask().execute(reunion);
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
