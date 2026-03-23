package com.example.trabajos.services;

import com.example.trabajos.models.Nacionalidad;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class NacionalidadService {

    public List<Nacionalidad> obtenerTodasNacionalidades() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM Nacionalidad", Nacionalidad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Nacionalidad obtenerNacionalidadPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Nacionalidad.class, id);
        } finally {
            em.close();
        }
    }

    public Nacionalidad obtenerNacionalidadPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM Nacionalidad n WHERE n.nombreNacionalidad = :nombre",
                            Nacionalidad.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Si no existe, crear uno nuevo
            if (nombre != null) {
                Nacionalidad nacionalidad = new Nacionalidad(nombre);
                guardarNacionalidad(nacionalidad);
                return nacionalidad;
            }
            return null;
        } finally {
            em.close();
        }
    }

    public void guardarNacionalidad(Nacionalidad nacionalidad) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(nacionalidad);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}