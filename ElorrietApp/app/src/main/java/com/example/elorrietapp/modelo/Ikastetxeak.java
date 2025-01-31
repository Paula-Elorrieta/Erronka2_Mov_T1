package com.example.elorrietapp.modelo;

import java.io.Serializable;

public class Ikastetxeak implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CCEN;
    private String NOM;
    private String NOME;
    private String DGENRC;
    private String DMUNIC;
    private String DOMI;
    private int CPOS;
    private String TEL1;
    private String EMAIL;
    private String PAGINA;
    private double LATITUD;
    private double LONGITUD;

    public String getCCEN() {
        return CCEN;
    }

    public void setCCEN(String CCEN) {
        this.CCEN = CCEN;
    }

    public String getNOM() {
        return NOM;
    }

    public void setNOM(String NOM) {
        this.NOM = NOM;
    }

    public String getNOME() {
        return NOME;
    }

    public void setNOME(String NOME) {
        this.NOME = NOME;
    }

    public String getDGENRC() {
        return DGENRC;
    }

    public void setDGENRC(String DGENRC) {
        this.DGENRC = DGENRC;
    }

    public String getDMUNIC() {
        return DMUNIC;
    }

    public void setDMUNIC(String DMUNIC) {
        this.DMUNIC = DMUNIC;
    }

    public String getDOMI() {
        return DOMI;
    }

    public void setDOMI(String DOMI) {
        this.DOMI = DOMI;
    }

    public int getCPOS() {
        return CPOS;
    }

    public void setCPOS(int CPOS) {
        this.CPOS = CPOS;
    }

    public String getTEL1() {
        return TEL1;
    }

    public void setTEL1(String TEL1) {
        this.TEL1 = TEL1;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPAGINA() {
        return PAGINA;
    }

    public void setPAGINA(String PAGINA) {
        this.PAGINA = PAGINA;
    }

    public double getLATITUD() {
        return LATITUD;
    }

    public void setLATITUD(double LATITUD) {
        this.LATITUD = LATITUD;
    }

    public double getLONGITUD() {
        return LONGITUD;
    }

    public void setLONGITUD(double LONGITUD) {
        this.LONGITUD = LONGITUD;
    }

    @Override
    public String toString() {
        return "Ikastetxeak{" +
                "CCEN='" + CCEN + '\'' +
                ", NOM='" + NOM + '\'' +
                ", NOME='" + NOME + '\'' +
                ", DGENRC='" + DGENRC + '\'' +
                ", DMUNIC='" + DMUNIC + '\'' +
                ", DOMI='" + DOMI + '\'' +
                ", CPOS=" + CPOS +
                ", TEL1='" + TEL1 + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", PAGINA='" + PAGINA + '\'' +
                ", LATITUD=" + LATITUD +
                ", LONGITUD=" + LONGITUD +
                '}';
    }
}

