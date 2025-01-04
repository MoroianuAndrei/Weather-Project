package org.example.data_source.dao;

import org.example.data_source.model.LocationEntity;
import jakarta.persistence.*;
import java.util.List;

public class LocationDao {

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    public LocationDao() {
        // Inițializare manuală a EntityManager-ului folosind unitatea de persistență definită în persistence.xml
        emf = Persistence.createEntityManagerFactory("postgresPersistence");
        entityManager = emf.createEntityManager();
    }

    // Metoda pentru a găsi o locație pe baza coordonatelor
    public LocationEntity findLocationByCoordinates(double latitude, double longitude) {
        List<LocationEntity> locations = entityManager.createQuery("SELECT l FROM LocationEntity l WHERE l.latitude = :latitude AND l.longitude = :longitude", LocationEntity.class)
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .getResultList();

        if (!locations.isEmpty()) {
            return locations.get(0);  // Returnăm prima locație găsită
        } else {
            return null;  // Nu am găsit nicio locație cu aceste coordonate
        }
    }

    // Metoda pentru a găsi o locație pe baza numelui orașului
    public LocationEntity findLocationByCity(String city) {
        List<LocationEntity> locations = entityManager.createQuery("SELECT l FROM LocationEntity l WHERE l.city = :city", LocationEntity.class)
                .setParameter("city", city)
                .getResultList();

        if (!locations.isEmpty()) {
            return locations.get(0);  // Returnăm prima locație găsită
        } else {
            return null;  // Nu am găsit nicio locație cu acest nume de oraș
        }
    }

    public void addLocation(LocationEntity location) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(location);
            entityManager.flush();  // Forțează salvarea imediată
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Metoda pentru a obține toate locațiile
    public List<LocationEntity> findAll() {
        return entityManager.createQuery("SELECT l FROM LocationEntity l", LocationEntity.class).getResultList();
    }
}
