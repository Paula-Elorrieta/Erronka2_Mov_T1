package com.example.elorrietapp.modelo;

public class Matriculaciones implements java.io.Serializable {

    private MatriculacionesId id;
    private Ciclos ciclos;
    private Users users;

    public Matriculaciones() {
    }

    public Matriculaciones(MatriculacionesId id, Ciclos ciclos, Users users) {
        this.id = id;
        this.ciclos = ciclos;
        this.users = users;
    }

    public MatriculacionesId getId() {
        return this.id;
    }

    public void setId(MatriculacionesId id) {
        this.id = id;
    }

    public Ciclos getCiclos() {
        return this.ciclos;
    }

    public void setCiclos(Ciclos ciclos) {
        this.ciclos = ciclos;
    }

    public Users getUsers() {
        return this.users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

}