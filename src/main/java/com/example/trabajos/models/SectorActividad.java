package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sectores_actividad")
public class SectorActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_sector_actividad")
    private Integer idSectorActividad;

    @Column(name = "Tipo_sector_actividad", nullable = false, unique = true)
    private String tipoSectorActividad;

    @OneToMany(mappedBy = "sectorActividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empresa> empresas = new ArrayList<>();

    public SectorActividad() {
    }

    public SectorActividad(String tipoSectorActividad) {
        this.tipoSectorActividad = tipoSectorActividad;
    }

    public Integer getIdSectorActividad() {
        return idSectorActividad;
    }

    public void setIdSectorActividad(Integer idSectorActividad) {
        this.idSectorActividad = idSectorActividad;
    }

    public String getTipoSectorActividad() {
        return tipoSectorActividad;
    }

    public void setTipoSectorActividad(String tipoSectorActividad) {
        this.tipoSectorActividad = tipoSectorActividad;
    }

    public List<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
    }
}