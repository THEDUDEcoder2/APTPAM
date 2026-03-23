package com.example.trabajos.models;

import jakarta.persistence.*;

@Entity
@Table(name = "trabajador_idiomas")
public class TrabajadorIdioma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_trabajador_idioma")
    private Integer idTrabajadorIdioma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_trabajador", nullable = false)
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_idioma", nullable = false)
    private Idioma idioma;

    public TrabajadorIdioma() {
    }

    public TrabajadorIdioma(Trabajador trabajador, Idioma idioma) {
        this.trabajador = trabajador;
        this.idioma = idioma;
    }

    public Integer getIdTrabajadorIdioma() {
        return idTrabajadorIdioma;
    }

    public void setIdTrabajadorIdioma(Integer idTrabajadorIdioma) {
        this.idTrabajadorIdioma = idTrabajadorIdioma;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }
}