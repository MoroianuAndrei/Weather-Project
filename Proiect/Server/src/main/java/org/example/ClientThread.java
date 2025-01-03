package org.example;

import com.google.gson.Gson;
import org.example.data_source.dao.UserDao;
import org.example.data_source.model.AppUsersRolesEntity;
import org.example.data_source.model.RoleEntity;
import org.example.data_source.model.UserEntity;
import org.example.network.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
                String message = (String) this.in.readObject();
                Request request = new Gson().fromJson(message, Request.class);

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

        UserEntity user = userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // Email exists, check password
            if (user.getPassword().equals(password)) {
                sendResponse("Login successful. Welcome, " + user.getUsername() + "!");
            } else {
                sendResponse("Incorrect password. Please try again.");
            }
        } else {
            // Email does not exist, create a new user
            UserEntity newUser = new UserEntity();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(email);
            newUser.setPassword(password);

            // Save the new user
            userDao.save(newUser);

            // After saving the user, add the user to app_users_roles with role_id = 1 (for 'user' role)
            AppUsersRolesEntity appUsersRolesEntity = new AppUsersRolesEntity();
            appUsersRolesEntity.setAppUserId(newUser.getId());  // Ensure this ID is properly set after persisting the user
            appUsersRolesEntity.setRoleId(1);  // 1 represents the 'user' role

            // Save the relation in app_users_roles
            System.out.println("Saving user-role relation: userId = " + newUser.getId() + ", roleId = 1");
            userDao.saveAppUsersRoles(appUsersRolesEntity);

            sendResponse("Account created successfully. Welcome, " + request.getUsername() + "!");
        }
    }

    private void sendResponse(String message) {
        Request response = new Request("Server", message, null, null);
        try {
            this.out.writeObject(new Gson().toJson(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
