package com.example.trabajos.services;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.utils.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecuperacionService {

    private static RecuperacionService instancia;
    private Map<String, String> codigosVerificacion = new HashMap<>();
    private Map<String, Long> codigosExpiracion = new HashMap<>();
    private EmailService emailService = new EmailService();

    private RecuperacionService() {}

    public static RecuperacionService getInstancia() {
        if (instancia == null) {
            instancia = new RecuperacionService();
        }
        return instancia;
    }

    public boolean existeEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long countTrabajador = em.createQuery(
                            "SELECT COUNT(t) FROM Trabajador t WHERE t.correoElectronico = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (countTrabajador > 0) return true;

            Long countEmpresa = em.createQuery(
                            "SELECT COUNT(e) FROM Empresa e WHERE e.correoElectronico = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            return countEmpresa > 0;
        } finally {
            em.close();
        }
    }

    public String getTipoUsuario(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long countTrabajador = em.createQuery(
                            "SELECT COUNT(t) FROM Trabajador t WHERE t.correoElectronico = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (countTrabajador > 0) return "TRABAJADOR";

            Long countEmpresa = em.createQuery(
                            "SELECT COUNT(e) FROM Empresa e WHERE e.correoElectronico = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (countEmpresa > 0) return "EMPRESA";

            return null;
        } finally {
            em.close();
        }
    }

    private String generarCodigo() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    public boolean guardarCodigoYEnviar(String email) {
        String codigo = generarCodigo();
        codigosVerificacion.put(email, codigo);
        codigosExpiracion.put(email, System.currentTimeMillis() + (5 * 60 * 1000));

        return emailService.enviarCodigoRecuperacion(email, codigo);
    }

    public boolean verificarCodigo(String email, String codigo) {
        String codigoGuardado = codigosVerificacion.get(email);
        Long expiracion = codigosExpiracion.get(email);

        if (codigoGuardado == null || expiracion == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiracion) {
            codigosVerificacion.remove(email);
            codigosExpiracion.remove(email);
            return false;
        }

        return codigoGuardado.equals(codigo);
    }

    public boolean actualizarContrasena(String email, String nuevaContrasena) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            int updatedTrabajador = em.createQuery(
                            "UPDATE Trabajador t SET t.contrasena = :password WHERE t.correoElectronico = :email")
                    .setParameter("password", nuevaContrasena)
                    .setParameter("email", email)
                    .executeUpdate();

            if (updatedTrabajador > 0) {
                em.getTransaction().commit();
                limpiarCodigo(email);
                emailService.enviarConfirmacionCambio(email);
                return true;
            }

            int updatedEmpresa = em.createQuery(
                            "UPDATE Empresa e SET e.contrasena = :password WHERE e.correoElectronico = :email")
                    .setParameter("password", nuevaContrasena)
                    .setParameter("email", email)
                    .executeUpdate();

            if (updatedEmpresa > 0) {
                em.getTransaction().commit();
                limpiarCodigo(email);
                emailService.enviarConfirmacionCambio(email);
                return true;
            }

            em.getTransaction().rollback();
            return false;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public void limpiarCodigo(String email) {
        codigosVerificacion.remove(email);
        codigosExpiracion.remove(email);
    }

    public boolean reenviarCodigo(String email) {
        String codigoExistente = codigosVerificacion.get(email);
        Long expiracion = codigosExpiracion.get(email);

        if (codigoExistente != null && expiracion != null && System.currentTimeMillis() <= expiracion) {
            return emailService.enviarCodigoRecuperacion(email, codigoExistente);
        } else {
            return guardarCodigoYEnviar(email);
        }
    }
}