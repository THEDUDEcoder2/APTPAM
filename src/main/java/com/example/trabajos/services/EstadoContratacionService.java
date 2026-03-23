package com.example.trabajos.services;

import com.example.trabajos.models.EstadoContratacion;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EstadoContratacionService {

    public List<EstadoContratacion> obtenerTodosEstadosContratacion() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM EstadoContratacion", EstadoContratacion.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public EstadoContratacion obtenerEstadoContratacionPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(EstadoContratacion.class, id);
        } finally {
            em.close();
        }
    }

    public EstadoContratacion obtenerEstadoContratacionPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<EstadoContratacion> lista = em.createQuery(
                            "SELECT e FROM EstadoContratacion e WHERE e.estadoActual = :nombre",
                            EstadoContratacion.class)
                    .setParameter("nombre", nombre)
                    .getResultList();

            return lista.isEmpty() ? null : lista.get(0);

        } finally {
            em.close();
        }
    }
}
