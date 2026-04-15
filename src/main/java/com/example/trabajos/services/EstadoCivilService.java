package com.example.trabajos.services;

import com.example.trabajos.models.EstadoCivil;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class EstadoCivilService {

    public List<EstadoCivil> obtenerTodosEstadosCiviles() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM EstadoCivil", EstadoCivil.class).getResultList();
        } finally {
            em.close();
        }
    }

    public EstadoCivil obtenerEstadoCivilPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(EstadoCivil.class, id);
        } finally {
            em.close();
        }
    }

    public EstadoCivil obtenerEstadoCivilPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM EstadoCivil e WHERE e.estadoCivil = :nombre",
                            EstadoCivil.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Si no existe, crear uno nuevo
            if (nombre != null) {
                EstadoCivil estadoCivil = new EstadoCivil(nombre);
                guardarEstadoCivil(estadoCivil);
                return estadoCivil;
            }
            return null;
        } finally {
            em.close();
        }
    }

    public void guardarEstadoCivil(EstadoCivil estadoCivil) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(estadoCivil);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}