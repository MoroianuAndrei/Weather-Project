package org.example.data_source.dao;

import org.example.data_source.model.UserEntity;
import org.example.data_source.model.AppUsersRolesEntity;

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

    public void saveAppUsersRoles(AppUsersRolesEntity appUsersRolesEntity) {
        entityManager.getTransaction().begin();  // Începe tranzacția
        entityManager.persist(appUsersRolesEntity);
        entityManager.getTransaction().commit();  // Comite tranzacția
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
