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
        Gson gson = new GsonBuilder().create();
        Socket socket = null;

        try {
            socket = new Socket("localhost", PORT);
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
            } while (!isValidEmail(email));

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

            if (response.getMessage().toLowerCase().contains("add_location or add_weather")) {
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
        } finally {
            closeResources();
        }
    }

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

            int choice = getValidMenuChoice(scanner, 1, 5);

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
            }
        }
    }

    private void deleteLocation(Scanner scanner) {
        scanner.nextLine();
        System.out.println("Enter the city name of the location to delete: ");
        String city = scanner.nextLine();

        Request request = new Request();
        request.setAction("delete_location");
        request.setCity(city);

        sendRequestToServer(request);
    }

    private void deleteWeatherData(Scanner scanner) {
        scanner.nextLine();
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

            int choice = getValidMenuChoice(scanner, 1, 2);

            switch (choice) {
                case 1:
                    insertCoordinates(scanner, request);
                    break;
                case 2:
                    System.out.println("Exiting user menu.");
                    return;
            }
        }
    }

    private void insertCoordinates(Scanner scanner, Request request) {
        double latitude = getValidDoubleInput(scanner, "Insert your latitude: ");
        double longitude = getValidDoubleInput(scanner, "Insert your longitude: ");

        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setAction("verifystatus");

        sendRequestToServer(request);

        try {
            String serverData = (String) this.in.readObject();
            Request response = new Gson().fromJson(serverData, Request.class);

            System.out.println("Server: " + response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addNewLocation(Scanner scanner) {
        scanner.nextLine();

        System.out.println("Enter city name: ");
        String city = scanner.nextLine();

        double latitude = getValidDoubleInput(scanner, "Enter latitude: ");
        double longitude = getValidDoubleInput(scanner, "Enter longitude: ");

        Request request = new Request();
        request.setAction("add_location");
        request.setCity(city);
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        sendRequestToServer(request);
    }

    private void addWeatherData(Scanner scanner) {
        scanner.nextLine();

        System.out.println("Enter city name: ");
        String city = scanner.nextLine();

        double latitude = getValidDoubleInput(scanner, "Enter latitude: ");
        double longitude = getValidDoubleInput(scanner, "Enter longitude: ");

        Date date = getValidDateInput(scanner);

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

    private Date getValidDateInput(Scanner scanner) {
        Date date = null;
        while (date == null) {
            System.out.println("Enter date (YYYY-MM-DD): ");
            String dateString = scanner.next();
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
            }
        }
        return date;
    }

    private int getValidMenuChoice(Scanner scanner, int min, int max) {
        int choice = 0;
        boolean valid = false;

        while (!valid) {
            try {
                choice = scanner.nextInt();
                if (choice >= min && choice <= max) {
                    valid = true;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }

        return choice;
    }

    private double getValidDoubleInput(Scanner scanner, String prompt) {
        double inputValue = 0.0;
        boolean valid = false;

        while (!valid) {
            System.out.println(prompt);
            try {
                inputValue = scanner.nextDouble();
                valid = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
        }

        return inputValue;
    }

    private void sendRequestToServer(Request request) {
        try {
            if (request.getAction() == null || request.getAction().isEmpty()) {
                return;
            }

            Gson gson = new Gson();
            String requestJson = gson.toJson(request);

            this.out.writeObject(requestJson);
            this.out.flush();
        } catch (IOException e) {
            System.err.println("Error during communication with the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
