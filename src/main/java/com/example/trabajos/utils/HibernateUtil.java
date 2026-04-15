package com.example.trabajos.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static EntityManagerFactory entityManagerFactory;
    private static final String PERSISTENCE_UNIT_NAME = "com.example.trabajos";

    static {
        try {
            System.out.println("🔄 Intentando crear EntityManagerFactory para: " + PERSISTENCE_UNIT_NAME);
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println("✅ EntityManagerFactory creado exitosamente");

            EntityManager testEm = entityManagerFactory.createEntityManager();
            testEm.close();
            System.out.println("✅ EntityManager test exitoso");

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO al crear EntityManagerFactory:");
            System.err.println("Persistence Unit: " + PERSISTENCE_UNIT_NAME);
            System.err.println("Mensaje de error: " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "Desconocida"));
            e.printStackTrace();

            System.exit(1);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory no está inicializado. Verifica tu configuración de persistencia.");
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("✅ EntityManagerFactory cerrado");
        }
    }
}