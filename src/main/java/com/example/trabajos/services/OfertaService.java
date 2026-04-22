package com.example.trabajos.services;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

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

            System.out.println("✅ Oferta guardada: " + oferta.getPuesto_trabajo() + " | Sueldo: " + oferta.getSueldo());
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
            return entityManager.createQuery("FROM Oferta ORDER BY fecha_publicacion DESC", Oferta.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public List<Oferta> obtenerOfertasPublicas() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PUBLICA' AND o.trabajadorDestino IS NULL ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .getResultList();

            // Forzar carga del sueldo
            for (Oferta o : ofertas) {
                o.getSueldo();
            }

            System.out.println("📋 Ofertas públicas encontradas: " + ofertas.size());
            return ofertas;
        } finally {
            entityManager.close();
        }
    }

    public List<Oferta> obtenerOfertasPrivadasPorTrabajador(Trabajador trabajador) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PRIVADA' AND o.trabajadorDestino.idTrabajador = :idTrabajador ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("idTrabajador", trabajador.getIdTrabajador())
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
                oferta.getSueldo(); // Forzar carga
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