package com.example.maruta.uberfirebase;

public class UserLocation {

    private double latitude;
    private double longitude;
    private boolean isAvailable;

    public UserLocation(){}//for firebase

    public UserLocation(double la, double lo, boolean v){

        this.latitude = la;
        this.longitude = lo;
        isAvailable = v;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Latitude " + this.latitude + " Longitude " + this.longitude + " Available " + this.isAvailable;
    }
}
