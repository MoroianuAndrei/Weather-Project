package org.example;

import com.google.gson.Gson;
import org.example.network.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ClientThread(Socket socket) {
        this.socket = socket;
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = (String) this.in.readObject();
                Request request = new Gson().fromJson(message, Request.class);

                // Exemplu: Afișează emailul și parola
                System.out.println("Received email: " + request.getEmail());
                System.out.println("Received password: " + request.getPassword());

                // Procesăm cererea
                execute(request);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(Request request) {
        String clientMessage = request.getMessage();
        Request response;

        // Exemplu simplu de răspuns
        if ("hello".equalsIgnoreCase(clientMessage)) {
            response = new Request("Server", "Hello, " + request.getUsername(), null, null);
        } else {
            response = new Request("Server", "Message received: " + clientMessage, null, null);
        }

        try {
            this.out.writeObject(new Gson().toJson(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
