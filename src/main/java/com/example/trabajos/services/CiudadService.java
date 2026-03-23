package com.example.trabajos.services;

import com.example.trabajos.models.Ciudad;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CiudadService {

    public List<Ciudad> obtenerTodasCiudades() {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.createQuery("FROM Ciudad ORDER BY nombreCiudad", Ciudad.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public List<Ciudad> obtenerCiudadesPorMunicipio(Municipio municipio) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "SELECT c FROM Ciudad c WHERE c.municipio = :municipio ORDER BY c.nombreCiudad",
                            Ciudad.class)
                    .setParameter("municipio", municipio)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public List<Ciudad> obtenerCiudadesPorMunicipioId(int idMunicipio) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "SELECT c FROM Ciudad c WHERE c.municipio.idMunicipio = :idMunicipio ORDER BY c.nombreCiudad",
                            Ciudad.class)
                    .setParameter("idMunicipio", idMunicipio)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Ciudad obtenerCiudadPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.find(Ciudad.class, id);
        } finally {
            entityManager.close();
        }
    }

    public Ciudad obtenerCiudadPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Ciudad c WHERE c.nombreCiudad = :nombre",
                            Ciudad.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void guardarCiudad(Ciudad ciudad) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(ciudad);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar ciudad: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }

    public void actualizarCiudad(Ciudad ciudad) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(ciudad);
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

    public void eliminarCiudad(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Ciudad ciudad = entityManager.find(Ciudad.class, id);
            if (ciudad != null) {
                entityManager.remove(ciudad);
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
