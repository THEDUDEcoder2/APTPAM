package com.example.trabajos.services;

import com.example.trabajos.models.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class MatchService {

    private TrabajadorService trabajadorService = new TrabajadorService();
    private TrabajadorIdiomaService trabajadorIdiomaService = new TrabajadorIdiomaService();

    // Mapa de distancias entre ciudades de BCS
    private static final Map<String, Map<String, Integer>> DISTANCIAS = new HashMap<>();

    static {
        // Inicializar distancias aproximadas en km entre ciudades de BCS
        Map<String, Integer> laPaz = new HashMap<>();
        laPaz.put("La Paz", 0); laPaz.put("El Centenario", 10); laPaz.put("Cabo San Lucas", 180);
        laPaz.put("San José del Cabo", 160); laPaz.put("Todos Santos", 80); laPaz.put("Ciudad Constitución", 200);
        laPaz.put("Santa Rosalía", 400); laPaz.put("Loreto", 350); laPaz.put("Guerrero Negro", 600);

        Map<String, Integer> cabosSanLucas = new HashMap<>();
        cabosSanLucas.put("Cabo San Lucas", 0); cabosSanLucas.put("San José del Cabo", 30);
        cabosSanLucas.put("Todos Santos", 70); cabosSanLucas.put("La Paz", 180);
        cabosSanLucas.put("El Centenario", 190); cabosSanLucas.put("Ciudad Constitución", 380);

        Map<String, Integer> sanJoseCabo = new HashMap<>();
        sanJoseCabo.put("San José del Cabo", 0); sanJoseCabo.put("Cabo San Lucas", 30);
        sanJoseCabo.put("Todos Santos", 90); sanJoseCabo.put("La Paz", 160);
        sanJoseCabo.put("El Centenario", 170); sanJoseCabo.put("Ciudad Constitución", 360);

        Map<String, Integer> ciudadConstitucion = new HashMap<>();
        ciudadConstitucion.put("Ciudad Constitución", 0); ciudadConstitucion.put("La Paz", 200);
        ciudadConstitucion.put("El Centenario", 210); ciudadConstitucion.put("Loreto", 150);
        ciudadConstitucion.put("Santa Rosalía", 200); ciudadConstitucion.put("Cabo San Lucas", 380);
        ciudadConstitucion.put("San José del Cabo", 360);

        Map<String, Integer> loreto = new HashMap<>();
        loreto.put("Loreto", 0); loreto.put("Ciudad Constitución", 150);
        loreto.put("Santa Rosalía", 100); loreto.put("La Paz", 350);

        Map<String, Integer> santaRosalia = new HashMap<>();
        santaRosalia.put("Santa Rosalía", 0); santaRosalia.put("Loreto", 100);
        santaRosalia.put("Ciudad Constitución", 200); santaRosalia.put("Guerrero Negro", 200);
        santaRosalia.put("La Paz", 400);

        Map<String, Integer> guerreroNegro = new HashMap<>();
        guerreroNegro.put("Guerrero Negro", 0); guerreroNegro.put("Santa Rosalía", 200);
        guerreroNegro.put("Loreto", 300); guerreroNegro.put("La Paz", 600);

        DISTANCIAS.put("La Paz", laPaz);
        DISTANCIAS.put("El Centenario", laPaz);
        DISTANCIAS.put("Cabo San Lucas", cabosSanLucas);
        DISTANCIAS.put("San José del Cabo", sanJoseCabo);
        DISTANCIAS.put("Todos Santos", cabosSanLucas);
        DISTANCIAS.put("Ciudad Constitución", ciudadConstitucion);
        DISTANCIAS.put("Loreto", loreto);
        DISTANCIAS.put("Santa Rosalía", santaRosalia);
        DISTANCIAS.put("Guerrero Negro", guerreroNegro);
    }

    // Mapa de sinónimos y palabras relacionadas para puestos
    private static final Map<String, List<String>> SINONIMOS_PUESTOS = new HashMap<>();

    static {
        SINONIMOS_PUESTOS.put("asesor", Arrays.asList("consultor", "consejero", "especialista"));
        SINONIMOS_PUESTOS.put("consultor", Arrays.asList("asesor", "consejero", "especialista"));
        SINONIMOS_PUESTOS.put("administrativo", Arrays.asList("oficina", "asistente", "secretaria", "recepcionista"));
        SINONIMOS_PUESTOS.put("recepcionista", Arrays.asList("recepcion", "atención al cliente", "vigilancia"));
        SINONIMOS_PUESTOS.put("vigilancia", Arrays.asList("guardia", "seguridad", "recepcionista"));
        SINONIMOS_PUESTOS.put("seguridad", Arrays.asList("guardia", "vigilancia"));
        SINONIMOS_PUESTOS.put("tutor", Arrays.asList("profesor", "maestro", "enseñanza", "educador"));
        SINONIMOS_PUESTOS.put("profesor", Arrays.asList("tutor", "maestro", "enseñanza", "educador"));
        SINONIMOS_PUESTOS.put("jardineria", Arrays.asList("jardinero", "paisajista", "horticultura"));
        SINONIMOS_PUESTOS.put("limpieza", Arrays.asList("aseo", "housekeeping", "intendencia"));
        SINONIMOS_PUESTOS.put("repartidor", Arrays.asList("mensajero", "delivery", "chofer"));
        SINONIMOS_PUESTOS.put("chofer", Arrays.asList("conductor", "repartidor", "transportista"));
        SINONIMOS_PUESTOS.put("cuidado", Arrays.asList("cuidador", "asistente", "enfermero", "geriatra"));
        SINONIMOS_PUESTOS.put("telemercadeo", Arrays.asList("call center", "ventas telefónicas", "promotor"));
        SINONIMOS_PUESTOS.put("cocina", Arrays.asList("chef", "ayudante de cocina", "mesero"));
    }

    // Mapa de similitud de herramientas
    private static final Map<String, List<String>> SINONIMOS_HERRAMIENTAS = new HashMap<>();

    static {
        SINONIMOS_HERRAMIENTAS.put("excel", Arrays.asList("hoja de cálculo", "spreadsheet", "libreoffice calc"));
        SINONIMOS_HERRAMIENTAS.put("word", Arrays.asList("procesador de texto", "documentos", "libreoffice writer"));
        SINONIMOS_HERRAMIENTAS.put("powerpoint", Arrays.asList("presentaciones", "slides", "libreoffice impress"));
        SINONIMOS_HERRAMIENTAS.put("autocad", Arrays.asList("cad", "diseño asistido", "dibujo técnico"));
        SINONIMOS_HERRAMIENTAS.put("photoshop", Arrays.asList("edición de imágenes", "diseño gráfico", "illustrator"));
        SINONIMOS_HERRAMIENTAS.put("quickbooks", Arrays.asList("contabilidad", "facturación", "sistema contable"));
        SINONIMOS_HERRAMIENTAS.put("sap", Arrays.asList("erp", "sistema empresarial", "planificación recursos"));
    }

    public static class MatchResult {
        private Trabajador trabajador;
        private int puntaje;
        private List<String> coincidencias;
        private List<String> faltantes;
        private Map<String, Integer> desglose;

        public MatchResult(Trabajador trabajador, int puntaje, List<String> coincidencias,
                           List<String> faltantes, Map<String, Integer> desglose) {
            this.trabajador = trabajador;
            this.puntaje = puntaje;
            this.coincidencias = coincidencias;
            this.faltantes = faltantes;
            this.desglose = desglose;
        }

        public Trabajador getTrabajador() { return trabajador; }
        public int getPuntaje() { return puntaje; }
        public List<String> getCoincidencias() { return coincidencias; }
        public List<String> getFaltantes() { return faltantes; }
        public Map<String, Integer> getDesglose() { return desglose; }
        public String getPuntajeTexto() { return puntaje + "%"; }
    }

    public static class CriteriosMatch {
        private String puesto;
        private String herramientas;
        private List<String> idiomas;
        private String nivelEstudio;
        private Integer anosExperiencia;
        private String municipio;
        private String ciudad;
        private Integer edadMin;
        private Integer edadMax;
        private String genero; // "MASCULINO", "FEMENINO", "AMBOS"

        public CriteriosMatch() {
            this.idiomas = new ArrayList<>();
            this.genero = "AMBOS";
        }

        // Getters y Setters
        public String getPuesto() { return puesto; }
        public void setPuesto(String puesto) { this.puesto = puesto; }
        public String getHerramientas() { return herramientas; }
        public void setHerramientas(String herramientas) { this.herramientas = herramientas; }
        public List<String> getIdiomas() { return idiomas; }
        public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
        public String getNivelEstudio() { return nivelEstudio; }
        public void setNivelEstudio(String nivelEstudio) { this.nivelEstudio = nivelEstudio; }
        public Integer getAnosExperiencia() { return anosExperiencia; }
        public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }
        public String getMunicipio() { return municipio; }
        public void setMunicipio(String municipio) { this.municipio = municipio; }
        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }
        public Integer getEdadMin() { return edadMin; }
        public void setEdadMin(Integer edadMin) { this.edadMin = edadMin; }
        public Integer getEdadMax() { return edadMax; }
        public void setEdadMax(Integer edadMax) { this.edadMax = edadMax; }
        public String getGenero() { return genero; }
        public void setGenero(String genero) { this.genero = genero; }
    }

    public List<MatchResult> buscarMatches(CriteriosMatch criterios) {
        List<Trabajador> todos = trabajadorService.obtenerTodosTrabajadores();
        List<MatchResult> resultados = new ArrayList<>();

        for (Trabajador t : todos) {
            MatchResult result = calcularMatch(t, criterios);
            if (result.getPuntaje() > 0) {
                resultados.add(result);
            }
        }

        resultados.sort((a, b) -> Integer.compare(b.getPuntaje(), a.getPuntaje()));
        return resultados;
    }

    public MatchResult calcularMatch(Trabajador trabajador, CriteriosMatch criterios) {
        Map<String, Integer> desglose = new HashMap<>();
        List<String> coincidencias = new ArrayList<>();
        List<String> faltantes = new ArrayList<>();

        int puntajeTotal = 0;
        int maxPuntaje = 0;

        // 1. Puesto con palabras similares (15 puntos)
        maxPuntaje += 15;
        int puntajePuesto = calcularPuntajePuesto(trabajador, criterios);
        puntajeTotal += puntajePuesto;
        desglose.put("puesto", puntajePuesto);
        if (puntajePuesto > 10) coincidencias.add("✅ Puesto altamente compatible");
        else if (puntajePuesto > 5) coincidencias.add("✅ Puesto parcialmente compatible");
        else if (puntajePuesto > 0) coincidencias.add("✅ Puesto relacionado");
        else faltantes.add("❌ Puesto no relacionado con su perfil");

        // 2. Herramientas con similitud (12 puntos)
        maxPuntaje += 12;
        int puntajeHerramientas = calcularPuntajeHerramientas(trabajador, criterios);
        puntajeTotal += puntajeHerramientas;
        desglose.put("herramientas", puntajeHerramientas);
        if (puntajeHerramientas > 8) coincidencias.add("✅ Maneja las herramientas requeridas");
        else if (puntajeHerramientas > 4) coincidencias.add("✅ Maneja algunas herramientas requeridas");
        else if (puntajeHerramientas > 0) coincidencias.add("✅ Conoce herramientas similares");
        else faltantes.add("❌ No maneja las herramientas requeridas");

        // 3. Idiomas (10 puntos)
        maxPuntaje += 10;
        int puntajeIdiomas = calcularPuntajeIdiomas(trabajador, criterios);
        puntajeTotal += puntajeIdiomas;
        desglose.put("idiomas", puntajeIdiomas);
        if (puntajeIdiomas > 7) coincidencias.add("✅ Domina todos los idiomas requeridos");
        else if (puntajeIdiomas > 3) coincidencias.add("✅ Domina algunos idiomas requeridos");
        else if (puntajeIdiomas > 0) coincidencias.add("✅ Tiene conocimientos de idiomas");
        else faltantes.add("❌ No domina los idiomas requeridos");

        // 4. Nivel de estudio (10 puntos)
        maxPuntaje += 10;
        int puntajeEstudio = calcularPuntajeEstudio(trabajador, criterios);
        puntajeTotal += puntajeEstudio;
        desglose.put("estudio", puntajeEstudio);
        if (puntajeEstudio > 7) coincidencias.add("✅ Nivel de estudio superior al requerido");
        else if (puntajeEstudio > 4) coincidencias.add("✅ Nivel de estudio cumple el requisito");
        else if (puntajeEstudio > 0) coincidencias.add("✅ Nivel de estudio aceptable");
        else faltantes.add("❌ Nivel de estudio insuficiente");

        // 5. Experiencia (10 puntos)
        maxPuntaje += 10;
        int puntajeExperiencia = calcularPuntajeExperiencia(trabajador, criterios);
        puntajeTotal += puntajeExperiencia;
        desglose.put("experiencia", puntajeExperiencia);
        if (puntajeExperiencia > 7) coincidencias.add("✅ Experiencia superior a la requerida");
        else if (puntajeExperiencia > 4) coincidencias.add("✅ Experiencia cumple el requisito");
        else if (puntajeExperiencia > 0) coincidencias.add("✅ Experiencia cercana al requisito");
        else faltantes.add("❌ Experiencia insuficiente");

        // 6. Edad (8 puntos)
        maxPuntaje += 8;
        int puntajeEdad = calcularPuntajeEdad(trabajador, criterios);
        puntajeTotal += puntajeEdad;
        desglose.put("edad", puntajeEdad);
        if (puntajeEdad > 5) coincidencias.add("✅ Edad ideal para el puesto");
        else if (puntajeEdad > 2) coincidencias.add("✅ Edad aceptable");
        else if (puntajeEdad > 0) coincidencias.add("✅ Edad dentro del rango");
        else faltantes.add("❌ Edad fuera del rango deseado");

        // 7. Género (5 puntos)
        maxPuntaje += 5;
        int puntajeGenero = calcularPuntajeGenero(trabajador, criterios);
        puntajeTotal += puntajeGenero;
        desglose.put("genero", puntajeGenero);
        if (puntajeGenero > 0) coincidencias.add("✅ Género coincide con lo buscado");

        // 8. Ubicación (20 puntos) - LA MÁS IMPORTANTE
        maxPuntaje += 20;
        int puntajeUbicacion = calcularPuntajeUbicacion(trabajador, criterios);
        puntajeTotal += puntajeUbicacion;
        desglose.put("ubicacion", puntajeUbicacion);
        if (puntajeUbicacion == 20) coincidencias.add("✅ Misma ciudad que la vacante");
        else if (puntajeUbicacion >= 15) coincidencias.add("✅ Ciudad muy cercana");
        else if (puntajeUbicacion >= 10) coincidencias.add("✅ Distancia razonable");
        else if (puntajeUbicacion >= 5) coincidencias.add("✅ Acepta reubicación");
        else faltantes.add("❌ Ubicación muy lejana");

        int porcentaje = (puntajeTotal * 100) / maxPuntaje;

        return new MatchResult(trabajador, porcentaje, coincidencias, faltantes, desglose);
    }

    private int calcularPuntajePuesto(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getPuesto() == null || criterios.getPuesto().isEmpty()) {
            return 15; // Sin filtro, puntaje completo
        }

        String puestoReq = criterios.getPuesto().toLowerCase();
        String especialidad = trabajador.getEspecialidad() != null ? trabajador.getEspecialidad().toLowerCase() : "";
        String experiencia = trabajador.getExperienciaLaboral() != null ? trabajador.getExperienciaLaboral().toLowerCase() : "";
        String habilidades = trabajador.getHabilidades() != null ? trabajador.getHabilidades().toLowerCase() : "";

        // Coincidencia exacta
        if (especialidad.contains(puestoReq) || experiencia.contains(puestoReq)) {
            return 15;
        }

        // Buscar palabras clave del puesto
        String[] palabrasPuesto = puestoReq.split("\\s+");
        int coincidencias = 0;
        for (String palabra : palabrasPuesto) {
            if (palabra.length() > 3) {
                if (especialidad.contains(palabra) || experiencia.contains(palabra) || habilidades.contains(palabra)) {
                    coincidencias++;
                }
                // Buscar sinónimos
                List<String> sinonimos = SINONIMOS_PUESTOS.get(palabra);
                if (sinonimos != null) {
                    for (String sin : sinonimos) {
                        if (especialidad.contains(sin) || experiencia.contains(sin)) {
                            coincidencias++;
                            break;
                        }
                    }
                }
            }
        }

        if (palabrasPuesto.length > 0) {
            return (coincidencias * 15) / palabrasPuesto.length;
        }

        return 5; // Coincidencia parcial baja
    }

    private int calcularPuntajeHerramientas(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getHerramientas() == null || criterios.getHerramientas().isEmpty()) {
            return 12;
        }

        String herramientasTrabajador = trabajador.getConocimientosHerramientas() != null ?
                trabajador.getConocimientosHerramientas().toLowerCase() : "";

        String[] herramientasReq = criterios.getHerramientas().toLowerCase().split(",");
        int puntaje = 0;

        for (String hr : herramientasReq) {
            String herramientaLimpia = hr.trim();
            if (herramientasTrabajador.contains(herramientaLimpia)) {
                puntaje += 3;
            } else {
                // Buscar sinónimos
                List<String> sinonimos = SINONIMOS_HERRAMIENTAS.get(herramientaLimpia);
                if (sinonimos != null) {
                    for (String sin : sinonimos) {
                        if (herramientasTrabajador.contains(sin)) {
                            puntaje += 2;
                            break;
                        }
                    }
                }
            }
        }

        return Math.min(puntaje, 12);
    }

    private int calcularPuntajeIdiomas(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getIdiomas() == null || criterios.getIdiomas().isEmpty()) {
            return 10;
        }

        List<Idioma> idiomasTrabajador = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajador)
                .stream().map(ti -> ti.getIdioma()).collect(Collectors.toList());
        List<String> idiomasTrabajadorStr = idiomasTrabajador.stream()
                .map(i -> i.getNombreIdioma().toLowerCase())
                .collect(Collectors.toList());

        int coincidencias = 0;
        for (String idiomaReq : criterios.getIdiomas()) {
            if (idiomasTrabajadorStr.contains(idiomaReq.toLowerCase())) {
                coincidencias++;
            }
        }

        if (criterios.getIdiomas().isEmpty()) return 10;
        return (coincidencias * 10) / criterios.getIdiomas().size();
    }

    private int calcularPuntajeEstudio(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getNivelEstudio() == null || criterios.getNivelEstudio().isEmpty()) {
            return 10;
        }

        List<String> niveles = Arrays.asList("Primaria", "Secundaria", "Bachillerato", "Técnico", "Licenciatura", "Maestría", "Doctorado");
        String nivelTrabajador = trabajador.getNivelEstudio();
        String nivelRequerido = criterios.getNivelEstudio();

        int idxTrabajador = niveles.indexOf(nivelTrabajador);
        int idxRequerido = niveles.indexOf(nivelRequerido);

        if (idxTrabajador == -1) return 2;
        if (idxTrabajador >= idxRequerido) {
            int diferencia = idxTrabajador - idxRequerido;
            return Math.min(10, 8 + diferencia);
        }
        return Math.max(0, 10 - (idxRequerido - idxTrabajador) * 3);
    }

    private int calcularPuntajeExperiencia(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getAnosExperiencia() == null || criterios.getAnosExperiencia() == 0) {
            return 10;
        }

        Integer anosTrabajador = trabajador.getAnosExperiencia() != null ? trabajador.getAnosExperiencia() : 0;
        int anosReq = criterios.getAnosExperiencia();

        if (anosTrabajador >= anosReq) {
            return Math.min(10, 8 + (anosTrabajador - anosReq));
        }

        int diferencia = anosReq - anosTrabajador;
        if (diferencia <= 2) return 6;
        if (diferencia <= 4) return 3;
        return 1;
    }

    private int calcularPuntajeEdad(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getEdadMin() == null && criterios.getEdadMax() == null) {
            return 8;
        }

        int edad = trabajador.getEdad();
        int min = criterios.getEdadMin() != null ? criterios.getEdadMin() : 18;
        int max = criterios.getEdadMax() != null ? criterios.getEdadMax() : 99;

        if (edad >= min && edad <= max) {
            // Entre más cerca del centro del rango, mejor puntaje
            int centro = (min + max) / 2;
            int distancia = Math.abs(edad - centro);
            int rango = (max - min) / 2;
            if (rango == 0) rango = 1;
            int puntaje = 8 - (distancia * 8 / rango);
            return Math.max(4, puntaje);
        } else if (edad < min) {
            return Math.max(2, 6 - (min - edad));
        } else {
            return Math.max(2, 6 - (edad - max));
        }
    }

    private int calcularPuntajeGenero(Trabajador trabajador, CriteriosMatch criterios) {
        if (criterios.getGenero() == null || "AMBOS".equals(criterios.getGenero())) {
            return 5;
        }

        String generoTrabajador = trabajador.getGenero() != null ? trabajador.getGenero().getTipoGenero() : "";
        if (generoTrabajador.equalsIgnoreCase(criterios.getGenero())) {
            return 5;
        }
        return 0;
    }

    private int calcularPuntajeUbicacion(Trabajador trabajador, CriteriosMatch criterios) {
        String ciudadVacante = criterios.getCiudad();
        String municipioVacante = criterios.getMunicipio();

        if ((ciudadVacante == null || ciudadVacante.isEmpty() || "Todas".equals(ciudadVacante)) &&
                (municipioVacante == null || municipioVacante.isEmpty() || "Todos".equals(municipioVacante))) {
            return 20; // Sin restricción de ubicación
        }

        String ciudadTrabajador = trabajador.getCiudad() != null ? trabajador.getCiudad().getNombreCiudad() : "";
        String municipioTrabajador = trabajador.getMunicipio() != null ? trabajador.getMunicipio().getNombreMunicipio() : "";

        // Misma ciudad exacta
        if (!ciudadVacante.isEmpty() && !"Todas".equals(ciudadVacante) &&
                ciudadTrabajador.equalsIgnoreCase(ciudadVacante)) {
            return 20;
        }

        // Mismo municipio
        if (!municipioVacante.isEmpty() && !"Todos".equals(municipioVacante) &&
                municipioTrabajador.equalsIgnoreCase(municipioVacante)) {
            return 18;
        }

        // Calcular distancia si es posible
        if (!ciudadTrabajador.isEmpty() && !ciudadVacante.isEmpty() && !"Todas".equals(ciudadVacante)) {
            Map<String, Integer> distanciasDesde = DISTANCIAS.get(ciudadTrabajador);
            if (distanciasDesde != null && distanciasDesde.containsKey(ciudadVacante)) {
                int distancia = distanciasDesde.get(ciudadVacante);
                if (distancia <= 30) return 18;
                if (distancia <= 60) return 15;
                if (distancia <= 100) return 12;
                if (distancia <= 150) return 9;
                if (distancia <= 200) return 6;
                return 3;
            }
        }

        return 5; // Puntaje base por disposición a reubicarse
    }
}