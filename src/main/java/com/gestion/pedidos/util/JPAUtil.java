package com.gestion.pedidos.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Nombre debe coincidir exactamente con el de persistence.xml
    private static final String PERSISTENCE_UNIT = "miUnidad";

    private static EntityManagerFactory emf;

    // Constructor privado: no se instancia
    private JPAUtil() {}

    // Retorna la instancia única del EntityManagerFactory (patrón Singleton)
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return emf;
    }

    // Cierra el factory al terminar la aplicación
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}