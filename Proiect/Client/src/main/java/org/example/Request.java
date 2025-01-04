package org.example;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor
public class Request {
    private String username;
    private String message;
    private String email;
    private String password;
    private double latitude;
    private double longitude;
    private String city;
    private Date date;
    private String temperature;
    private String condition;
    private String action;

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
}

