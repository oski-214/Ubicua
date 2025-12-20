package com.example.ejemplopl3;

import com.google.gson.annotations.SerializedName;

public class Vehicle {


    @SerializedName("plate")
    private String matricula;

    @SerializedName("meanPressure")
    private double media_presiones;

    @SerializedName("typeVehicle")
    private String tipo;

    @SerializedName("distance")
    private double media_distancias;

    @SerializedName("streetId")
    private String street_id;

    // --- GETTERS Y SETTERS ---

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public double getMedia_presiones() {
        return media_presiones;
    }

    public void setMedia_presiones(double media_presiones) {
        this.media_presiones = media_presiones;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMedia_distancias() {
        return media_distancias;
    }

    public void setMedia_distancias(double media_distancias) {
        this.media_distancias = media_distancias;
    }

    public String getStreet_id() {
        return street_id;
    }

    public void setStreet_id(String street_id) {
        this.street_id = street_id;
    }
}
