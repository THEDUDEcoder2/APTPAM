package com.example.trabajos.services;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Idioma;
import com.example.trabajos.models.OfertaIdioma;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class OfertaIdiomaService {

    public void guardarOfertaIdioma(OfertaIdioma ofertaIdioma) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(ofertaIdioma);
            entityManager.getTransaction().commit();
            System.out.println("✅ OfertaIdioma guardado: " + ofertaIdioma.getIdioma().getNombreIdioma());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar oferta idioma: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }

    public void agregarIdiomaAOferta(Oferta oferta, Idioma idioma) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            OfertaIdioma ofertaIdioma = new OfertaIdioma(oferta, idioma);
            em.persist(ofertaIdioma);

            em.getTransaction().commit();
            System.out.println("✅ Idioma agregado a oferta: " + idioma.getNombreIdioma());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al agregar idioma a oferta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<OfertaIdioma> obtenerIdiomasPorOferta(Oferta oferta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT oi FROM OfertaIdioma oi WHERE oi.oferta = :oferta",
                            OfertaIdioma.class)
                    .setParameter("oferta", oferta)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Idioma> obtenerIdiomasDeOferta(Oferta oferta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT oi.idioma FROM OfertaIdioma oi WHERE oi.oferta = :oferta",
                            Idioma.class)
                    .setParameter("oferta", oferta)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void eliminarIdiomaDeOferta(Oferta oferta, Idioma idioma) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            OfertaIdioma ofertaIdioma = em.createQuery(
                            "SELECT oi FROM OfertaIdioma oi WHERE oi.oferta = :oferta AND oi.idioma = :idioma",
                            OfertaIdioma.class)
                    .setParameter("oferta", oferta)
                    .setParameter("idioma", idioma)
                    .getSingleResult();

            if (ofertaIdioma != null) {
                em.remove(ofertaIdioma);
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminarTodosIdiomasDeOferta(Oferta oferta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            List<OfertaIdioma> ofertaIdiomas = em.createQuery(
                            "SELECT oi FROM OfertaIdioma oi WHERE oi.oferta = :oferta",
                            OfertaIdioma.class)
                    .setParameter("oferta", oferta)
                    .getResultList();

            for (OfertaIdioma ofertaIdioma : ofertaIdiomas) {
                em.remove(ofertaIdioma);
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}