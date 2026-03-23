package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estados_contratacion")
public class EstadoContratacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_estado_contratacion")
    private Integer idEstadoContratacion;

    @Column(name = "Estado_actual", nullable = false, unique = true)
    private String estadoActual;

    @OneToMany(mappedBy = "estadoContratacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oferta> ofertas = new ArrayList<>();

    public EstadoContratacion() {
    }

    public EstadoContratacion(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public Integer getIdEstadoContratacion() {
        return idEstadoContratacion;
    }

    public void setIdEstadoContratacion(Integer idEstadoContratacion) {
        this.idEstadoContratacion = idEstadoContratacion;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }
}