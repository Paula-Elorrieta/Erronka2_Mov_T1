package com.example.elorrietapp.db;

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

    public static void bileraGehitu(Reuniones reunion) {
        new InsertBilerakTask().execute(reunion);
    }

    public static ArrayList<Users> irakasleakKargatu() {
        ArrayList<Users> irakasleak = new ArrayList<>();
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

                irakasleak.add(user);
            }
        } catch (SQLException e) {
            Log.e("MYSQL", "Errorea: " + e.getMessage(), e);
        }

        return irakasleak;
    }

    private static class InsertBilerakTask extends AsyncTask<Reuniones, Void, Void> {
        @Override
        protected Void doInBackground(Reuniones... bilerak) {
            Reuniones reunion = bilerak[0];
            if (reunion == null || reunion.getUsersByProfesorId() == null || reunion.getUsersByAlumnoId() == null) {
                Log.e("MYSQL", "Error: bilera edo erabiltzaileak hutsik daude.");
                return null;
            }
            reunion.setEstado("pendiente");
            reunion.setEstadoEus("onartzeke");

            String sql = "INSERT INTO reuniones (profesor_id, alumno_id, estado_en, estado, estado_eus, id_centro, titulo, asunto, aula, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, reunion.getUsersByProfesorId().getId());
                preparedStatement.setInt(2, reunion.getUsersByAlumnoId().getId());
                preparedStatement.setString(3, "pending");
                preparedStatement.setString(4, reunion.getEstado());
                preparedStatement.setString(5, reunion.getEstadoEus());
                preparedStatement.setString(6, reunion.getIdCentro());
                preparedStatement.setString(7, reunion.getTitulo());
                preparedStatement.setString(8, reunion.getAsunto());
                preparedStatement.setString(9, reunion.getAula());
                preparedStatement.setTimestamp(10, reunion.getFecha());

                int filasAfectadas = preparedStatement.executeUpdate();
                Log.d("MYSQL", "Ondo egin da insert: " + filasAfectadas);

            } catch (SQLException e) {
                Log.e("MYSQL", "Errorea bilera gehitzean: " + e.getMessage(), e);
            }
            return null;
        }
    }
}
