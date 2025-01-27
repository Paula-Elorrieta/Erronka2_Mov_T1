package com.example.elorrietapp.db;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomObjectInputStream extends ObjectInputStream {

    public CustomObjectInputStream(java.io.InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        // Resolver la clase Users
        if (desc.getName().equals("modelo.Users")) {
            return com.example.elorrietapp.modelo.Users.class;
        }
        // Resolver la clase Horarios
        if (desc.getName().equals("modelo.Horarios")) {
            return com.example.elorrietapp.modelo.Horarios.class;
        }
        // Resolver la clase Modulos
        if (desc.getName().equals("modelo.Modulos")) {
            return com.example.elorrietapp.modelo.Modulos.class;
        }
        // Resolver la clase Modulos
        if (desc.getName().equals("modelo.HorariosId")) {
            return com.example.elorrietapp.modelo.HorariosId.class;
        }
        // Resolver la clase Reuniones
        if (desc.getName().equals("modelo.Reuniones")) {
            return com.example.elorrietapp.modelo.Reuniones.class;
        }

        return super.resolveClass(desc);
    }
}