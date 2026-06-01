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

    // ── Calificación empresa → trabajador ────────────────────────────────
    @Column(name = "calif_emp_puntualidad")
    private Double califEmpPuntualidad;
    @Column(name = "calif_emp_actitud")
    private Double califEmpActitud;
    @Column(name = "calif_emp_desempeno")
    private Double califEmpDesempeno;
    @Column(name = "calif_emp_confiabilidad")
    private Double califEmpConfiabilidad;
    @Column(name = "calif_emp_promedio")
    private Double califEmpPromedio;
    @Column(name = "comentario_empresa", columnDefinition = "TEXT")
    private String comentarioEmpresa;

    // ── Calificación trabajador → empresa ────────────────────────────────
    @Column(name = "calif_tra_trato")
    private Double califTraTrato;
    @Column(name = "calif_tra_condiciones")
    private Double califTraCondiciones;
    @Column(name = "calif_tra_pago")
    private Double califTraPago;
    @Column(name = "calif_tra_confiabilidad")
    private Double califTraConfiabilidad;
    @Column(name = "calif_tra_promedio")
    private Double califTraPromedio;
    @Column(name = "comentario_trabajador", columnDefinition = "TEXT")
    private String comentarioTrabajador;

    // ── Flags de "ya calificado" ─────────────────────────────────────────
    @Column(name = "empresa_califico")
    private Boolean empresaCalifico = false;
    @Column(name = "trabajador_califico")
    private Boolean trabajadorCalifico = false;

    public Postulacion() {
        this.fechaPostulacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.notaEmpresa = "";
        this.empresaCalifico = false;
        this.trabajadorCalifico = false;
    }

    // Constructor que toma la empresa AUTOMÁTICAMENTE de la oferta
    public Postulacion(Trabajador trabajador, Oferta oferta) {
        this();
        this.trabajador = trabajador;
        this.oferta = oferta;
        // La empresa se obtiene de la oferta (¡CRUCIAL!)
        if (oferta != null) {
            this.empresa = oferta.getEmpresa();
        }
    }

    // Constructor completo (mantener compatibilidad)
    public Postulacion(Trabajador trabajador, Oferta oferta, Empresa empresa) {
        this();
        this.trabajador = trabajador;
        this.oferta = oferta;
        this.empresa = empresa;
    }

    public Postulacion(Trabajador trabajador, Oferta oferta, Empresa empresa, LocalDateTime fechaPostulacion, String estado) {
        this.trabajador = trabajador;
        this.oferta = oferta;
        this.empresa = empresa;
        this.fechaPostulacion = fechaPostulacion;
        this.estado = estado;
        this.notaEmpresa = "";
        this.empresaCalifico = false;
        this.trabajadorCalifico = false;
    }

    // ── Getters / Setters básicos ────────────────────────────────────────
    public Integer getIdPostulacion() { return idPostulacion; }
    public void setIdPostulacion(Integer idPostulacion) { this.idPostulacion = idPostulacion; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
        // Actualizar automáticamente la empresa cuando se establece la oferta
        if (oferta != null && this.empresa == null) {
            this.empresa = oferta.getEmpresa();
        }
    }
    public Trabajador getTrabajador() { return trabajador; }
    public void setTrabajador(Trabajador trabajador) { this.trabajador = trabajador; }
    public LocalDateTime getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(LocalDateTime fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado != null ? estado.toUpperCase() : "PENDIENTE"; }
    public String getNotaEmpresa() { return notaEmpresa != null ? notaEmpresa : ""; }
    public void setNotaEmpresa(String notaEmpresa) { this.notaEmpresa = notaEmpresa; }

    // ── Calificación getters/setters ─────────────────────────────────────
    public Double getCalifEmpPuntualidad() { return califEmpPuntualidad; }
    public void setCalifEmpPuntualidad(Double v) { this.califEmpPuntualidad = v; }
    public Double getCalifEmpActitud() { return califEmpActitud; }
    public void setCalifEmpActitud(Double v) { this.califEmpActitud = v; }
    public Double getCalifEmpDesempeno() { return califEmpDesempeno; }
    public void setCalifEmpDesempeno(Double v) { this.califEmpDesempeno = v; }
    public Double getCalifEmpConfiabilidad() { return califEmpConfiabilidad; }
    public void setCalifEmpConfiabilidad(Double v) { this.califEmpConfiabilidad = v; }
    public Double getCalifEmpPromedio() { return califEmpPromedio; }
    public void setCalifEmpPromedio(Double v) { this.califEmpPromedio = v; }
    public String getComentarioEmpresa() { return comentarioEmpresa; }
    public void setComentarioEmpresa(String v) { this.comentarioEmpresa = v; }

    public Double getCalifTraTrato() { return califTraTrato; }
    public void setCalifTraTrato(Double v) { this.califTraTrato = v; }
    public Double getCalifTraCondiciones() { return califTraCondiciones; }
    public void setCalifTraCondiciones(Double v) { this.califTraCondiciones = v; }
    public Double getCalifTraPago() { return califTraPago; }
    public void setCalifTraPago(Double v) { this.califTraPago = v; }
    public Double getCalifTraConfiabilidad() { return califTraConfiabilidad; }
    public void setCalifTraConfiabilidad(Double v) { this.califTraConfiabilidad = v; }
    public Double getCalifTraPromedio() { return califTraPromedio; }
    public void setCalifTraPromedio(Double v) { this.califTraPromedio = v; }
    public String getComentarioTrabajador() { return comentarioTrabajador; }
    public void setComentarioTrabajador(String v) { this.comentarioTrabajador = v; }

    public Boolean getEmpresaCalifico() { return empresaCalifico != null && empresaCalifico; }
    public void setEmpresaCalifico(Boolean v) { this.empresaCalifico = v; }
    public Boolean getTrabajadorCalifico() { return trabajadorCalifico != null && trabajadorCalifico; }
    public void setTrabajadorCalifico(Boolean v) { this.trabajadorCalifico = v; }

    // ── Helpers ──────────────────────────────────────────────────────────
    public boolean isPendiente() { return "PENDIENTE".equals(estado); }
    public boolean isAceptada() { return "ACEPTADO".equals(estado); }
    public boolean isRechazada() { return "RECHAZADO".equals(estado); }
    public boolean tieneNotaEmpresa() { return notaEmpresa != null && !notaEmpresa.trim().isEmpty(); }

    public String getEstadoFormateado() {
        switch (estado.toUpperCase()) {
            case "ACEPTADO": return "✅ Aceptado";
            case "RECHAZADO": return "❌ Rechazado";
            default: return "⏳ Pendiente";
        }
    }

    public String getFechaPostulacionFormateada() {
        if (fechaPostulacion == null) return "No especificada";
        return fechaPostulacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Postulacion{id=" + idPostulacion +
                ", trabajador=" + (trabajador != null ? trabajador.getNombreCompleto() : "null") +
                ", oferta=" + (oferta != null ? oferta.getPuesto_trabajo() : "null") +
                ", empresa=" + (empresa != null ? empresa.getNombreEmpresa() : "null") +
                ", estado='" + estado + "'}";
    }
}