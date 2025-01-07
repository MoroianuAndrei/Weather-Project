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

    public void addWeather(WeatherEntity weather) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(weather);  // Adaugă informațiile meteo în baza de date
            entityManager.flush();  // Forțează salvarea imediată
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();  // Revocă tranzacția în caz de eroare
            }
            throw e;
        }
    }

    public void deleteWeatherByLocation(int locationId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("DELETE FROM WeatherEntity w WHERE w.location.idLoc = :locationId");
            query.setParameter("locationId", locationId);
            query.executeUpdate(); // Execută ștergerea
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback(); // Revocă tranzacția în caz de eroare
            }
            throw e;
        }
    }

    public WeatherEntity findWeatherByLocationAndDate(int locationId, String date) {
        return entityManager.createQuery("SELECT w FROM WeatherEntity w WHERE w.location.idLoc = :locationId AND w.date = :date", WeatherEntity.class)
                .setParameter("locationId", locationId)
                .setParameter("date", date)
                .getSingleResult();
    }
}