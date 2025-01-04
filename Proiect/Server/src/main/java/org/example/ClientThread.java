package org.example;

import com.google.gson.Gson;
import org.example.data_source.dao.LocationDao;
import org.example.data_source.dao.LocationWeatherDao;
import org.example.data_source.dao.UserDao;
import org.example.data_source.dao.WeatherDao;
import org.example.data_source.model.*;
import org.example.network.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClientThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final UserDao userDao;
    private WeatherDao weatherDao = new WeatherDao();  // Instanțierea WeatherDao
    private LocationDao locationDao = new LocationDao();  // Instanțierea LocationDao

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

        // Aplica hash-ul pe parola
        String hashedPassword = hashPassword(password);

        // Verificăm dacă utilizatorul există
        UserEntity user = userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // Email există, verificăm parola hashuită
            if (user.getPassword().equals(hashedPassword)) {
                // Verificăm dacă utilizatorul are rolul de admin
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getId() == 2); // 2 este id-ul pentru "admin"

                if (isAdmin) {
                    // Dacă este admin, trimitem mesajul pentru admin
                    sendResponse("Hello, you are ADMIN", "");
                } else {
                    // Dacă este user, continuăm logica normală
                    if (request.getLatitude() == 0.0 && request.getLongitude() == 0.0) {
                        // Login reușit, trimitem mesajul de bun venit
                        sendResponse("Login successful. Welcome, " + user.getUsername() + "!", "");
                    } else {
                        // Căutăm locația cea mai apropiată pe baza coordonatelor
                        LocationEntity closestLocation = findClosestLocation(request.getLatitude(), request.getLongitude());
                        if (closestLocation != null) {
                            // Căutăm vremea pentru locația respectivă pentru zilele următoare
                            List<WeatherEntity> weatherList = weatherDao.findWeatherByLocation(closestLocation.getIdLoc());

                            // Sortăm lista de vreme în funcție de dată (în ordine crescătoare)
                            Collections.sort(weatherList, new Comparator<WeatherEntity>() {
                                @Override
                                public int compare(WeatherEntity w1, WeatherEntity w2) {
                                    // Comparăm datele din cele două obiecte WeatherEntity
                                    return w1.getDate().compareTo(w2.getDate());
                                }
                            });

                            // Verificăm dacă există vreme pentru 3 zile
                            StringBuilder weatherInfo = new StringBuilder();
                            weatherInfo.append("Server: ").append(closestLocation.getCity()).append("\n"); // Adăugăm orașul
                            for (WeatherEntity weather : weatherList) {
                                // Afișăm doar vremea pentru ziua curentă și următoarele 2 zile
                                if (isWeatherForNextDays(weather.getDate())) {
                                    weatherInfo.append("Date: ").append(weather.getDate())
                                            .append(" | Temp: ").append(weather.getTemperature())
                                            .append(" | Condition: ").append(weather.getCondition())
                                            .append("\n");
                                }
                            }

                            if (weatherInfo.length() > 0) {
                                sendResponse("", weatherInfo.toString());
                            } else {
                                sendResponse("No weather data available for the next 3 days.", "");
                            }
                        } else {
                            sendResponse("Location not found for the given coordinates.", "");
                        }
                    }
                }
            } else {
                // Parolă incorectă, trimitem mesaj și oprim clientul
                sendResponse("Incorrect password.", "");
                closeConnection(); // Închidem conexiunea pentru client
            }
        } else {
            // Emailul nu există, creăm un utilizator nou
            UserEntity newUser = new UserEntity();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);  // Salvăm parola hashuită

            // Salvăm noul utilizator
            userDao.save(newUser);

            // Adăugăm utilizatorul în tabelul de relații user-role
            AppUsersRolesEntity appUsersRolesEntity = new AppUsersRolesEntity();
            appUsersRolesEntity.setAppUserId(newUser.getId());  // Asigurăm că ID-ul este corect
            appUsersRolesEntity.setRoleId(1);  // 1 reprezintă rolul de 'user'

            // Salvăm relația în tabelul user-role
            userDao.saveAppUsersRoles(appUsersRolesEntity);

            // Trimitem mesaj de creare cont
            sendResponse("Account created successfully. Welcome, " + request.getUsername() + "!", "");
        }
    }

    // Verifică dacă vremea este pentru zilele curente și următoarele două zile
    private boolean isWeatherForNextDays(LocalDate requestedDate) {
        LocalDate currentDate = LocalDate.now();
        return !requestedDate.isBefore(currentDate) && requestedDate.isBefore(currentDate.plusDays(3));
    }

    private LocationEntity findClosestLocation(double userLatitude, double userLongitude) {
        // Obținem toate locațiile din baza de date
        LocationDao locationDao = new LocationDao();
        List<LocationEntity> locations = locationDao.findAll();  // Aici poți să folosești metoda care returnează toate locațiile

        // Variabile pentru locația cea mai apropiată
        LocationEntity closestLocation = null;
        double minDistance = Double.MAX_VALUE;

        // Căutăm locația cea mai apropiată
        for (LocationEntity location : locations) {
            // Calculăm distanța între locația utilizatorului și fiecare locație
            double distance = GeoUtils.calculateDistance(userLatitude, userLongitude, location.getLatitude(), location.getLongitude());

            // Dacă distanța calculată este mai mică decât distanța minimă, actualizăm locația cea mai apropiată
            if (distance < minDistance) {
                minDistance = distance;
                closestLocation = location;
            }
        }

        return closestLocation;
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Functie pentru a crea hash-ul parolei
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
