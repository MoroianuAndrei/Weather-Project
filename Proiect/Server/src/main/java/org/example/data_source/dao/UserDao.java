package org.example.data_source.dao;

import org.example.data_source.model.UserEntity;

import jakarta.persistence.*;
import java.util.List;

public class UserDao {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    // Constructorul clasei
    public UserDao(String persistenceUnitName) {
        // Inițializează EntityManagerFactory
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    // Obține EntityManager-ul
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    // Metoda pentru a salva un utilizator
    public void save(UserEntity user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    // Metoda pentru a obține toți utilizatorii
    public List<UserEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
    }

    // Închide EntityManager-ul la finalul utilizării
    public void close() {
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
