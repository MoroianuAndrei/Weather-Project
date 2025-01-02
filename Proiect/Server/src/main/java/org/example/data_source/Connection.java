package org.example.data_source;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;
import java.util.function.Function;

public class Connection {
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public Connection() {
        // Default constructor for flexibility
    }

    public Connection(String persistenceUnit) {
        initTransaction(persistenceUnit);
    }

    public EntityManager getEntityManager() {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager has not been initialized. Call initTransaction first.");
        }
        return entityManager;
    }

    /**
     * Executes a query transaction that returns a result.
     *
     * @param action the query logic
     * @param <T>    the type of the result
     * @return the query result
     */
    public <T> T executeReturningTransaction(Function<EntityManager, T> action) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager is not initialized. Call initTransaction first.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T result = action.apply(entityManager);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Executes a query transaction that does not return a result.
     *
     * @param action the query logic
     */
    public void executeVoidTransaction(Consumer<EntityManager> action) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager is not initialized. Call initTransaction first.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Initializes the EntityManager with the given persistence unit.
     *
     * @param persistenceUnit the persistence unit name
     */
    public void initTransaction(String persistenceUnit) {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
            entityManager = entityManagerFactory.createEntityManager();
        } catch (Exception e) {
            System.err.println("Error initializing DatabaseManager: " + e.getMessage());
            throw new IllegalStateException("Failed to initialize EntityManager with persistence unit: " + persistenceUnit, e);
        }
    }

    /**
     * Closes the EntityManager and EntityManagerFactory if they are open.
     */
    public void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
