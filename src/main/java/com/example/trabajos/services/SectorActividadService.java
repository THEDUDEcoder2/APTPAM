package com.example.trabajos.services;

import com.example.trabajos.models.SectorActividad;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class SectorActividadService {

    public List<SectorActividad> obtenerTodosSectores() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM SectorActividad", SectorActividad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public SectorActividad obtenerSectorPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(SectorActividad.class, id);
        } finally {
            em.close();
        }
    }

    public SectorActividad obtenerSectorPorNombre(String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT s FROM SectorActividad s WHERE s.tipoSectorActividad = :nombre",
                            SectorActividad.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NoResultException e) {
            SectorActividad sector = new SectorActividad(nombre);
            guardarSector(sector);
            return sector;
        } finally {
            em.close();
        }
    }

    public void guardarSector(SectorActividad sector) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(sector);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}