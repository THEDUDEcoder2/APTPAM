package com.example.trabajos.services;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class TrabajadorService {

    public void guardarTrabajador(Trabajador trabajador) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(trabajador);
            entityManager.getTransaction().commit();
            System.out.println("✅ Trabajador guardado: " + trabajador.getNombre());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar trabajador: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }

    public List<Trabajador> obtenerTodosTrabajadores() {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.createQuery("FROM Trabajador", Trabajador.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Trabajador obtenerTrabajadorPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            return entityManager.find(Trabajador.class, id);
        } finally {
            entityManager.close();
        }
    }

    public Trabajador obtenerTrabajadorPorEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM Trabajador t WHERE t.correoElectronico = :email",
                            Trabajador.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Trabajador obtenerTrabajadorPorCURP(String curp) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM Trabajador t WHERE t.curp = :curp",
                            Trabajador.class)
                    .setParameter("curp", curp)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void actualizarTrabajador(Trabajador trabajador) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(trabajador);
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

    public void eliminarTrabajador(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Trabajador trabajador = entityManager.find(Trabajador.class, id);
            if (trabajador != null) {
                entityManager.remove(trabajador);
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

    public boolean existeEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Trabajador t WHERE t.correoElectronico = :email",
                            Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean validarCredenciales(String email, String password) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Trabajador t WHERE t.correoElectronico = :email AND t.contrasena = :password",
                            Long.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}