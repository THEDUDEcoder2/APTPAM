package com.example.trabajos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {
    private static GestorUsuarios instancia;
    private List<Usuario> usuariosRegistrados;
    private List<Trabajador> trabajadoresRegistrados;

    private GestorUsuarios() {
        usuariosRegistrados = new ArrayList<>();
        trabajadoresRegistrados = new ArrayList<>();
    }

    public static GestorUsuarios getInstancia() {
        if (instancia == null) {
            instancia = new GestorUsuarios();
        }
        return instancia;
    }

    public void registrarUsuario(String nombre, String email, String password) {
        usuariosRegistrados.add(new Usuario(nombre, email, password, true));
        System.out.println("Usuario registrado: " + email);
    }

    public void registrarUsuario(String nombre, String email, String password, boolean esEmpresa) {
        usuariosRegistrados.add(new Usuario(nombre, email, password, esEmpresa));
        System.out.println("Usuario registrado: " + email + " (Tipo: " + (esEmpresa ? "Empresa" : "Buscador") + ")");
    }

    public void registrarEmpresa(String nombre, String email, String password,
                                 String tipoEmpresa, String telefono, String sectorActividad,
                                 String actividadEconomicaPrincipal, String domicilio,
                                 String codigoPostal, String rfc, String razonSocial) {
        Usuario empresa = new Usuario(nombre, email, password, true,
                tipoEmpresa, telefono, sectorActividad,
                actividadEconomicaPrincipal, domicilio,
                codigoPostal, rfc, razonSocial);
        usuariosRegistrados.add(empresa);
        System.out.println("Empresa registrada: " + email + " - " + razonSocial);
    }

    public void registrarTrabajador(String nombre, String email, String password,
                                    LocalDate fechaNacimiento, String genero, String nacionalidad,
                                    String estadoCivil, String rfc, String curp, String domicilio,
                                    String codigoPostal, String telefono, String herramientas,
                                    String idiomas, String nivelEstudio, String especialidad,
                                    String anosExperiencia, String discapacidad, String experiencia,
                                    String habilidades) {

        Trabajador trabajador = new Trabajador(
                nombre, email, password, fechaNacimiento, genero, nacionalidad,
                estadoCivil, rfc, curp, domicilio, codigoPostal, telefono,
                herramientas, idiomas, nivelEstudio, especialidad,
                anosExperiencia, discapacidad, experiencia, habilidades
        );

        trabajadoresRegistrados.add(trabajador);
        registrarUsuario(nombre, email, password, false);

        System.out.println("Trabajador registrado: " + email + " - " + nombre);
        System.out.println("Datos completos guardados en sistema");
    }

    public boolean verificarCredenciales(String email, String password) {
        for (Usuario usuario : usuariosRegistrados) {
            if (usuario.getEmail().equals(email) && usuario.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeUsuario(String email) {
        for (Usuario usuario : usuariosRegistrados) {
            if (usuario.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public List<Usuario> getUsuariosRegistrados() {
        return usuariosRegistrados;
    }

    public List<Trabajador> getTrabajadoresRegistrados() {
        return trabajadoresRegistrados;
    }

    public Usuario getUsuarioPorEmail(String email) {
        for (Usuario usuario : usuariosRegistrados) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }
        return null;
    }

    public Trabajador getTrabajadorPorEmail(String email) {
        for (Trabajador trabajador : trabajadoresRegistrados) {
            if (trabajador.getEmail().equals(email)) {
                return trabajador;
            }
        }
        return null;
    }
}