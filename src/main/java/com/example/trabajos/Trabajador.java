package com.example.trabajos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Trabajador {
    private String nombre;
    private String email;
    private String password;
    private LocalDate fechaNacimiento;
    private String genero;
    private String nacionalidad;
    private String estadoCivil;
    private String rfc;
    private String curp;
    private String domicilio;
    private String codigoPostal;
    private String telefono;
    private String herramientas;
    private String idiomas;
    private String nivelEstudio;
    private String especialidad;
    private String anosExperiencia;
    private String discapacidad;
    private String experiencia;
    private String habilidades;

    public Trabajador(String nombre, String email, String password,
                      LocalDate fechaNacimiento, String genero, String nacionalidad,
                      String estadoCivil, String rfc, String curp, String domicilio,
                      String codigoPostal, String telefono, String herramientas,
                      String idiomas, String nivelEstudio, String experiencia) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
        this.rfc = rfc;
        this.curp = curp;
        this.domicilio = domicilio;
        this.codigoPostal = codigoPostal;
        this.telefono = telefono;
        this.herramientas = herramientas;
        this.idiomas = idiomas;
        this.nivelEstudio = nivelEstudio;
        this.experiencia = experiencia;
        this.especialidad = "";
        this.anosExperiencia = "";
        this.discapacidad = "";
        this.habilidades = "";
    }

    public Trabajador(String nombre, String email, String password,
                      LocalDate fechaNacimiento, String genero, String nacionalidad,
                      String estadoCivil, String rfc, String curp, String domicilio,
                      String codigoPostal, String telefono, String herramientas,
                      String idiomas, String nivelEstudio, String especialidad,
                      String anosExperiencia, String discapacidad, String experiencia,
                      String habilidades) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
        this.rfc = rfc;
        this.curp = curp;
        this.domicilio = domicilio;
        this.codigoPostal = codigoPostal;
        this.telefono = telefono;
        this.herramientas = herramientas;
        this.idiomas = idiomas;
        this.nivelEstudio = nivelEstudio;
        this.especialidad = especialidad;
        this.anosExperiencia = anosExperiencia;
        this.discapacidad = discapacidad;
        this.experiencia = experiencia;
        this.habilidades = habilidades;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRfc() { return rfc; }
    public String getIdiomas() { return idiomas; }



    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public void setIdiomas(String idiomas) { this.idiomas = idiomas; }
}