package com.example.trabajos.services;

import com.example.trabajos.models.Genero;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class GeneroService {

    public List<Genero> obtenerTodosGeneros() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM Genero", Genero.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Genero obtenerGeneroPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Genero.class, id);
        } finally {
            em.close();
        }
    }

    public Genero obtenerGeneroPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT g FROM Genero g WHERE g.tipoGenero = :nombre",
                            Genero.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Si no existe, crear uno nuevo
            if (nombre != null) {
                Genero genero = new Genero(nombre);
                guardarGenero(genero);
                return genero;
            }
            return null;
        } finally {
            em.close();
        }
    }

    public void guardarGenero(Genero genero) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(genero);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}