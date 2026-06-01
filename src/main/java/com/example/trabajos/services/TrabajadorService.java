package com.example.trabajos.services;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
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
            System.out.println("✅ Trabajador guardado: " + trabajador.getNombre() + " | Foto: " + (trabajador.getFotoPerfil() != null ? trabajador.getFotoPerfil().length + " bytes" : "sin foto"));
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
            List<Trabajador> trabajadores = entityManager.createQuery("FROM Trabajador", Trabajador.class).getResultList();
            for (Trabajador t : trabajadores) {
                if (t.getFotoPerfil() != null) {
                    byte[] foto = t.getFotoPerfil();
                }
            }
            return trabajadores;
        } finally {
            entityManager.close();
        }
    }

    // NUEVO: Obtener solo trabajadores disponibles (activo = true Y sin oferta aceptada)
    public List<Trabajador> obtenerTrabajadoresDisponibles() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            // Usar subconsulta para excluir trabajadores con oferta aceptada
            String jpql = "SELECT t FROM Trabajador t WHERE t.activo = true AND NOT EXISTS (" +
                    "SELECT p FROM Postulacion p WHERE p.trabajador = t AND p.estado = 'ACEPTADO')";
            return em.createQuery(jpql, Trabajador.class).getResultList();
        } finally {
            em.close();
        }
    }

    // NUEVO: Verificar si un trabajador tiene oferta aceptada
    public boolean tieneOfertaAceptada(int idTrabajador) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Postulacion p WHERE p.trabajador.idTrabajador = :id AND p.estado = 'ACEPTADO'",
                            Long.class)
                    .setParameter("id", idTrabajador)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // NUEVO: Cambiar estado activo/inactivo por ID
    public void cambiarEstadoActivo(int idTrabajador, boolean activo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int updated = em.createQuery(
                            "UPDATE Trabajador t SET t.activo = :activo WHERE t.idTrabajador = :id")
                    .setParameter("activo", activo)
                    .setParameter("id", idTrabajador)
                    .executeUpdate();
            em.getTransaction().commit();
            System.out.println(activo ? "✅ Trabajador activado: ID " + idTrabajador : "⛔ Trabajador desactivado: ID " + idTrabajador);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al cambiar estado activo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // NUEVO: Cambiar estado activo/inactivo por objeto Trabajador
    public void cambiarEstadoActivo(Trabajador trabajador, boolean activo) {
        trabajador.setActivo(activo);
        actualizarTrabajador(trabajador);
    }

    public Trabajador obtenerTrabajadorPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            Trabajador trabajador = entityManager.find(Trabajador.class, id);
            if (trabajador != null && trabajador.getFotoPerfil() != null) {
                byte[] foto = trabajador.getFotoPerfil();
            }
            return trabajador;
        } finally {
            entityManager.close();
        }
    }

    public Trabajador obtenerTrabajadorPorEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Trabajador trabajador = em.createQuery(
                            "SELECT t FROM Trabajador t WHERE t.correoElectronico = :email",
                            Trabajador.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (trabajador != null && trabajador.getFotoPerfil() != null) {
                byte[] foto = trabajador.getFotoPerfil();
            }
            return trabajador;
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
            System.out.println("✅ Trabajador actualizado: " + trabajador.getNombre());
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