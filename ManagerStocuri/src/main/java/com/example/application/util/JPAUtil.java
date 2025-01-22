package com.example.application.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myJpaUnit");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close(EntityManager em) {
        if (em != null) {
            em.close();
        }
    }

    public static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
        }
    }
}
