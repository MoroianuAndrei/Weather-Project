package org.example.data_source.dao;

import jakarta.persistence.*;
import org.example.data_source.model.LocationWeatherEntity;

import java.util.List;

public class LocationWeatherDao {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public LocationWeatherDao() {
        // Inițializează EntityManagerFactory și EntityManager
        this.entityManagerFactory = Persistence.createEntityManagerFactory("postgresPersistence");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public void save(LocationWeatherEntity locationWeather) {
        entityManager.getTransaction().begin();
        entityManager.persist(locationWeather);
        entityManager.getTransaction().commit();
    }

    public LocationWeatherEntity findById(int id) {
        return entityManager.find(LocationWeatherEntity.class, id);
    }

    public List<LocationWeatherEntity> findAll() {
        return entityManager.createQuery("SELECT l FROM LocationWeatherEntity l", LocationWeatherEntity.class)
                .getResultList();
    }

    public void update(LocationWeatherEntity locationWeatherEntity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(locationWeatherEntity); // Actualizăm entitatea
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update location weather", e);
        }
    }

    public void close() {
        // Închide EntityManager și EntityManagerFactory
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
