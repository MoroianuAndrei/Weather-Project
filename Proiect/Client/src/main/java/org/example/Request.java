package org.example;

import com.google.gson.Gson;

public class Request {
    private String username;
    private String message;
    private String email;
    private String password;
    private double latitude;  // Adăugat
    private double longitude; // Adăugat

    public Request() {
        // Constructor implicit
    }

    public Request(String username, String message, String email, String password, double latitude, double longitude) {
        this.username = username;
        this.message = message;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}

