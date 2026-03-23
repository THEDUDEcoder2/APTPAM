package com.example.trabajos;

public class Usuario {
    private String nombre;
    private String email;
    private String password;
    private boolean esEmpresa;
    private String tipoEmpresa;
    private String telefono;
    private String sectorActividad;
    private String actividadEconomicaPrincipal;
    private String calle;
    private String colonia;
    private String codigoPostal;
    private String rfc;
    private String razonSocial;


    public Usuario(String nombre, String email, String password, boolean esEmpresa) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.esEmpresa = esEmpresa;
    }

    public Usuario(String nombre, String email, String password, boolean esEmpresa,
                   String tipoEmpresa, String telefono, String sectorActividad,
                   String actividadEconomicaPrincipal, String domicilio,
                   String codigoPostal, String rfc, String razonSocial) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.esEmpresa = esEmpresa;
        this.tipoEmpresa = tipoEmpresa;
        this.telefono = telefono;
        this.sectorActividad = sectorActividad;
        this.actividadEconomicaPrincipal = actividadEconomicaPrincipal;

        if (domicilio != null && domicilio.contains(",")) {
            String[] partes = domicilio.split(",", 2);
            this.calle = partes[0].trim();
            this.colonia = partes[1].trim();
        } else {
            this.calle = domicilio;
            this.colonia = "";
        }

        this.codigoPostal = codigoPostal;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
    }

    public Usuario(String nombre, String email, String password, boolean esEmpresa,
                   String tipoEmpresa, String telefono, String sectorActividad,
                   String actividadEconomicaPrincipal, String calle, String colonia,
                   String codigoPostal, String rfc, String razonSocial) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.esEmpresa = esEmpresa;
        this.tipoEmpresa = tipoEmpresa;
        this.telefono = telefono;
        this.sectorActividad = sectorActividad;
        this.actividadEconomicaPrincipal = actividadEconomicaPrincipal;
        this.calle = calle;
        this.colonia = colonia;
        this.codigoPostal = codigoPostal;
        this.rfc = rfc;
        this.razonSocial = razonSocial;
    }

    public String getNombre() {
        return nombre != null ? nombre : "No especificado";
    }

    public String getEmail() {
        return email != null ? email : "No especificado";
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public boolean isEsEmpresa() {
        return esEmpresa;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa != null ? tipoEmpresa : "No especificado";
    }

    public String getTelefono() {
        return telefono != null ? telefono : "No especificado";
    }

    public String getSectorActividad() {
        return sectorActividad != null ? sectorActividad : "No especificado";
    }



    public String getDomicilio() {
        if (calle != null && colonia != null && !calle.isEmpty() && !colonia.isEmpty()) {
            return calle + ", " + colonia;
        } else if (calle != null && !calle.isEmpty()) {
            return calle;
        } else if (colonia != null && !colonia.isEmpty()) {
            return colonia;
        } else {
            return "No especificado";
        }
    }

    public String getCalle() {
        return calle != null ? calle : "No especificado";
    }

    public String getColonia() {
        return colonia != null ? colonia : "No especificado";
    }

    public String getCodigoPostal() {
        return codigoPostal != null ? codigoPostal : "No especificado";
    }

    public String getRfc() {
        return rfc != null ? rfc : "No especificado";
    }

    public String getRazonSocial() {
        return razonSocial != null ? razonSocial : "No especificado";
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEsEmpresa(boolean esEmpresa) {
        this.esEmpresa = esEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSectorActividad(String sectorActividad) {
        this.sectorActividad = sectorActividad;
    }

    public void setActividadEconomicaPrincipal(String actividadEconomicaPrincipal) {
        this.actividadEconomicaPrincipal = actividadEconomicaPrincipal;
    }

    public void setDomicilio(String domicilio) {
        if (domicilio != null && domicilio.contains(",")) {
            String[] partes = domicilio.split(",", 2);
            this.calle = partes[0].trim();
            this.colonia = partes[1].trim();
        } else {
            this.calle = domicilio;
            this.colonia = "";
        }
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDomicilioCompleto() {
        StringBuilder domicilioCompleto = new StringBuilder();

        if (calle != null && !calle.isEmpty()) {
            domicilioCompleto.append(calle);
        }

        if (colonia != null && !colonia.isEmpty()) {
            if (domicilioCompleto.length() > 0) {
                domicilioCompleto.append(", ");
            }
            domicilioCompleto.append(colonia);
        }

        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            if (domicilioCompleto.length() > 0) {
                domicilioCompleto.append(", ");
            }
            domicilioCompleto.append("C.P. ").append(codigoPostal);
        }

        if (domicilioCompleto.length() == 0) {
            return "No especificado";
        }

        return domicilioCompleto.toString();
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", esEmpresa=" + esEmpresa +
                ", tipoEmpresa='" + tipoEmpresa + '\'' +
                ", telefono='" + telefono + '\'' +
                ", calle='" + calle + '\'' +
                ", colonia='" + colonia + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}