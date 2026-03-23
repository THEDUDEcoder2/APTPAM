package com.example.trabajos.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ofertas")
public class Oferta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_oferta")
    private Integer idOferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_salario")
    private Salario salario;

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_estado_contratacion")
    private EstadoContratacion estadoContratacion;

    // Relación con trabajador para ofertas privadas (misma entidad Trabajador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_trabajador_destino")
    private Trabajador trabajadorDestino;

    @Column(name = "Puesto_trabajo", nullable = false)
    private String puesto_trabajo;

    @Column(name = "Descripcion_trabajo", columnDefinition = "TEXT")
    private String descripcion_trabajo;

    @Column(name = "Experiencia")
    private String experiencia;

    @Column(name = "Jornada_laboral")
    private String jornada_laboral;

    @Column(name = "Nivel_estudio")
    private String nivel_estudio;

    @Column(name = "Cantidad")
    private Integer cantidad;

    @Column(name = "Fecha_publicacion")
    private LocalDate fecha_publicacion;

    @Column(name = "Tipo_oferta", nullable = false)
    private String tipoOferta; // "PUBLICA" o "PRIVADA"

    @Column(name = "Mensaje_personal", columnDefinition = "TEXT")
    private String mensajePersonal;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfertaIdioma> ofertaIdiomas = new ArrayList<>();

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulaciones = new ArrayList<>();

    public Oferta() {
        this.fecha_publicacion = LocalDate.now();
    }

    // Getters y Setters
    public Integer getIdOferta() { return idOferta; }
    public void setIdOferta(Integer idOferta) { this.idOferta = idOferta; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Salario getSalario() { return salario; }
    public void setSalario(Salario salario) { this.salario = salario; }

    public EstadoContratacion getEstadoContratacion() { return estadoContratacion; }
    public void setEstadoContratacion(EstadoContratacion estadoContratacion) { this.estadoContratacion = estadoContratacion; }

    public Trabajador getTrabajadorDestino() { return trabajadorDestino; }
    public void setTrabajadorDestino(Trabajador trabajadorDestino) {
        this.trabajadorDestino = trabajadorDestino;
        if (trabajadorDestino != null) {
            this.tipoOferta = "PRIVADA";
        }
    }

    public String getPuesto_trabajo() { return puesto_trabajo; }
    public void setPuesto_trabajo(String puesto_trabajo) { this.puesto_trabajo = puesto_trabajo; }

    public String getDescripcion_trabajo() { return descripcion_trabajo; }
    public void setDescripcion_trabajo(String descripcion_trabajo) { this.descripcion_trabajo = descripcion_trabajo; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getJornada_laboral() { return jornada_laboral; }
    public void setJornada_laboral(String jornada_laboral) { this.jornada_laboral = jornada_laboral; }

    public String getNivel_estudio() { return nivel_estudio; }
    public void setNivel_estudio(String nivel_estudio) { this.nivel_estudio = nivel_estudio; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDate getFecha_publicacion() { return fecha_publicacion; }
    public void setFecha_publicacion(LocalDate fecha_publicacion) { this.fecha_publicacion = fecha_publicacion; }

    public String getTipoOferta() { return tipoOferta; }
    public void setTipoOferta(String tipoOferta) { this.tipoOferta = tipoOferta; }

    public String getMensajePersonal() { return mensajePersonal; }
    public void setMensajePersonal(String mensajePersonal) { this.mensajePersonal = mensajePersonal; }

    public List<OfertaIdioma> getOfertaIdiomas() { return ofertaIdiomas; }
    public void setOfertaIdiomas(List<OfertaIdioma> ofertaIdiomas) { this.ofertaIdiomas = ofertaIdiomas; }

    public List<Postulacion> getPostulaciones() { return postulaciones; }
    public void setPostulaciones(List<Postulacion> postulaciones) { this.postulaciones = postulaciones; }

    public void addOfertaIdioma(OfertaIdioma ofertaIdioma) {
        ofertaIdiomas.add(ofertaIdioma);
        ofertaIdioma.setOferta(this);
    }

    public void removeOfertaIdioma(OfertaIdioma ofertaIdioma) {
        ofertaIdiomas.remove(ofertaIdioma);
        ofertaIdioma.setOferta(null);
    }

    public void addPostulacion(Postulacion postulacion) {
        postulaciones.add(postulacion);
        postulacion.setOferta(this);
    }

    public void removePostulacion(Postulacion postulacion) {
        postulaciones.remove(postulacion);
        postulacion.setOferta(null);
    }

    public String getIdiomasRequeridos() {
        StringBuilder idiomas = new StringBuilder();
        for (OfertaIdioma ofertaIdioma : ofertaIdiomas) {
            if (idiomas.length() > 0) idiomas.append(", ");
            idiomas.append(ofertaIdioma.getIdioma().getNombreIdioma());
        }
        return idiomas.toString();
    }

    public boolean esOfertaPublica() {
        return "PUBLICA".equals(tipoOferta);
    }

    public boolean esOfertaPrivada() {
        return "PRIVADA".equals(tipoOferta) && trabajadorDestino != null;
    }

    public String getNombreTrabajadorDestino() {
        return trabajadorDestino != null ? trabajadorDestino.getNombreCompleto() : null;
    }
}