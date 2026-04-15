package com.example.trabajos.services;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.utils.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class PostulacionService {

    public void guardarPostulacion(Postulacion postulacion) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(postulacion);
            em.getTransaction().commit();
            System.out.println("✅ Postulación guardada para oferta: " +
                    postulacion.getOferta().getPuesto_trabajo());
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Postulacion obtenerPostulacionPorTrabajadorYOferta(Trabajador trabajador, Oferta oferta) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Postulacion> lista = em.createQuery(
                            "SELECT p FROM Postulacion p " +
                                    "WHERE p.trabajador.idTrabajador = :idTrabajador " +
                                    "AND p.oferta.idOferta = :idOferta",
                            Postulacion.class
                    )
                    .setParameter("idTrabajador", trabajador.getIdTrabajador())
                    .setParameter("idOferta", oferta.getIdOferta())
                    .getResultList();

            return lista.isEmpty() ? null : lista.get(0);
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Postulacion> obtenerPostulacionesPorOferta(Oferta oferta) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Postulacion p WHERE p.oferta = :oferta",
                            Postulacion.class
                    )
                    .setParameter("oferta", oferta)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void actualizarPostulacion(Postulacion postulacion) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(postulacion);
            em.getTransaction().commit();

            System.out.println("🔄 Postulación actualizada: " + postulacion.getEstado());

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void actualizarNotaEmpresa(Postulacion postulacion, String nota) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            postulacion.setNotaEmpresa(nota);
            em.merge(postulacion);

            em.getTransaction().commit();

            System.out.println("📝 Nota actualizada para trabajador: "
                    + postulacion.getTrabajador().getNombre()
                    + " en oferta: " + postulacion.getOferta().getPuesto_trabajo());

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Postulacion> obtenerPostulacionesPorEmpresa(String emailEmpresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Postulacion p " +
                                    "JOIN p.empresa e " +
                                    "WHERE e.correoElectronico = :emailEmpresa " +
                                    "ORDER BY p.fechaPostulacion DESC",
                            Postulacion.class
                    )
                    .setParameter("emailEmpresa", emailEmpresa)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error al obtener postulaciones por empresa: " + e.getMessage());
            return java.util.Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Postulacion> obtenerPostulacionesPorEmpresaId(Integer idEmpresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Postulacion p " +
                                    "WHERE p.empresa.idEmpresa = :idEmpresa " +
                                    "ORDER BY p.fechaPostulacion DESC",
                            Postulacion.class
                    )
                    .setParameter("idEmpresa", idEmpresa)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}