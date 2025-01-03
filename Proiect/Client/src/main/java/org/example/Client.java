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
        try {
            Socket socket = new Socket("localhost", PORT);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            // Trimitem date către server
            new Thread(() -> {
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

                try {
                    this.out.writeObject(requestJson);  // Trimitem cererea
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Primim răspunsul de la server
                try {
                    String serverData = (String) this.in.readObject();
                    Request response = new Gson().fromJson(serverData, Request.class);

                    // Afișăm mesajul de succes al logării
                    System.out.println("Server: " + response.getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                // Introducem coordonatele
                System.out.println("Insert your latitude: ");
                double latitude = scanner.nextDouble();
                System.out.println("Insert your longitude: ");
                double longitude = scanner.nextDouble();

                // Trimitem coordonatele
                request.setLatitude(latitude);
                request.setLongitude(longitude);

                try {
                    this.out.writeObject(new Gson().toJson(request));  // Trimitem coordonatele
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Primim informațiile meteo
                try {
                    String serverData = (String) this.in.readObject();
                    Request response2 = new Gson().fromJson(serverData, Request.class);
                    System.out.println("Server: " + response2.getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
