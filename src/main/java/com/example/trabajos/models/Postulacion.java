package com.example.trabajos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "postulacion")
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_postulacion")
    private Integer idPostulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_oferta", nullable = false)
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_usuario", nullable = false)
    private Trabajador trabajador;

    @Column(name = "Fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    @Column(name = "Estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "Nota_empresa", columnDefinition = "TEXT")
    private String notaEmpresa;

    public Postulacion() {
        this.fechaPostulacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.notaEmpresa = "";
    }

    public Postulacion(Trabajador trabajador, Oferta oferta, Empresa empresa) {
        this();
        this.trabajador = trabajador;
        this.oferta = oferta;
        this.empresa = empresa;
    }

    public Integer getIdPostulacion() {
        return idPostulacion;
    }

    public void setIdPostulacion(Integer idPostulacion) {
        this.idPostulacion = idPostulacion;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
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
        this.estado = estado != null ? estado.toUpperCase() : "PENDIENTE";
    }

    public String getNotaEmpresa() {
        return notaEmpresa != null ? notaEmpresa : "";
    }

    public void setNotaEmpresa(String notaEmpresa) {
        this.notaEmpresa = notaEmpresa;
    }

    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }

    public boolean isAceptada() {
        return "ACEPTADO".equals(estado);
    }

    public boolean isRechazada() {
        return "RECHAZADO".equals(estado);
    }

    public boolean tieneNotaEmpresa() {
        return notaEmpresa != null && !notaEmpresa.trim().isEmpty();
    }

    public String getEstadoFormateado() {
        switch (estado.toUpperCase()) {
            case "ACEPTADO":
                return "✅ Aceptado";
            case "RECHAZADO":
                return "❌ Rechazado";
            case "PENDIENTE":
            default:
                return "⏳ Pendiente";
        }
    }

    public String getFechaPostulacionFormateada() {
        if (fechaPostulacion == null) return "No especificada";
        return fechaPostulacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Postulacion{" +
                "id=" + idPostulacion +
                ", trabajador=" + (trabajador != null ? trabajador.getNombreCompleto() : "null") +
                ", oferta=" + (oferta != null ? oferta.getPuesto_trabajo() : "null") +
                ", empresa=" + (empresa != null ? empresa.getNombreEmpresa() : "null") +
                ", estado='" + estado + '\'' +
                '}';
    }
}