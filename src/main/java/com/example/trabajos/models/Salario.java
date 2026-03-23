package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salarios")
public class Salario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_salario")
    private Integer idSalario;

    @Column(name = "Tipo_salario", nullable = false)
    private String tipoSalario;

    @OneToMany(mappedBy = "salario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oferta> ofertas = new ArrayList<>();

    public Salario() {
    }

    public Salario(String tipoSalario) {
        this.tipoSalario = tipoSalario;
    }

    public Integer getIdSalario() {
        return idSalario;
    }

    public void setIdSalario(Integer idSalario) {
        this.idSalario = idSalario;
    }

    public String getTipoSalario() {
        return tipoSalario;
    }

    public void setTipoSalario(String tipoSalario) {
        this.tipoSalario = tipoSalario;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }
}