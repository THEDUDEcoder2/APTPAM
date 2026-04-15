package com.example.trabajos.services;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Idioma;
import com.example.trabajos.models.TrabajadorIdioma;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class TrabajadorIdiomaService {

    public List<TrabajadorIdioma> obtenerIdiomasPorTrabajador(Trabajador trabajador) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT ti FROM TrabajadorIdioma ti WHERE ti.trabajador = :trabajador",
                            TrabajadorIdioma.class)
                    .setParameter("trabajador", trabajador)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public String obtenerIdiomasComoString(Trabajador trabajador) {
        List<TrabajadorIdioma> trabajadorIdiomas = obtenerIdiomasPorTrabajador(trabajador);

        if (trabajadorIdiomas.isEmpty()) {
            return "No especificado";
        }

        return trabajadorIdiomas.stream()
                .map(ti -> ti.getIdioma().getNombreIdioma())
                .collect(Collectors.joining(", "));
    }

    public void agregarIdiomaATrabajador(Trabajador trabajador, Idioma idioma) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(ti) FROM TrabajadorIdioma ti WHERE ti.trabajador = :trabajador AND ti.idioma = :idioma",
                            Long.class)
                    .setParameter("trabajador", trabajador)
                    .setParameter("idioma", idioma)
                    .getSingleResult();

            if (count == 0) {
                em.getTransaction().begin();
                TrabajadorIdioma trabajadorIdioma = new TrabajadorIdioma(trabajador, idioma);
                em.persist(trabajadorIdioma);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    public void eliminarIdiomaDeTrabajador(Trabajador trabajador, Idioma idioma) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            TrabajadorIdioma trabajadorIdioma = em.createQuery(
                            "SELECT ti FROM TrabajadorIdioma ti WHERE ti.trabajador = :trabajador AND ti.idioma = :idioma",
                            TrabajadorIdioma.class)
                    .setParameter("trabajador", trabajador)
                    .setParameter("idioma", idioma)
                    .getSingleResult();

            if (trabajadorIdioma != null) {
                em.remove(trabajadorIdioma);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}