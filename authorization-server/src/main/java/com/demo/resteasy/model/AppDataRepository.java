package com.demo.resteasy.model;

import com.demo.resteasy.util.JPAUtil;

import jakarta.persistence.EntityManager;


public class AppDataRepository {

//    @PersistenceContext
    /**
     * Tomcat不支持对JPA进行CDI注入，只能手动创建
     * https://www.linkedin.com/pulse/getting-advantage-java-8-using-eclipselink-tomcat-se-nicola-cogotti/
     */
    private EntityManager entityManager;

    public AppDataRepository() {
        this.entityManager = JPAUtil.acquireEntityManager();
    }

    public Client getClient(String clientId) {
        return entityManager.find(Client.class, clientId);
    }

    public User getUser(String userId) {
        return entityManager.find(User.class, userId);
    }

    public AuthorizationCode save(AuthorizationCode authorizationCode) {
        entityManager.getTransaction().begin();
        entityManager.persist(authorizationCode);
        entityManager.getTransaction().commit();
        return authorizationCode;
    }
}
