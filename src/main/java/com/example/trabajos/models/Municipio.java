package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "municipios_bcs")
public class Municipio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_municipio")
    private Integer idMunicipio;

    @Column(name = "Nombre_municipio", nullable = false, unique = true)
    private String nombreMunicipio;

    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ciudad> ciudades = new ArrayList<>();

    public Municipio() {
    }

    public Municipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public List<Ciudad> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<Ciudad> ciudades) {
        this.ciudades = ciudades;
    }

    public void addCiudad(Ciudad ciudad) {
        ciudades.add(ciudad);
        ciudad.setMunicipio(this);
    }

    public void removeCiudad(Ciudad ciudad) {
        ciudades.remove(ciudad);
        ciudad.setMunicipio(null);
    }
}