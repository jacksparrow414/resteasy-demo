package com.demo.resteasy.model;

import com.demo.resteasy.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class AppDataRepository {

//    @PersistenceContext
    /**
     * Tomcat不支持对JPA进行CDI注入，只能手动创建
     * https://www.linkedin.com/pulse/getting-advantage-java-8-using-eclipselink-tomcat-se-nicola-cogotti/
     */
    private EntityManager entityManager;

    public AppDataRepository() {
        this.entityManager = JPAUtil.getEntityManager();
    }

    public Client getClient(String clientId) {
        return entityManager.find(Client.class, clientId);
    }

    public User getUser(String userId) {
        return entityManager.find(User.class, userId);
    }

    public AuthorizationCode save(AuthorizationCode authorizationCode) {
        entityManager.persist(authorizationCode);
        return authorizationCode;
    }
}