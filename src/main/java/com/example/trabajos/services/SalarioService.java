package com.example.trabajos.services;

import com.example.trabajos.models.Salario;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SalarioService {

    public List<Salario> obtenerTodosSalarios() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM Salario", Salario.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Salario obtenerSalarioPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Salario.class, id);
        } finally {
            em.close();
        }
    }

    public Salario obtenerSalarioPorTipo(String tipo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Salario> lista = em.createQuery(
                            "SELECT s FROM Salario s WHERE s.tipoSalario = :tipo",
                            Salario.class)
                    .setParameter("tipo", tipo)
                    .getResultList();

            return lista.isEmpty() ? null : lista.get(0);

        } finally {
            em.close();
        }
    }
}
