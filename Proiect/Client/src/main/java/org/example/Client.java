package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final int PORT = 6543;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void start() {
        try (Socket socket = new Socket("localhost", PORT)) {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);

            // Introducere username, email și parolă
            System.out.println("Insert your username: ");
            String username = scanner.nextLine();
            System.out.println("Insert your email: ");
            String email = scanner.nextLine();
            System.out.println("Insert your password: ");
            String password = scanner.nextLine();

            // Creăm obiectul Request
            Request request = new Request(username, "", email, password, 0.0, 0.0);
            String requestJson = new Gson().toJson(request);

            // Trimitem cererea de login
            this.out.writeObject(requestJson);

            // Primim răspunsul de la server
            String serverData = (String) this.in.readObject();
            Request response = new Gson().fromJson(serverData, Request.class);

            // Verificăm dacă logarea a fost reușită
            if (response.getMessage().contains("Incorrect password")) {
                System.out.println("Server: " + response.getMessage());
                System.out.println("Connection closing due to incorrect password.");
                return; // Oprire execuție
            }

            // Afișăm mesajul de succes al logării
            System.out.println("Server: " + response.getMessage());

            while (true) {
                // Introducem coordonatele
                System.out.println("Insert your latitude: ");
                double latitude = scanner.nextDouble();
                System.out.println("Insert your longitude: ");
                double longitude = scanner.nextDouble();

                // Trimitem coordonatele
                request.setLatitude(latitude);
                request.setLongitude(longitude);
                this.out.writeObject(new Gson().toJson(request));

                // Primim informațiile meteo
                serverData = (String) this.in.readObject();
                Request response2 = new Gson().fromJson(serverData, Request.class);
                System.out.println("Server: " + response2.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error during communication with the server", e);
        }
    }
}
