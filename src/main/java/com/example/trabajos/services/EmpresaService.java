package com.example.trabajos.services;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EmpresaService {

    public void guardarEmpresa(Empresa empresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(empresa);
            em.getTransaction().commit();
            System.out.println("✅ Empresa guardada: " + empresa.getNombreEmpresa());
        } finally {
            em.close();
        }
    }

    public List<Empresa> obtenerTodasEmpresas() {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("FROM Empresa", Empresa.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Empresa obtenerEmpresaPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Empresa.class, id);
        } finally {
            em.close();
        }
    }

    public Empresa obtenerEmpresaPorEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Empresa> lista = em.createQuery(
                            "SELECT e FROM Empresa e WHERE e.correoElectronico = :email",
                            Empresa.class)
                    .setParameter("email", email)
                    .getResultList();

            return lista.isEmpty() ? null : lista.get(0);

        } finally {
            em.close();
        }
    }

    public Empresa obtenerEmpresaPorRFC(String rfc) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Empresa> lista = em.createQuery(
                            "SELECT e FROM Empresa e WHERE e.rfc = :rfc",
                            Empresa.class)
                    .setParameter("rfc", rfc)
                    .getResultList();

            return lista.isEmpty() ? null : lista.get(0);

        } finally {
            em.close();
        }
    }

    public void actualizarEmpresa(Empresa empresa) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(empresa);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void eliminarEmpresa(int id) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, id);
            if (empresa != null) {
                em.remove(empresa);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public boolean existeEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Empresa e WHERE e.correoElectronico = :email",
                            Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }
}
