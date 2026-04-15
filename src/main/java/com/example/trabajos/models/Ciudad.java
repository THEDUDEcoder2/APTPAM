package com.example.trabajos.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudades_bcs")
public class Ciudad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ciudad")
    private Integer idCiudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_municipio", nullable = false)
    private Municipio municipio;

    @Column(name = "Nombre_ciudad", nullable = false)
    private String nombreCiudad;

    public Ciudad() {
    }

    public Ciudad(String nombreCiudad, Municipio municipio) {
        this.nombreCiudad = nombreCiudad;
        this.municipio = municipio;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }
}