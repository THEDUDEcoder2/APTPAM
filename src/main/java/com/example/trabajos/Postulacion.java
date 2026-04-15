package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import java.time.LocalDateTime;

public class Postulacion {

    private Trabajador trabajador;
    private Oferta oferta;
    private LocalDateTime fechaPostulacion;
    private String estado;
    private String notaEmpresa;

    public Postulacion() {
        this.estado = "PENDIENTE";
        this.notaEmpresa = "";
    }

    public Postulacion(Trabajador trabajador, Oferta oferta, LocalDateTime fechaPostulacion) {
        this.trabajador = trabajador;
        this.oferta = oferta;
        this.fechaPostulacion = fechaPostulacion;
        this.estado = "PENDIENTE";
        this.notaEmpresa = "";
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public Oferta getOferta() {      // ← GETTER CORRECTO
        return oferta;
    }

    public void setOferta(Oferta oferta) {    // ← SETTER CORRECTO
        this.oferta = oferta;
    }

    public LocalDateTime getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDateTime fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotaEmpresa() {
        return notaEmpresa != null ? notaEmpresa : "";
    }

    public void setNotaEmpresa(String notaEmpresa) {
        this.notaEmpresa = notaEmpresa;
    }

    public boolean tieneNotaEmpresa() {
        return notaEmpresa != null && !notaEmpresa.trim().isEmpty();
    }

    public String getNombreTrabajador() {
        return trabajador != null ? trabajador.getNombre() : "No disponible";
    }

    public String getEmailTrabajador() {
        return trabajador != null ? trabajador.getEmail() : "No disponible";
    }

    public String getEstadoFormateado() {
        switch (estado) {
            case "ACEPTADO": return "✅ Aceptado";
            case "RECHAZADO": return "❌ Rechazado";
            default: return "⏳ Pendiente";
        }
    }
}
