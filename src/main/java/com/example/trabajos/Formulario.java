package com.example.trabajos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Formulario {

    private String nombreEmpresa;
    private String herramienta;
    private List<String> idiomasLista;
    private String domicilio;
    private String gmail;
    private String telefono;
    private String puesto;
    private String horario;
    private String sueldo;
    private String nivelEstudio;
    private String experiencia;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String emailEmpresa;

    public Formulario(String nombreEmpresa, String herramienta, String idiomas,
                      String domicilio, String gmail, String telefono, String puesto,
                      String horario, String sueldo, String nivelEstudio,
                      String experiencia, String descripcion, String emailEmpresa) {

        this.nombreEmpresa = nombreEmpresa;
        this.herramienta = herramienta;
        this.idiomasLista = convertirStringALista(idiomas);
        this.domicilio = domicilio;
        this.gmail = gmail;
        this.telefono = telefono;
        this.puesto = puesto;
        this.horario = horario;
        this.sueldo = sueldo;
        this.nivelEstudio = nivelEstudio;
        this.experiencia = experiencia;
        this.descripcion = descripcion;
        this.fechaCreacion = LocalDateTime.now();
        this.emailEmpresa = emailEmpresa;
    }

    private List<String> convertirStringALista(String idiomas) {
        List<String> lista = new ArrayList<>();
        if (idiomas != null && !idiomas.isEmpty()) {
            for (String idioma : idiomas.split(",")) {
                lista.add(idioma.trim());
            }
        }
        return lista;
    }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getHerramienta() { return herramienta; }
    public List<String> getIdiomasLista() { return idiomasLista; }
    public String getIdiomas() { return String.join(", ", idiomasLista); }
    public String getDomicilio() { return domicilio; }
    public String getGmail() { return gmail; }
    public String getTelefono() { return telefono; }
    public String getPuesto() { return puesto; }
    public String getHorario() { return horario; }
    public String getSueldo() { return sueldo; }
    public String getNivelEstudio() { return nivelEstudio; }
    public String getExperiencia() { return experiencia; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getEmailEmpresa() { return emailEmpresa; }

    public String getFechaFormateada() {
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTitulo() {
        return nombreEmpresa + " - " + puesto;
    }
}
