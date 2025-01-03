package org.example;

import com.google.gson.Gson;
import org.example.data_source.dao.LocationWeatherDao;
import org.example.data_source.dao.UserDao;
import org.example.data_source.model.AppUsersRolesEntity;
import org.example.data_source.model.LocationWeatherEntity;
import org.example.data_source.model.UserEntity;
import org.example.network.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final UserDao userDao;

    public ClientThread(Socket socket) {
        this.socket = socket;
        this.userDao = new UserDao("postgresPersistence"); // Înlocuiți cu unitatea de persistență utilizată
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
                // Citim mesajul primit de la client
                String message = (String) this.in.readObject();
                Request request = new Gson().fromJson(message, Request.class);

                // Afisam informatiile primite de la client
                System.out.println("Received email: " + request.getEmail());
                System.out.println("Received password: " + request.getPassword());

                execute(request);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(Request request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Verificam daca utilizatorul exista
        UserEntity user = userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // Email exista, verificam parola
            if (user.getPassword().equals(password)) {
                // Login reusit, trimitem mesajul de bun venit
                sendResponse("Login successful. Welcome, " + user.getUsername() + "!", "");

                // Trimitem mesajul de vremea
                String weatherInfo = getWeatherInfo(request.getLatitude(), request.getLongitude());
                sendResponse("", weatherInfo); // Trimitem doar informatiile meteo
            } else {
                sendResponse("Incorrect password. Please try again.", ""); // Fara informatii meteo
            }
        } else {
            // Emailul nu exista, creem un utilizator nou
            UserEntity newUser = new UserEntity();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(email);
            newUser.setPassword(password);

            // Salvam noul utilizator
            userDao.save(newUser);

            // Adaugam utilizatorul in tabelul de relatii user-role
            AppUsersRolesEntity appUsersRolesEntity = new AppUsersRolesEntity();
            appUsersRolesEntity.setAppUserId(newUser.getId());  // Asiguram ca ID-ul este corect
            appUsersRolesEntity.setRoleId(1);  // 1 reprezinta rolul de 'user'

            // Salvam relatia in tabela user-role
            userDao.saveAppUsersRoles(appUsersRolesEntity);

            // Trimitem mesaj de creare cont
            sendResponse("Account created successfully. Welcome, " + request.getUsername() + "!", "");

            // Trimitem mesajul de vremea
            String weatherInfo = getWeatherInfo(request.getLatitude(), request.getLongitude());
            sendResponse("", weatherInfo); // Trimitem doar informatiile meteo
        }
    }

    // Simuleaza obtinerea informatiilor meteo pe baza latitudinii si longitudinii
    private String getWeatherInfo(double latitude, double longitude) {
        // Obtinem toate locatiile din baza de date
        LocationWeatherDao locationWeatherDao = new LocationWeatherDao();
        List<LocationWeatherEntity> locations = locationWeatherDao.findAll();

        // Variabile pentru locația cea mai apropiată
        LocationWeatherEntity closestLocation = null;
        double minDistance = Double.MAX_VALUE;

        // Căutăm locația cea mai apropiată
        for (LocationWeatherEntity location : locations) {
            double distance = GeoUtils.calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                closestLocation = location;
            }
        }

        // Dacă am găsit locația cea mai apropiată
        if (closestLocation != null) {
            return String.format("Weather for city %s: Temperature = %s, Condition = %s",
                    closestLocation.getCity(), closestLocation.getTemperature(), closestLocation.getCondition());
        } else {
            return "Weather information not available.";
        }
    }

    private void sendResponse(String message, String weatherInfo) {
        // Formam mesajul complet
        String fullMessage = message + (weatherInfo.isEmpty() ? "" : " " + weatherInfo);

        // Trimitem raspunsul catre client
        Request response = new Request("Server", fullMessage, "", "");  // Trimitem raspunsul
        try {
            this.out.writeObject(new Gson().toJson(response));  // Convertim in JSON si trimitem
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
