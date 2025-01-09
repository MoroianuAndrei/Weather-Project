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
import java.util.regex.Pattern;

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

            String email;
            do {
                System.out.println("Insert your email: ");
                email = scanner.nextLine();
                if (!isValidEmail(email)) {
                    System.out.println("Invalid email format. Please try again.");
                }
            } while (!isValidEmail(email)); // Se repetă până când email-ul este valid

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

    // Metoda pentru validarea email-ului
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add new location");
            System.out.println("2. Add weather data");
            System.out.println("3. Delete location");
            System.out.println("4. Delete weather data");
            System.out.println("5. Exit");

            int choice = 0;
            boolean validChoice = false;

            while (!validChoice) {
                try {
                    choice = scanner.nextInt();
                    if (choice >= 1 && choice <= 5) {
                        validChoice = true;
                    } else {
                        System.out.println("Invalid choice. Please select a valid option.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 5.");
                    scanner.next();
                }
            }

            switch (choice) {
                case 1:
                    addNewLocation(scanner);
                    break;
                case 2:
                    addWeatherData(scanner);
                    break;
                case 3:
                    deleteLocation(scanner);
                    break;
                case 4:
                    deleteWeatherData(scanner);
                    break;
                case 5:
                    System.out.println("Exiting admin menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void deleteLocation(Scanner scanner) {
        scanner.nextLine(); // Consume newline
        System.out.println("Enter the city name of the location to delete: ");
        String city = scanner.nextLine();

        Request request = new Request();
        request.setAction("delete_location");
        request.setCity(city);

        sendRequestToServer(request);
    }

    private void deleteWeatherData(Scanner scanner) {
        scanner.nextLine(); // Consume newline
        System.out.println("Enter the city name for which you want to delete weather data: ");
        String city = scanner.nextLine();

        Request request = new Request();
        request.setAction("delete_weather");
        request.setCity(city);

        sendRequestToServer(request);
    }


    private void handleUserLogin(Scanner scanner, Request request) {
        while (true) {
            System.out.println("User Menu:");
            System.out.println("1. Insert coordinates");
            System.out.println("2. Exit");

            int choice = 0;
            boolean validChoice = false;

            // Validare alegere meniu
            while (!validChoice) {
                try {
                    choice = scanner.nextInt();
                    if (choice >= 1 && choice <= 2) {
                        validChoice = true;
                    } else {
                        System.out.println("Invalid choice. Please select a valid option.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number (1 or 2).");
                    scanner.next(); // Consumă intrarea invalidă
                }
            }

            switch (choice) {
                case 1:
                    insertCoordinates(scanner, request);
                    break;
                case 2:
                    System.out.println("Exiting user menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void insertCoordinates(Scanner scanner, Request request) {
        // Inserare latitudine
        double latitude = getValidDoubleInput(scanner, "Insert your latitude: ");

        // Inserare longitudine
        double longitude = getValidDoubleInput(scanner, "Insert your longitude: ");

        // Actualizare cerere cu coordonatele introduse
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setAction("verifystatus");

        // Trimitere cerere către server
        sendRequestToServer(request);

        try {
            String serverData = (String) this.in.readObject();
            Request response = new Gson().fromJson(serverData, Request.class);

            // Afișare răspuns de la server
            System.out.println("Server: " + response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
//                System.out.println("[Client] Error: Missing action in request.");
                return;
            }

//            System.out.println("[Client] Sending request to server: " + request.getAction());
            Gson gson = new Gson();

            String requestJson = gson.toJson(request);
            this.out.writeObject(requestJson);
            this.out.flush();

        } catch (IOException e) {
//            System.err.println("[Client] Error during communication with the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}