package com.example.trabajos.models;

import jakarta.persistence.*;

@Entity
@Table(name = "oferta_idiomas")
public class OfertaIdioma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_oferta_idioma")
    private Integer idOfertaIdioma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_oferta", nullable = false)
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_idioma", nullable = false)
    private Idioma idioma;

    public OfertaIdioma() {
    }

    public OfertaIdioma(Oferta oferta, Idioma idioma) {
        this.oferta = oferta;
        this.idioma = idioma;
    }

    public Integer getIdOfertaIdioma() {
        return idOfertaIdioma;
    }

    public void setIdOfertaIdioma(Integer idOfertaIdioma) {
        this.idOfertaIdioma = idOfertaIdioma;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }
}