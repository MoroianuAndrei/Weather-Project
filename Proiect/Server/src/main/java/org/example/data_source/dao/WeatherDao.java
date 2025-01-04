package org.example.data_source.dao;

import org.example.data_source.model.WeatherEntity;
import jakarta.persistence.*;
import java.util.List;

public class WeatherDao {

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    public WeatherDao() {
        // Inițializarea EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("postgresPersistence");
        entityManager = emf.createEntityManager();
    }

    public List<WeatherEntity> findWeatherByLocation(int locationId) {
        return entityManager.createQuery("SELECT w FROM WeatherEntity w WHERE w.location.idLoc = :locationId", WeatherEntity.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    public WeatherEntity findWeatherByLocationAndDate(int locationId, String date) {
        return entityManager.createQuery("SELECT w FROM WeatherEntity w WHERE w.location.idLoc = :locationId AND w.date = :date", WeatherEntity.class)
                .setParameter("locationId", locationId)
                .setParameter("date", date)
                .getSingleResult();
    }
}