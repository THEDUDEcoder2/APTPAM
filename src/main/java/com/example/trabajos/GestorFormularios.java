package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestorFormularios {

    private static GestorFormularios instancia;
    private List<Oferta> ofertas;

    private GestorFormularios() {
        ofertas = new ArrayList<>();
    }

    public static GestorFormularios getInstancia() {
        if (instancia == null) {
            instancia = new GestorFormularios();
        }
        return instancia;
    }

    public void guardarOferta(Oferta oferta) {
        ofertas.add(oferta);
        System.out.println("Oferta guardada: "
                + oferta.getPuesto_trabajo()
                + " - Empresa: "
                + oferta.getEmpresa().getCorreoElectronico());
    }

    public List<Oferta> getOfertas() {
        return new ArrayList<>(ofertas);
    }

    public List<Oferta> getOfertasPorEmpresa(String emailEmpresa) {
        return ofertas.stream()
                .filter(oferta -> oferta.getEmpresa() != null &&
                        emailEmpresa.equals(oferta.getEmpresa().getCorreoElectronico()))
                .collect(Collectors.toList());
    }

    public void eliminarOferta(Oferta oferta) {
        ofertas.remove(oferta);
    }
}
