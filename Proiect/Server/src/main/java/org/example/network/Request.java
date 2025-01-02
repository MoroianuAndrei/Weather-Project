package org.example.network;

public class Request {
    private String username;
    private String message;
    private String email;
    private String password;

    public Request() {
        // Constructor implicit
    }

    public Request(String username, String message, String email, String password) {
        this.username = username;
        this.message = message;
        this.email = email;
        this.password = password;
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
}
