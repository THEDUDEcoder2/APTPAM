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

            String tipoInfo = oferta.getTipoOferta() != null ? oferta.getTipoOferta() : "PUBLICA";
            String destinoInfo = "";
            if (oferta.getTrabajadorDestino() != null) {
                destinoInfo = " para " + oferta.getTrabajadorDestino().getNombre() + " " +
                        (oferta.getTrabajadorDestino().getApellidoPaterno() != null ? oferta.getTrabajadorDestino().getApellidoPaterno() : "");
            }

            System.out.println("✅ Oferta guardada: " + oferta.getPuesto_trabajo() +
                    " (" + tipoInfo + ")" + destinoInfo);
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

    // Obtener SOLO ofertas públicas (sin trabajador destino)
    public List<Oferta> obtenerOfertasPublicas() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Oferta> ofertas = entityManager.createQuery(
                            "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PUBLICA' AND o.trabajadorDestino IS NULL ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .getResultList();

            System.out.println("📋 Ofertas públicas encontradas: " + ofertas.size());
            return ofertas;
        } finally {
            entityManager.close();
        }
    }

    // Obtener SOLO ofertas privadas para un trabajador específico
    public List<Oferta> obtenerOfertasPrivadasPorTrabajador(Trabajador trabajador) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT o FROM Oferta o WHERE o.tipoOferta = 'PRIVADA' AND o.trabajadorDestino.idTrabajador = :idTrabajador ORDER BY o.fecha_publicacion DESC";

            List<Oferta> ofertas = entityManager.createQuery(jpql, Oferta.class)
                    .setParameter("idTrabajador", trabajador.getIdTrabajador())
                    .getResultList();

            System.out.println("🔍 Buscando ofertas privadas para trabajador ID: " + trabajador.getIdTrabajador());
            System.out.println("   Encontradas: " + ofertas.size() + " ofertas privadas");

            for (Oferta o : ofertas) {
                System.out.println("   - ID: " + o.getIdOferta() + " | Puesto: " + o.getPuesto_trabajo() +
                        " | Empresa: " + (o.getEmpresa() != null ? o.getEmpresa().getNombreEmpresa() : "?"));
            }

            return ofertas;
        } finally {
            entityManager.close();
        }
    }

    public Oferta obtenerOfertaPorId(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.find(Oferta.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Oferta> obtenerOfertasPorEmpresa(Empresa empresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Oferta o WHERE o.empresa = :empresa ORDER BY o.fecha_publicacion DESC",
                            Oferta.class)
                    .setParameter("empresa", empresa)
                    .getResultList();
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
        } finally {
            entityManager.close();
        }
    }
}