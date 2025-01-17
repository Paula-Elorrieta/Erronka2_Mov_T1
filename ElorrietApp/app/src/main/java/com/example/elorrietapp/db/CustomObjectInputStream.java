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
        if (desc.getName().equals("modelo.Users")) {
            return com.example.elorrietapp.modelo.Users.class;
        }
        return super.resolveClass(desc);
    }
}