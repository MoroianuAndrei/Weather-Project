package org.example.network;

public class Request {
    private String username;
    private String message;
    private String email;
    private String password;
    private double latitude;  // Adăugat
    private double longitude; // Adăugat

    // Constructor implicit
    public Request() {
    }

    // Constructor cu toate câmpurile
    public Request(String username, String message, String email, String password, double latitude, double longitude) {
        this.username = username;
        this.message = message;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Request(String username, String message, String email, String password) {
        this.username = username;
        this.message = message;
        this.email = email;
        this.password = password;
    }

    // Getter și setter pentru username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter și setter pentru message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter și setter pentru email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter și setter pentru password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter și setter pentru latitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter și setter pentru longitude
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}