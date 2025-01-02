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

                while (true) {
                    System.out.println("Insert your username: ");
                    String username = scanner.nextLine();
                    System.out.println("Insert your email: ");
                    String email = scanner.nextLine();
                    System.out.println("Insert your password: ");
                    String password = scanner.nextLine();
                    System.out.println("Insert your message: ");
                    String message = scanner.nextLine();

                    Request request = new Request(username, message, email, password);
                    String requestJson = new Gson().toJson(request);

                    try {
                        this.out.writeObject(requestJson);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            // Primim răspuns de la server
            new Thread(() -> {
                while (true) {
                    try {
                        String serverData = (String) this.in.readObject();
                        Request request = new Gson().fromJson(serverData, Request.class);

                        System.out.println(request.getUsername() + ": " + request.getMessage());
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
