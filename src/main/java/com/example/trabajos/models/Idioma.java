package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "idiomas")
public class Idioma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_idioma")
    private Integer idIdioma;

    @Column(name = "Nombre_idioma", nullable = false, unique = true)
    private String nombreIdioma;

    @OneToMany(mappedBy = "idioma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrabajadorIdioma> trabajadorIdiomas = new ArrayList<>();

    @OneToMany(mappedBy = "idioma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfertaIdioma> ofertaIdiomas = new ArrayList<>();

    public Idioma() {
    }

    public Idioma(String nombreIdioma) {
        this.nombreIdioma = nombreIdioma;
    }

    public Integer getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(Integer idIdioma) {
        this.idIdioma = idIdioma;
    }

    public String getNombreIdioma() {
        return nombreIdioma;
    }

    public void setNombreIdioma(String nombreIdioma) {
        this.nombreIdioma = nombreIdioma;
    }

    public List<TrabajadorIdioma> getTrabajadorIdiomas() {
        return trabajadorIdiomas;
    }

    public void setTrabajadorIdiomas(List<TrabajadorIdioma> trabajadorIdiomas) {
        this.trabajadorIdiomas = trabajadorIdiomas;
    }

    public List<OfertaIdioma> getOfertaIdiomas() {
        return ofertaIdiomas;
    }

    public void setOfertaIdiomas(List<OfertaIdioma> ofertaIdiomas) {
        this.ofertaIdiomas = ofertaIdiomas;
    }
}