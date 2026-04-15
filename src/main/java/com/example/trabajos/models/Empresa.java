package com.example.trabajos.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Empresa")
    private Integer idEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ciudad")
    private Ciudad ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_municipio")
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_sector_actividad")
    private SectorActividad sectorActividad;

    @Column(name = "Nombre_Empresa")
    private String nombreEmpresa;

    @Column(name = "Calle")
    private String calle;

    @Column(name = "Colonia")
    private String colonia;

    @Column(name = "Codigo_postal")
    private String codigoPostal;

    @Column(name = "Num_telefono")
    private String numTelefono;

    @Column(name = "Correo_Electronico")
    private String correoElectronico;

    @Column(name = "RFC", unique = true)
    private String rfc;

    @Column(name = "Tipo_empresa")
    private String tipoEmpresa;

    @Column(name = "Act_economica_principal")
    private String actEconomicaPrincipal;

    @Column(name = "Razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "Contrasena")
    private String contrasena;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oferta> ofertas = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulaciones = new ArrayList<>();

    public Empresa() {
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public SectorActividad getSectorActividad() {
        return sectorActividad;
    }

    public void setSectorActividad(SectorActividad sectorActividad) {
        this.sectorActividad = sectorActividad;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
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

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getActEconomicaPrincipal() {
        return actEconomicaPrincipal;
    }

    public void setActEconomicaPrincipal(String actEconomicaPrincipal) {
        this.actEconomicaPrincipal = actEconomicaPrincipal;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }

    public List<Postulacion> getPostulaciones() {
        return postulaciones;
    }

    public void setPostulaciones(List<Postulacion> postulaciones) {
        this.postulaciones = postulaciones;
    }

    public void addOferta(Oferta oferta) {
        ofertas.add(oferta);
        oferta.setEmpresa(this);
    }

    public void removeOferta(Oferta oferta) {
        ofertas.remove(oferta);
        oferta.setEmpresa(null);
    }

    public void addPostulacion(Postulacion postulacion) {
        postulaciones.add(postulacion);
        postulacion.setEmpresa(this);
    }

    public void removePostulacion(Postulacion postulacion) {
        postulaciones.remove(postulacion);
        postulacion.setEmpresa(null);
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
}