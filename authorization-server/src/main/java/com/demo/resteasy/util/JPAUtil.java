package com.demo.resteasy.util;

import lombok.extern.java.Log;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Level;

/**
 * Tomcat不会引导JPA，所以需要手动引导JPA
 */
@Log
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "restEasy";

    private static EntityManagerFactory entityManagerFactory;

    public static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    private JPAUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 引导JPA
     */
    public static void bootstrapJPA() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Create entity manager factory failure for persistence unit " + PERSISTENCE_UNIT_NAME, e);
        }
    }

    public static void shutdownEntityManagerFactory() {
        entityManagerFactory.close();
        log.info("shutdown EntityManagerFactory");
    }

    /**
     * EntityManager
     * @return
     */
    public static EntityManager acquireEntityManager(){
        EntityManager manager = threadLocal.get();
        if(manager == null || !manager.isOpen()){
            manager = entityManagerFactory.createEntityManager();
            threadLocal.set(manager);
        }
        return manager;
    }
}
