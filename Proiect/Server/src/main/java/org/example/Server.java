package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int SERVER_PORT = 6543;
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            while(true) {
                Socket client = serverSocket.accept();
                new ClientThread(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
