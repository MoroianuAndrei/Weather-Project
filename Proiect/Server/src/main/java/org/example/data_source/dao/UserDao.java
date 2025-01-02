package org.example.data_source.dao;

import jakarta.persistence.TypedQuery;
import org.example.data_source.Connection;
import org.example.data_source.model.UserEntity;

import java.util.List;

public class UserDao {
    private Connection connection = new Connection();

    public UserDao(String persistenceUnit) {
        this.connection.initTransaction(persistenceUnit);
    }

    /**
     * Finds all UserEntity records in the database.
     *
     * @return a list of UserEntity
     */
// findAll method
    public List<UserEntity> findAll() {
        return connection.executeReturningTransaction(entityManager -> {
            TypedQuery<UserEntity> query = entityManager.createQuery("SELECT e FROM UserEntity e", UserEntity.class);
            return query.getResultList();
        });
    }
    /**
     * Saves a new UserEntity to the database.
     *
     * @param user the user to save
     */
    public void save(UserEntity user) {
        connection.executeVoidTransaction(entityManager -> entityManager.persist(user));
    }
}
