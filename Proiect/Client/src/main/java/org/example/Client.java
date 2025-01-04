package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private final int PORT = 6543;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void start() {
        Gson gson = new GsonBuilder()
                .create(); // No need for a LocalDateAdapter

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

            Request request = new Request(username, "", email, password, 0.0, 0.0);
            request.setAction("login");
            sendRequestToServer(request);

            String serverData = (String) this.in.readObject();
            Request response = gson.fromJson(serverData, Request.class);

            System.out.println("Server: " + response.getMessage());

            if (response.getMessage().contains("Incorrect password")) {
                System.out.println("Connection closing due to incorrect password.");
                return;
            }

            // Dacă utilizatorul este admin, afișăm meniul admin
            if (response.getMessage().toLowerCase().contains("add_location or add_weather")) {
                System.out.println("Debug: Admin detected, showing menu.");
                showAdminMenu(scanner);
            } else {
                handleUserLogin(scanner, request);
            }
        } catch (IOException e) {
            System.err.println("Error during communication with the server: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Class not found while processing server response.");
            e.printStackTrace();
        }
    }

    private void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add new location");
            System.out.println("2. Add weather data");
            System.out.println("3. Exit");

            int choice = 0;
            boolean validChoice = false;

            while (!validChoice) {
                try {
                    choice = scanner.nextInt();
                    if (choice >= 1 && choice <= 3) {
                        validChoice = true;
                    } else {
                        System.out.println("Invalid choice. Please select a valid option.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.next();
                }
            }

            switch (choice) {
                case 1:
                    System.out.println("[Client] Admin selected option: 1");
                    addNewLocation(scanner);
                    break;
                case 2:
                    System.out.println("[Client] Admin selected option: 2");
                    addWeatherData(scanner);
                    break;
                case 3:
                    System.out.println("Exiting admin menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleUserLogin(Scanner scanner, Request request) {
        while (true) {
            System.out.println("Insert your latitude: ");
            double latitude = scanner.nextDouble();
            System.out.println("Insert your longitude: ");
            double longitude = scanner.nextDouble();

            request.setLatitude(latitude);
            request.setLongitude(longitude);
            request.setAction("verifystatus");

            sendRequestToServer(request); // Trimite cererea cu acțiunea corectă

            try {
                String serverData = (String) this.in.readObject();
                Request response = new Gson().fromJson(serverData, Request.class);
                System.out.println("Server: " + response.getMessage());

                // Verifică dacă răspunsul este valid și iese din loop
                if (response.getMessage().contains("Success") || response.getMessage().contains("No weather data available")) {
                    break; // Iesi din loop la succes
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNewLocation(Scanner scanner) {
        scanner.nextLine(); // Consume the newline character

        System.out.println("Enter city name: ");
        String city = scanner.nextLine();

        // Citirea latitudinii cu verificarea corectitudinii
        double latitude = getValidDoubleInput(scanner, "Enter latitude: ");

        // Citirea longitudinii cu verificarea corectitudinii
        double longitude = getValidDoubleInput(scanner, "Enter longitude: ");

        // Creăm cererea pentru adăugarea locației
        Request request = new Request();
        request.setAction("add_location");
        request.setCity(city);
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        sendRequestToServer(request);
    }

    private void addWeatherData(Scanner scanner) {
        scanner.nextLine(); // Consumă newline-ul rămas

        System.out.println("Enter city name: ");
        String city = scanner.nextLine();

        System.out.println("Enter latitude: ");
        double latitude = scanner.nextDouble();

        System.out.println("Enter longitude: ");
        double longitude = scanner.nextDouble();

        Date date = null;
        while (date == null) {
            System.out.println("Enter date (YYYY-MM-DD): ");
            String dateString = scanner.next();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
            }
        }

        System.out.println("Enter temperature: ");
        String temperature = scanner.next();

        System.out.println("Enter weather condition: ");
        String condition = scanner.next();

        Request request = new Request();
        request.setAction("add_weather");
        request.setCity(city);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setDate(date);
        request.setTemperature(temperature);
        request.setCondition(condition);

        sendRequestToServer(request);
    }

    // Metoda pentru citirea unui număr valid de tip double
    private double getValidDoubleInput(Scanner scanner, String prompt) {
        double inputValue = 0.0;
        boolean validInput = false;
        while (!validInput) {
            System.out.println(prompt);
            try {
                inputValue = scanner.nextDouble();
                validInput = true; // Valoare validă introdusă, ieșim din buclă
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number for latitude/longitude.");
                scanner.nextLine(); // Consumă linia curentă pentru a evita bucla infinită
            }
        }
        return inputValue;
    }

    private void sendRequestToServer(Request request) {
        try {
            if (request.getAction() == null || request.getAction().isEmpty()) {
                System.out.println("[Client] Error: Missing action in request.");
                return;
            }

            System.out.println("[Client] Sending request to server: " + request.getAction());
            Gson gson = new Gson();

            String requestJson = gson.toJson(request);
            this.out.writeObject(requestJson);
            this.out.flush();

        } catch (IOException e) {
            System.err.println("[Client] Error during communication with the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}