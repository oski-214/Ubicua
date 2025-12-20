package com.example.ejemplopl3;

import com.google.gson.annotations.SerializedName;

public class Street {

    

    @SerializedName("streetId") 
    private String street_id;

    @SerializedName("streetLength") 
    private double street_length;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("district")
    private String district;

    @SerializedName("neighborhood")
    private String neighborhood;

    @SerializedName("postalCode") 
    private String postal_code;

    @SerializedName("streetName") 
    private String street_name;

    @SerializedName("surfaceType") 
    private String surface_type;

    @SerializedName("speedLimit") 
    private int speed_limit;

    // --- GETTERS Y SETTERS ---
    
    public String getStreet_id() {
        return street_id;
    }

    public void setStreet_id(String street_id) {
        this.street_id = street_id;
    }

    public double getStreet_length() {
        return street_length;
    }

    public void setStreet_length(double street_length) {
        this.street_length = street_length;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getSurface_type() {
        return surface_type;
    }

    public void setSurface_type(String surface_type) {
        this.surface_type = surface_type;
    }

    public int getSpeed_limit() {
        return speed_limit;
    }

    public void setSpeed_limit(int speed_limit) {
        this.speed_limit = speed_limit;
    }
}
