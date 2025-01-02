package org.example;

import org.example.data_source.Connection;
import org.example.data_source.dao.UserDao;
import org.example.data_source.model.UserEntity;

public class App {
    public static void main(String[] args) {
        new Server().start();
    }
}
