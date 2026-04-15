package com.example.trabajos.services;

import com.example.trabajos.models.Municipio;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MunicipioService {

    public List<Municipio> obtenerTodosMunicipios() {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.createQuery("FROM Municipio ORDER BY nombreMunicipio", Municipio.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Municipio obtenerMunicipioPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.find(Municipio.class, id);
        } finally {
            entityManager.close();
        }
    }

    public Municipio obtenerMunicipioPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Municipio m WHERE m.nombreMunicipio = :nombre",
                            Municipio.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void guardarMunicipio(Municipio municipio) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(municipio);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar municipio: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }

    public void actualizarMunicipio(Municipio municipio) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(municipio);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void eliminarMunicipio(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Municipio municipio = entityManager.find(Municipio.class, id);
            if (municipio != null) {
                entityManager.remove(municipio);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
