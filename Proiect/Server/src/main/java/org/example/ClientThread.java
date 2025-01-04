package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data_source.dao.LocationDao;
import org.example.data_source.dao.WeatherDao;
import org.example.data_source.dao.UserDao;
import org.example.data_source.model.*;
import org.example.network.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ClientThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final UserDao userDao;
    private final Gson gson;
    private final WeatherDao weatherDao = new WeatherDao();
    private final LocationDao locationDao = new LocationDao();
    private UserEntity authenticatedUser = null;  // Utilizatorul autentificat

    public ClientThread(Socket socket) {
        this.socket = socket;
        this.userDao = new UserDao("postgresPersistence");
        this.gson = new Gson();  // Nu mai este nevoie de LocalDateAdapter
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
                Request request = gson.fromJson(message, Request.class);

                // Verifică acțiunea trimisă de client
                if (request.getAction().equals("login")) {
                    // Dacă acțiunea este "login", efectuăm autentificarea
                    if (authenticatedUser == null) {
                        executeAuthentication(request);
                    }
                } else if (authenticatedUser != null) {
                    // Dacă utilizatorul este deja autentificat, efectuăm alte acțiuni
                    System.out.println("[Server] Authenticated user: " + authenticatedUser.getUsername());
                    if ("add_location".equals(request.getAction())) {
                        handleAddLocation(request);
                    } else if ("add_weather".equals(request.getAction())) {
                        handleAddWeather(request);
                    } else if ("verifystatus".equals(request.getAction())) {
                        handleUserActions(request);  // Nu mai este nevoie de `user`
                    } else {
                        sendResponse("Error", "Unknown action. Please choose either 'add_location', 'add_weather', or 'verifystatus'.", "");
                    }
                } else {
                    // Dacă utilizatorul nu este autentificat și trimite o acțiune care nu este "login"
                    sendResponse("Error", "You must log in first.", "");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeAuthentication(Request request) {
        String email = request.getEmail();
        String password = request.getPassword();

        System.out.println("[Server] Received request from email: " + email);

        String hashedPassword = hashPassword(password);
        UserEntity user = userDao.findByEmail(email);

        if (user != null) {
            System.out.println("[Server] User found: " + user.getUsername());
            if (user.getPassword().equals(hashedPassword)) {
                System.out.println("[Server] Password correct for user: " + user.getUsername());
                authenticatedUser = user;  // Păstrează utilizatorul autentificat
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getId() == 2);

                if (isAdmin) {
                    System.out.println("[Server] User is ADMIN.");
                    sendResponse("Admin Actions", "Please choose an action: add_location or add_weather.", "");
                } else {
                    System.out.println("[Server] User is regular.");
                    handleUserActions(request);
                }
            } else {
                System.out.println("[Server] Incorrect password for email: " + email);
                sendResponse("Error", "Incorrect password.", "");
                closeConnection();
            }
        } else {
            System.out.println("[Server] User not found. Creating new user with email: " + email);
            createUser(request, email, hashedPassword);
        }
    }

    private void handleAddLocation(Request request) {
        LocationEntity newLocation = new LocationEntity();
        newLocation.setCity(request.getCity());
        newLocation.setLatitude(request.getLatitude());
        newLocation.setLongitude(request.getLongitude());
        System.out.println("Locatie noua: " + newLocation.getCity() + " " + newLocation.getLatitude() + " " + newLocation.getLongitude());
        locationDao.addLocation(newLocation);
        sendResponse("Success", "New location added successfully!", "");
    }

    private void handleAddWeather(Request request) {
        LocationEntity location = locationDao.findLocationByCity(request.getCity());

        // Dacă locația nu există, o adăugăm
        if (location == null) {
            location = new LocationEntity();
            location.setCity(request.getCity());
            location.setLatitude(request.getLatitude());
            location.setLongitude(request.getLongitude());
            locationDao.addLocation(location);  // Adaugă locația
        }

        // Creăm entitatea Weather pentru a o salva
        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setLocation(location);
        weatherEntity.setDate(new Date(request.getDate().getTime())); // Conversia din LocalDate în Date
        weatherEntity.setTemperature(request.getTemperature());
        weatherEntity.setCondition(request.getCondition());

        System.out.println("Vreme noua: " + weatherEntity.getLocation().getCity() + " " + weatherEntity.getDate() + " " + weatherEntity.getTemperature() + " " + weatherEntity.getCondition());

        // Salvează vremea
        weatherDao.addWeather(weatherEntity);
        sendResponse("Success", "Weather data added successfully!", "");
    }

    private void handleUserActions(Request request) {
        // Folosește `authenticatedUser` direct, nu mai este nevoie să treci `UserEntity user` ca parametru.
        UserEntity user = authenticatedUser;

        if (request.getLatitude() == 0.0 && request.getLongitude() == 0.0) {
            sendResponse("Success", "Login successful. Welcome, " + user.getUsername() + "!", "");
        } else if ("verifystatus".equals(request.getAction())) {
            LocationEntity nearestLocation = getNearestLocation(request.getLatitude(), request.getLongitude());
            if (nearestLocation != null) {
                List<WeatherEntity> weatherList = weatherDao.findWeatherByLocation(nearestLocation.getIdLoc());

                // Sortăm lista de vreme în funcție de data în ordine crescătoare
                Collections.sort(weatherList, new Comparator<WeatherEntity>() {
                    @Override
                    public int compare(WeatherEntity w1, WeatherEntity w2) {
                        return w1.getDate().compareTo(w2.getDate());  // Comparație pe data
                    }
                });

                StringBuilder weatherInfo = new StringBuilder("Server: ").append(nearestLocation.getCity()).append("\n");

                for (WeatherEntity weather : weatherList) {
                    if (isWeatherForNextDays(weather.getDate())) {
                        weatherInfo.append("Date: ").append(weather.getDate())
                                .append(" | Temp: ").append(weather.getTemperature())
                                .append(" | Condition: ").append(weather.getCondition())
                                .append("\n");
                    }
                }

                if (weatherInfo.length() > 0) {
                    sendResponse("Success", "", weatherInfo.toString());
                } else {
                    sendResponse("Info", "No weather data available for the next 3 days.", "");
                }
            } else {
                sendResponse("Error", "No location found for the given coordinates.", "");
            }
        }
    }

    // Metodă pentru a găsi cea mai apropiată locație pe baza coordonatelor
    private LocationEntity getNearestLocation(double latitude, double longitude) {
        List<LocationEntity> allLocations = locationDao.findAll(); // Sau un alt mod de a obține locațiile din DB
        LocationEntity nearestLocation = null;
        double minDistance = Double.MAX_VALUE;

        for (LocationEntity location : allLocations) {
            double distance = GeoUtils.calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestLocation = location;
            }
        }

        return nearestLocation;
    }

    private void createUser(Request request, String email, String hashedPassword) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        userDao.save(newUser);

        AppUsersRolesEntity appUsersRolesEntity = new AppUsersRolesEntity();
        appUsersRolesEntity.setAppUserId(newUser.getId());
        appUsersRolesEntity.setRoleId(1);

        userDao.saveAppUsersRoles(appUsersRolesEntity);

        sendResponse("Success", "Account created successfully. Welcome, " + request.getUsername() + "!", "");
    }

    private boolean isWeatherForNextDays(Date requestedDate) {
        Date currentDate = new Date();
        return requestedDate.after(new Date(currentDate.getTime() - 86400000)) && requestedDate.before(new Date(currentDate.getTime() + 259200000));
    }

    private void closeConnection() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private void sendResponse(String status, String message, String weatherInfo) {
        String fullMessage = message + (weatherInfo.isEmpty() ? "" : " " + weatherInfo);
        Request response = new Request(status, fullMessage, "", "");
        try {
            this.out.writeObject(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}