package com.example.trabajos.services;

import com.example.trabajos.models.Idioma;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class IdiomaService {

    public List<Idioma> obtenerTodosIdiomas() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM Idioma", Idioma.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Idioma obtenerIdiomaPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Idioma.class, id);
        } finally {
            em.close();
        }
    }

    public Idioma obtenerIdiomaPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Idioma> lista = em.createQuery(
                            "SELECT i FROM Idioma i WHERE i.nombreIdioma = :nombre",
                            Idioma.class)
                    .setParameter("nombre", nombre)
                    .getResultList();

            if (!lista.isEmpty()) {
                return lista.get(0);
            }

            // Si no existe → crear automáticamente
            Idioma nuevo = new Idioma(nombre);
            guardarIdioma(nuevo);
            return nuevo;

        } finally {
            em.close();
        }
    }

    public void guardarIdioma(Idioma idioma) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(idioma);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
