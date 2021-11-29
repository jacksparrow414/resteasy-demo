package com.demo.resteasy.model;

import com.demo.resteasy.util.JPAUtil;

import javax.persistence.EntityManager;


public class AppDataRepository {

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
