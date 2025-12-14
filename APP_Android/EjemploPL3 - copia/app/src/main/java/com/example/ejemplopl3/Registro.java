package com.example.ejemplopl3;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Modelo de datos para un objeto 'Registro'.
 * Las anotaciones @SerializedName se aseguran de que los campos del JSON
 * se mapeen correctamente a las variables de esta clase.
 */
public class Registro {

    @SerializedName("typeVehicle")
    private String type_vehicle;

    @SerializedName("plate")
    private String plate;

    @SerializedName("streetId")
    private String street_id;

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("carCount")
    private int car_count;

    @SerializedName("truckCount")
    private int truck_count;

    @SerializedName("bicycleCount")
    private int bicycle_count;

    @SerializedName("gasCount")
    private int gas_count;

    @SerializedName("ecoCount")
    private int eco_count;

    @SerializedName("technology")
    private String technology;

    // --- GETTERS ---
    // (No es necesario añadir Setters si solo vas a consumir datos)

    public String getType_vehicle() {
        return type_vehicle;
    }

    public String getPlate() {
        return plate;
    }

    public String getStreet_id() {
        return street_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getCar_count() {
        return car_count;
    }

    public int getTruck_count() {
        return truck_count;
    }

    public int getBicycle_count() {
        return bicycle_count;
    }

    public int getGas_count() {
        return gas_count;
    }

    public int getEco_count() {
        return eco_count;
    }

    public String getTechnology() {
        return technology;
    }
}
