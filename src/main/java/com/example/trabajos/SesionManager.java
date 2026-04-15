package com.example.trabajos;

import com.example.trabajos.services.MatchService;
import java.util.ArrayList;
import java.util.List;

public class SesionManager {
    private static SesionManager instancia;
    private Usuario usuarioActual;

    // Estado del Match (persistente globalmente)
    private String matchPuesto = "Todos los puestos";
    private String matchHerramientas = "";
    private String matchCantidadIdiomas = "0";
    private String matchNivelEstudio = null;
    private String matchAnosExperiencia = "";
    private String matchEdadMin = "";
    private String matchEdadMax = "";
    private String matchGenero = "AMBOS";
    private String matchMunicipio = "Todos";
    private String matchCiudad = "Todas";
    private List<String> matchIdiomas = new ArrayList<>();
    private List<MatchService.MatchResult> matchResultados = new ArrayList<>();
    private boolean matchHayResultados = false;

    private SesionManager() {}

    public static SesionManager getInstancia() {
        if (instancia == null) {
            instancia = new SesionManager();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
        // Limpiar estado del Match al cerrar sesión
        limpiarEstadoMatch();
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public boolean esEmpresa() {
        return usuarioActual != null && usuarioActual.isEsEmpresa();
    }

    public boolean esTrabajador() {
        return usuarioActual != null && !usuarioActual.isEsEmpresa();
    }

    // ========== MÉTODOS PARA GUARDAR ESTADO DEL MATCH ==========
    public void guardarEstadoMatch(String puesto, String herramientas, String cantidadIdiomas,
                                   String nivelEstudio, String anosExperiencia, String edadMin,
                                   String edadMax, String genero, String municipio, String ciudad,
                                   List<String> idiomas, List<MatchService.MatchResult> resultados, boolean hayResultados) {
        this.matchPuesto = puesto;
        this.matchHerramientas = herramientas;
        this.matchCantidadIdiomas = cantidadIdiomas;
        this.matchNivelEstudio = nivelEstudio;
        this.matchAnosExperiencia = anosExperiencia;
        this.matchEdadMin = edadMin;
        this.matchEdadMax = edadMax;
        this.matchGenero = genero;
        this.matchMunicipio = municipio;
        this.matchCiudad = ciudad;
        this.matchIdiomas.clear();
        this.matchIdiomas.addAll(idiomas);
        this.matchResultados.clear();
        this.matchResultados.addAll(resultados);
        this.matchHayResultados = hayResultados;
    }

    public String getMatchPuesto() { return matchPuesto; }
    public String getMatchHerramientas() { return matchHerramientas; }
    public String getMatchCantidadIdiomas() { return matchCantidadIdiomas; }
    public String getMatchNivelEstudio() { return matchNivelEstudio; }
    public String getMatchAnosExperiencia() { return matchAnosExperiencia; }
    public String getMatchEdadMin() { return matchEdadMin; }
    public String getMatchEdadMax() { return matchEdadMax; }
    public String getMatchGenero() { return matchGenero; }
    public String getMatchMunicipio() { return matchMunicipio; }
    public String getMatchCiudad() { return matchCiudad; }
    public List<String> getMatchIdiomas() { return new ArrayList<>(matchIdiomas); }
    public List<MatchService.MatchResult> getMatchResultados() { return new ArrayList<>(matchResultados); }
    public boolean getMatchHayResultados() { return matchHayResultados; }

    public void limpiarEstadoMatch() {
        matchPuesto = "Todos los puestos";
        matchHerramientas = "";
        matchCantidadIdiomas = "0";
        matchNivelEstudio = null;
        matchAnosExperiencia = "";
        matchEdadMin = "";
        matchEdadMax = "";
        matchGenero = "AMBOS";
        matchMunicipio = "Todos";
        matchCiudad = "Todas";
        matchIdiomas.clear();
        matchResultados.clear();
        matchHayResultados = false;
    }
}