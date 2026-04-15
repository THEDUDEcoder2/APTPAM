package com.example.trabajos.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trabajador")
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_trabajador")
    private Integer idTrabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ciudad")
    private Ciudad ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_municipio")
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Estado_Civil")
    private EstadoCivil estadoCivil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Genero")
    private Genero genero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Nacionalidad")
    private Nacionalidad nacionalidad;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @Column(name = "Apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "Apellido_materno")
    private String apellidoMaterno;

    @Column(name = "Calle")
    private String calle;

    @Column(name = "Colonia")
    private String colonia;

    @Column(name = "Codigo_postal")
    private String codigoPostal;

    @Column(name = "Fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "CURP", unique = true)
    private String curp;

    @Column(name = "Nivel_estudio")
    private String nivelEstudio;

    @Column(name = "Especialidad")
    private String especialidad;

    @Column(name = "Num_telefono")
    private String numTelefono;

    @Column(name = "RFC", unique = true)
    private String rfc;

    @Column(name = "Correo_electronico")
    private String correoElectronico;

    @Column(name = "Experiencia_laboral", columnDefinition = "TEXT")
    private String experienciaLaboral;

    @Column(name = "Anos_experiencia")
    private Integer anosExperiencia;

    @Column(name = "Conocimientos_herramientas", columnDefinition = "TEXT")
    private String conocimientosHerramientas;

    @Column(name = "Habilidades", columnDefinition = "TEXT")
    private String habilidades;

    @Column(name = "Discapacidad")
    private String discapacidad;

    @Column(name = "Contrasena")
    private String contrasena;

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrabajadorIdioma> trabajadorIdiomas = new ArrayList<>();

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulaciones = new ArrayList<>();

    public Trabajador() {
    }

    public Trabajador(String nombre, String apellidoPaterno, String apellidoMaterno,
                      String correoElectronico, String contrasena) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
    }

    public Integer getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Nacionalidad getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Nacionalidad nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNivelEstudio() {
        return nivelEstudio;
    }

    public void setNivelEstudio(String nivelEstudio) {
        this.nivelEstudio = nivelEstudio;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getExperienciaLaboral() {
        return experienciaLaboral;
    }

    public void setExperienciaLaboral(String experienciaLaboral) {
        this.experienciaLaboral = experienciaLaboral;
    }

    public Integer getAnosExperiencia() {
        return anosExperiencia;
    }

    public void setAnosExperiencia(Integer anosExperiencia) {
        this.anosExperiencia = anosExperiencia;
    }

    public String getConocimientosHerramientas() {
        return conocimientosHerramientas;
    }

    public void setConocimientosHerramientas(String conocimientosHerramientas) {
        this.conocimientosHerramientas = conocimientosHerramientas;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }

    public String getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<TrabajadorIdioma> getTrabajadorIdiomas() {
        return trabajadorIdiomas;
    }

    public void setTrabajadorIdiomas(List<TrabajadorIdioma> trabajadorIdiomas) {
        this.trabajadorIdiomas = trabajadorIdiomas;
    }

    public List<Postulacion> getPostulaciones() {
        return postulaciones;
    }

    public void setPostulaciones(List<Postulacion> postulaciones) {
        this.postulaciones = postulaciones;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno + (apellidoMaterno != null && !apellidoMaterno.isEmpty() ? " " + apellidoMaterno : "");
    }

    public String getDomicilioCompleto() {
        StringBuilder domicilio = new StringBuilder();
        if (calle != null && !calle.isEmpty()) {
            domicilio.append(calle);
        }
        if (colonia != null && !colonia.isEmpty()) {
            if (domicilio.length() > 0) domicilio.append(", ");
            domicilio.append(colonia);
        }
        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            if (domicilio.length() > 0) domicilio.append(", ");
            domicilio.append("C.P. ").append(codigoPostal);
        }
        return domicilio.length() > 0 ? domicilio.toString() : "No especificado";
    }

    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}