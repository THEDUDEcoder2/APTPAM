package com.example.trabajos.services;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class OfertaService {

    public Oferta guardarOferta(Oferta oferta) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();

            if (oferta.getIdOferta() == null) {
                entityManager.persist(oferta);
            } else {
                oferta = entityManager.merge(oferta);
            }
            entityManager.flush();
            entityManager.getTransaction().commit();

            System.out.println("✅ Oferta guardada: " + oferta.getPuesto_trabajo() + " | Sueldo: " + oferta.getSueldo() +
                    " | Expira: " + oferta.getFechaExpiracion());
            return oferta;
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public List<Oferta> obtenerTodasOfertas() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.fechaExpiracion >= :hoy ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("hoy", LocalDate.now())
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    // CORREGIDO: Filtrar solo ofertas públicas NO expiradas
    public List<Oferta> obtenerOfertasPublicas() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PUBLICA' " +
                                    "AND o.trabajadorDestino IS NULL " +
                                    "AND (o.fechaExpiracion IS NULL OR o.fechaExpiracion >= :hoy) " +
                                    "ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("hoy", LocalDate.now())
                    .getResultList();

            for (Oferta o : ofertas) {
                o.getSueldo();
            }

            System.out.println("📋 Ofertas públicas encontradas (no expiradas): " + ofertas.size());
            return ofertas;
        } finally {
            entityManager.close();
        }
    }

    // CORREGIDO: Filtrar solo ofertas privadas NO expiradas para el trabajador
    public List<Oferta> obtenerOfertasPrivadasPorTrabajador(Trabajador trabajador) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PRIVADA' " +
                                    "AND o.trabajadorDestino.idTrabajador = :idTrabajador " +
                                    "AND (o.fechaExpiracion IS NULL OR o.fechaExpiracion >= :hoy) " +
                                    "ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("idTrabajador", trabajador.getIdTrabajador())
                    .setParameter("hoy", LocalDate.now())
                    .getResultList();

            for (Oferta o : ofertas) {
                o.getSueldo();
            }

            return ofertas;
        } finally {
            entityManager.close();
        }
    }

    public Oferta obtenerOfertaPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            Oferta oferta = entityManager.find(Oferta.class, id);
            if (oferta != null) {
                oferta.getSueldo();
            }
            return oferta;
        } finally {
            entityManager.close();
        }
    }

    public List<Oferta> obtenerOfertasPorEmpresa(Empresa empresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = em.createQuery(
                            "SELECT o FROM Oferta o WHERE o.empresa = :empresa ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("empresa", empresa)
                    .getResultList();

            for (Oferta o : ofertas) {
                o.getSueldo();
            }

            return ofertas;
        } finally {
            em.close();
        }
    }

    public void actualizarOferta(Oferta oferta) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(oferta);
            entityManager.getTransaction().commit();
            System.out.println("✅ Oferta actualizada: " + oferta.getPuesto_trabajo() + " | Sueldo: " + oferta.getSueldo());
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public void eliminarOferta(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Oferta oferta = entityManager.find(Oferta.class, id);
            if (oferta != null) {
                entityManager.remove(oferta);
            }
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}