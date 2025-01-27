package com.example.elorrietapp.gen;

import com.example.elorrietapp.modelo.Users;

public class Gen {
    private static Users loggedUser = null;

    public void setLoggedUser(Users loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Users getLoggedUser() {
        return loggedUser;
    }

    public Gen() {
    }
}
