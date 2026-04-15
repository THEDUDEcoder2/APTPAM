package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Trabajador;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestorPostulaciones {

    private static GestorPostulaciones instancia;
    private List<Postulacion> postulaciones;

    private GestorPostulaciones() {
        postulaciones = new ArrayList<>();
    }

    public static GestorPostulaciones getInstancia() {
        if (instancia == null) instancia = new GestorPostulaciones();
        return instancia;
    }

    public void agregarPostulacion(Postulacion postulacion) {
        postulaciones.add(postulacion);
        System.out.println(
                "Postulación agregada: " + postulacion.getTrabajador().getNombre() +
                        " para " + postulacion.getOferta().getPuesto_trabajo() +
                        " - Estado: " + postulacion.getEstado()
        );
    }

    public List<Postulacion> getPostulacionesPorOferta(Oferta oferta) {
        return postulaciones.stream()
                .filter(p -> p.getOferta().equals(oferta))
                .collect(Collectors.toList());
    }

    public List<Postulacion> getPostulacionesPorTrabajador(Trabajador trabajador) {
        return postulaciones.stream()
                .filter(p -> p.getTrabajador().equals(trabajador))
                .collect(Collectors.toList());
    }

    public void actualizarEstadoPostulacion(Postulacion postulacion, String nuevoEstado) {
        postulacion.setEstado(nuevoEstado);
    }
}
