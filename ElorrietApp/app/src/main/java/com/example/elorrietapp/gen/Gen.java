package com.example.elorrietapp.gen;

import com.example.elorrietapp.modelo.Users;

public class Gen {
    private static Users loggedUser = null;
    private static Users[] users = null;

    public void setLoggedUser(Users loggedUser) {
        this.loggedUser = loggedUser;
    }

    public static Users getLoggedUser() {
        return loggedUser;
    }

    public void setUsers(Users[] users) {
        this.users = users;
    }

    public static Users[] getUsers() {
        return users;
    }


    public Gen() {
    }
}
