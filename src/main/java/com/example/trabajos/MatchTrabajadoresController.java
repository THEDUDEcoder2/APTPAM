package com.example.trabajos;

import com.example.trabajos.models.*;
import com.example.trabajos.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchTrabajadoresController {

    // Campos de entrada
    @FXML private ComboBox<String> puestoComboBox;
    @FXML private TextField herramientasField;
    @FXML private ComboBox<String> cantidadIdiomasComboBox;
    @FXML private VBox idiomasContainer;
    @FXML private ComboBox<String> nivelEstudioComboBox;
    @FXML private TextField anosExperienciaField;
    @FXML private TextField edadMinField;
    @FXML private TextField edadMaxField;
    @FXML private ComboBox<String> generoComboBox;
    @FXML private ComboBox<String> municipioComboBox;
    @FXML private ComboBox<String> ciudadComboBox;
    @FXML private Button buscarMatchButton;
    @FXML private Button limpiarButton;
    @FXML private Button volverButton;

    // Tabla de resultados
    @FXML private TableView<MatchService.MatchResult> resultadosTable;
    @FXML private TableColumn<MatchService.MatchResult, String> nombreColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> puntajeColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> edadColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> especialidadColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> experienciaColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> ubicacionColumn;
    @FXML private TableColumn<MatchService.MatchResult, Void> accionesColumn;
    @FXML private Label mensajeLabel;

    private MatchService matchService = new MatchService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();
    private IdiomaService idiomaService = new IdiomaService();
    private TrabajadorService trabajadorService = new TrabajadorService();

    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();
    private boolean isLoading = false; // Evitar bucles infinitos

    @FXML
    public void initialize() {
        configurarCombos();
        configurarTabla();
        configurarValidadores();
        restaurarEstado();
    }

    private void guardarEstado() {
        if (isLoading) return;

        List<String> idiomas = new ArrayList<>();
        for (ComboBox<String> cb : idiomasComboBoxes) {
            if (cb.getValue() != null && !cb.getValue().isEmpty()) {
                idiomas.add(cb.getValue());
            }
        }

        List<MatchService.MatchResult> resultados = new ArrayList<>(resultadosTable.getItems());

        SesionManager.getInstancia().guardarEstadoMatch(
                puestoComboBox.getValue(),
                herramientasField.getText(),
                cantidadIdiomasComboBox.getValue(),
                nivelEstudioComboBox.getValue(),
                anosExperienciaField.getText(),
                edadMinField.getText(),
                edadMaxField.getText(),
                generoComboBox.getValue(),
                municipioComboBox.getValue(),
                ciudadComboBox.getValue(),
                idiomas,
                resultados,
                !resultados.isEmpty()
        );
    }

    private void restaurarEstado() {
        isLoading = true;

        SesionManager sm = SesionManager.getInstancia();

        // Restaurar filtros
        if (puestoComboBox != null) puestoComboBox.setValue(sm.getMatchPuesto());
        if (herramientasField != null) herramientasField.setText(sm.getMatchHerramientas());
        if (cantidadIdiomasComboBox != null) cantidadIdiomasComboBox.setValue(sm.getMatchCantidadIdiomas());
        if (nivelEstudioComboBox != null) nivelEstudioComboBox.setValue(sm.getMatchNivelEstudio());
        if (anosExperienciaField != null) anosExperienciaField.setText(sm.getMatchAnosExperiencia());
        if (edadMinField != null) edadMinField.setText(sm.getMatchEdadMin());
        if (edadMaxField != null) edadMaxField.setText(sm.getMatchEdadMax());
        if (generoComboBox != null) generoComboBox.setValue(sm.getMatchGenero());
        if (municipioComboBox != null) municipioComboBox.setValue(sm.getMatchMunicipio());

        // Restaurar ciudad (con delay para cargar las opciones)
        String ciudadGuardada = sm.getMatchCiudad();
        if (municipioComboBox != null && ciudadComboBox != null && sm.getMatchMunicipio() != null) {
            String municipio = sm.getMatchMunicipio();
            if (!"Todos".equals(municipio)) {
                javafx.application.Platform.runLater(() -> {
                    cargarCiudadesPorMunicipio(municipio);
                    javafx.application.Platform.runLater(() -> {
                        if (ciudadComboBox.getItems().contains(ciudadGuardada)) {
                            ciudadComboBox.setValue(ciudadGuardada);
                        } else {
                            ciudadComboBox.setValue("Todas");
                        }
                    });
                });
            } else {
                ciudadComboBox.setValue("Todas");
            }
        }

        // Restaurar idiomas (se cargarán después de generar los ComboBox)
        List<String> idiomasGuardados = sm.getMatchIdiomas();
        if (!idiomasGuardados.isEmpty()) {
            javafx.application.Platform.runLater(() -> {
                for (int i = 0; i < idiomasGuardados.size() && i < idiomasComboBoxes.size(); i++) {
                    idiomasComboBoxes.get(i).setValue(idiomasGuardados.get(i));
                }
            });
        }

        // Restaurar resultados de la tabla
        if (sm.getMatchHayResultados()) {
            List<MatchService.MatchResult> resultados = sm.getMatchResultados();
            if (!resultados.isEmpty()) {
                javafx.application.Platform.runLater(() -> {
                    resultadosTable.getItems().clear();
                    resultadosTable.getItems().addAll(resultados);
                    resultadosTable.setVisible(true);
                    resultadosTable.refresh();
                    if (mensajeLabel != null) mensajeLabel.setVisible(false);
                });
            }
        } else {
            resultadosTable.setVisible(false);
        }

        isLoading = false;
    }

    private void configurarValidadores() {
        if (anosExperienciaField != null) {
            anosExperienciaField.textProperty().addListener((obs, old, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    anosExperienciaField.setText(newVal.replaceAll("[^\\d]", ""));
                }
                if (!newVal.isEmpty()) {
                    try {
                        int valor = Integer.parseInt(newVal);
                        if (valor > 100) {
                            anosExperienciaField.setText("100");
                        }
                    } catch (NumberFormatException e) {}
                }
                guardarEstado();
            });
        }

        if (edadMinField != null) {
            edadMinField.textProperty().addListener((obs, old, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    edadMinField.setText(newVal.replaceAll("[^\\d]", ""));
                }
                if (!newVal.isEmpty()) {
                    try {
                        int valor = Integer.parseInt(newVal);
                        if (valor > 120) {
                            edadMinField.setText("120");
                        }
                    } catch (NumberFormatException e) {}
                }
                guardarEstado();
            });
        }

        if (edadMaxField != null) {
            edadMaxField.textProperty().addListener((obs, old, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    edadMaxField.setText(newVal.replaceAll("[^\\d]", ""));
                }
                if (!newVal.isEmpty()) {
                    try {
                        int valor = Integer.parseInt(newVal);
                        if (valor > 120) {
                            edadMaxField.setText("120");
                        }
                    } catch (NumberFormatException e) {}
                }
                guardarEstado();
            });
        }

        if (herramientasField != null) {
            herramientasField.textProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && !newVal.isEmpty() && !newVal.startsWith(" ")) {
                    if (old == null || old.isEmpty() || old.length() == 1) {
                        String formateado = formatearPrimeraLetraMayuscula(newVal);
                        if (!formateado.equals(newVal)) {
                            herramientasField.setText(formateado);
                        }
                    }
                }
                guardarEstado();
            });
        }
    }

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void configurarCombos() {
        if (puestoComboBox != null) {
            puestoComboBox.getItems().addAll(
                    "Todos los puestos",
                    "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                    "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                    "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                    "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante"
            );
            puestoComboBox.valueProperty().addListener((obs, old, newVal) -> guardarEstado());
        }

        if (nivelEstudioComboBox != null) {
            nivelEstudioComboBox.getItems().addAll(
                    "Primaria", "Secundaria", "Bachillerato", "Técnico",
                    "Licenciatura", "Maestría", "Doctorado"
            );
            nivelEstudioComboBox.setPromptText("Selecciona nivel mínimo");
            nivelEstudioComboBox.valueProperty().addListener((obs, old, newVal) -> guardarEstado());
        }

        if (generoComboBox != null) {
            generoComboBox.getItems().addAll("AMBOS", "MASCULINO", "FEMENINO");
            generoComboBox.valueProperty().addListener((obs, old, newVal) -> guardarEstado());
        }

        if (municipioComboBox != null) {
            try {
                for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                    municipioComboBox.getItems().add(m.getNombreMunicipio());
                }
            } catch (Exception e) {
                municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
            }
            municipioComboBox.getItems().add(0, "Todos");
            municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
                cargarCiudadesPorMunicipio(newVal);
                guardarEstado();
            });
        }

        if (ciudadComboBox != null) {
            ciudadComboBox.getItems().add("Todas");
            ciudadComboBox.valueProperty().addListener((obs, old, newVal) -> guardarEstado());
        }

        if (cantidadIdiomasComboBox != null) {
            cantidadIdiomasComboBox.getItems().addAll("0", "1", "2", "3", "4", "5");
            cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
                generarComboBoxIdiomas(newVal);
                guardarEstado();
            });
        }
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
        if (ciudadComboBox == null) return;

        ciudadComboBox.getItems().clear();
        ciudadComboBox.getItems().add("Todas");

        if (nombreMunicipio == null || "Todos".equals(nombreMunicipio)) {
            ciudadComboBox.setValue("Todas");
            return;
        }

        try {
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(nombreMunicipio);
            if (municipio != null) {
                for (Ciudad c : ciudadService.obtenerCiudadesPorMunicipio(municipio)) {
                    ciudadComboBox.getItems().add(c.getNombreCiudad());
                }
            }
        } catch (Exception e) {
            switch (nombreMunicipio) {
                case "Comondú":
                    ciudadComboBox.getItems().addAll("Ciudad Constitución", "Puerto San Carlos", "Puerto Adolfo López Mateos");
                    break;
                case "La Paz":
                    ciudadComboBox.getItems().addAll("La Paz", "El Centenario", "El Sargento", "La Ventana", "La Ribera");
                    break;
                case "Loreto":
                    ciudadComboBox.getItems().addAll("Loreto", "Puerto Agua Verde", "Ensenada Blanca", "Ligüí", "San Javier");
                    break;
                case "Los Cabos":
                    ciudadComboBox.getItems().addAll("Cabo San Lucas", "San José del Cabo", "Santiago", "Miraflores", "Todos Santos");
                    break;
                case "Mulegé":
                    ciudadComboBox.getItems().addAll("Santa Rosalía", "Mulegé", "Guerrero Negro", "San Ignacio", "Bahía Tortugas");
                    break;
            }
        }
        ciudadComboBox.setValue("Todas");
    }

    private void generarComboBoxIdiomas(String cantidad) {
        if (idiomasContainer == null) return;

        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null && !"0".equals(cantidad)) {
            int numIdiomas = Integer.parseInt(cantidad);
            HBox hbox = new HBox(10);
            for (int i = 0; i < numIdiomas; i++) {
                ComboBox<String> comboBox = new ComboBox<>();
                try {
                    for (Idioma idioma : idiomaService.obtenerTodosIdiomas()) {
                        comboBox.getItems().add(idioma.getNombreIdioma());
                    }
                } catch (Exception e) {
                    comboBox.getItems().addAll("Español", "Inglés", "Francés", "Alemán", "Italiano", "Portugués");
                }
                comboBox.setPromptText("Idioma " + (i + 1));
                comboBox.setPrefWidth(150);
                comboBox.valueProperty().addListener((obs, old, newVal) -> guardarEstado());
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
        }
    }

    private void configurarTabla() {
        if (nombreColumn != null) {
            nombreColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                String nombre = r.getTrabajador().getNombre() + " " +
                        (r.getTrabajador().getApellidoPaterno() != null ? r.getTrabajador().getApellidoPaterno() : "");
                return new javafx.beans.property.SimpleStringProperty(nombre);
            });
        }

        if (puntajeColumn != null) {
            puntajeColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(r.getPuntaje() + "%");
            });

            puntajeColumn.setCellFactory(column -> new TableCell<MatchService.MatchResult, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        int puntaje = Integer.parseInt(item.replace("%", ""));
                        if (puntaje >= 80) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if (puntaje >= 60) {
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #e74c3c;");
                        }
                    }
                }
            });
        }

        if (edadColumn != null) {
            edadColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                int edad = r.getTrabajador().getEdad();
                return new javafx.beans.property.SimpleStringProperty(edad + " años");
            });
        }

        if (especialidadColumn != null) {
            especialidadColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                String esp = r.getTrabajador().getEspecialidad() != null ?
                        r.getTrabajador().getEspecialidad() : "No especificada";
                return new javafx.beans.property.SimpleStringProperty(esp);
            });
        }

        if (experienciaColumn != null) {
            experienciaColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                Integer anos = r.getTrabajador().getAnosExperiencia();
                String exp = anos != null ? anos + " años" : "No especificada";
                return new javafx.beans.property.SimpleStringProperty(exp);
            });
        }

        if (ubicacionColumn != null) {
            ubicacionColumn.setCellValueFactory(cellData -> {
                MatchService.MatchResult r = cellData.getValue();
                String ubicacion = "";
                if (r.getTrabajador().getMunicipio() != null) {
                    ubicacion = r.getTrabajador().getMunicipio().getNombreMunicipio();
                }
                if (r.getTrabajador().getCiudad() != null) {
                    if (!ubicacion.isEmpty()) ubicacion += " - ";
                    ubicacion += r.getTrabajador().getCiudad().getNombreCiudad();
                }
                return new javafx.beans.property.SimpleStringProperty(ubicacion.isEmpty() ? "No especificada" : ubicacion);
            });
        }

        if (accionesColumn != null) {
            accionesColumn.setCellFactory(param -> new TableCell<>() {
                private final Button verPerfilButton = new Button("👤 Ver perfil");
                private final Button enviarOfertaButton = new Button("📨 Enviar oferta");

                {
                    verPerfilButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verPerfilButton.setOnAction(event -> {
                        MatchService.MatchResult result = getTableView().getItems().get(getIndex());
                        guardarEstado();
                        abrirPerfilTrabajador(result.getTrabajador());
                    });

                    enviarOfertaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    enviarOfertaButton.setOnAction(event -> {
                        MatchService.MatchResult result = getTableView().getItems().get(getIndex());
                        guardarEstado();
                        abrirFormularioOferta(result.getTrabajador());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox hbox = new HBox(5);
                        hbox.getChildren().addAll(verPerfilButton, enviarOfertaButton);
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    @FXML
    private void onBuscarMatchClick() {
        if (resultadosTable == null) return;

        MatchService.CriteriosMatch criterios = new MatchService.CriteriosMatch();

        if (puestoComboBox != null && puestoComboBox.getValue() != null && !"Todos los puestos".equals(puestoComboBox.getValue())) {
            criterios.setPuesto(puestoComboBox.getValue());
        }

        if (herramientasField != null && herramientasField.getText() != null && !herramientasField.getText().trim().isEmpty()) {
            criterios.setHerramientas(formatearPrimeraLetraMayuscula(herramientasField.getText().trim()));
        }

        List<String> idiomas = new ArrayList<>();
        for (ComboBox<String> cb : idiomasComboBoxes) {
            if (cb.getValue() != null && !cb.getValue().isEmpty()) {
                idiomas.add(cb.getValue());
            }
        }
        criterios.setIdiomas(idiomas);

        if (nivelEstudioComboBox != null && nivelEstudioComboBox.getValue() != null) {
            criterios.setNivelEstudio(nivelEstudioComboBox.getValue());
        }

        if (anosExperienciaField != null && anosExperienciaField.getText() != null && !anosExperienciaField.getText().isEmpty()) {
            try {
                int valor = Integer.parseInt(anosExperienciaField.getText());
                if (valor <= 100) {
                    criterios.setAnosExperiencia(valor);
                }
            } catch (NumberFormatException e) {}
        }

        if (edadMinField != null && edadMinField.getText() != null && !edadMinField.getText().isEmpty()) {
            try {
                criterios.setEdadMin(Integer.parseInt(edadMinField.getText()));
            } catch (NumberFormatException e) {}
        }
        if (edadMaxField != null && edadMaxField.getText() != null && !edadMaxField.getText().isEmpty()) {
            try {
                criterios.setEdadMax(Integer.parseInt(edadMaxField.getText()));
            } catch (NumberFormatException e) {}
        }

        if (generoComboBox != null && generoComboBox.getValue() != null) {
            criterios.setGenero(generoComboBox.getValue());
        }

        if (municipioComboBox != null && municipioComboBox.getValue() != null && !"Todos".equals(municipioComboBox.getValue())) {
            criterios.setMunicipio(municipioComboBox.getValue());
        }
        if (ciudadComboBox != null && ciudadComboBox.getValue() != null && !"Todas".equals(ciudadComboBox.getValue())) {
            criterios.setCiudad(ciudadComboBox.getValue());
        }

        List<MatchService.MatchResult> resultados = matchService.buscarMatches(criterios);

        if (resultados.isEmpty()) {
            if (mensajeLabel != null) {
                mensajeLabel.setText("No se encontraron candidatos que coincidan con los criterios seleccionados.");
                mensajeLabel.setVisible(true);
            }
            resultadosTable.setVisible(false);
        } else {
            if (mensajeLabel != null) mensajeLabel.setVisible(false);
            resultadosTable.setVisible(true);
            resultadosTable.getItems().setAll(resultados);
            resultadosTable.refresh();
        }
        guardarEstado();
    }

    @FXML
    private void onLimpiarClick() {
        if (puestoComboBox != null) puestoComboBox.setValue("Todos los puestos");
        if (herramientasField != null) herramientasField.clear();
        if (cantidadIdiomasComboBox != null) cantidadIdiomasComboBox.setValue("0");
        if (nivelEstudioComboBox != null) nivelEstudioComboBox.setValue(null);
        if (anosExperienciaField != null) anosExperienciaField.clear();
        if (edadMinField != null) edadMinField.clear();
        if (edadMaxField != null) edadMaxField.clear();
        if (generoComboBox != null) generoComboBox.setValue("AMBOS");
        if (municipioComboBox != null) municipioComboBox.setValue("Todos");
        if (ciudadComboBox != null) ciudadComboBox.setValue("Todas");

        if (idiomasContainer != null) idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (resultadosTable != null) {
            resultadosTable.getItems().clear();
            resultadosTable.setVisible(false);
        }
        if (mensajeLabel != null) mensajeLabel.setVisible(false);

        guardarEstado();
    }

    @FXML
    private void onVolverClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar: " + e.getMessage());
        }
    }

    private void abrirPerfilTrabajador(com.example.trabajos.models.Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajador.fxml"));
            Parent root = loader.load();

            DetalleTrabajadorController controller = loader.getController();
            controller.setTrabajadorDesdeMatch(trabajador);

            Stage stage = new Stage();
            stage.setTitle("Perfil de " + trabajador.getNombreCompleto());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(volverButton.getScene().getWindow());
            stage.showAndWait();

            // Al regresar, restaurar estado
            restaurarEstado();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el perfil: " + e.getMessage());
        }
    }

    private void abrirFormularioOferta(com.example.trabajos.models.Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/FormularioPrivado.fxml"));
            Parent root = loader.load();

            FormularioPrivadoController controller = loader.getController();
            controller.setTrabajadorDestinoDesdeMatch(trabajador);

            Stage stage = new Stage();
            stage.setTitle("Enviar Oferta Exclusiva a " + trabajador.getNombreCompleto());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(volverButton.getScene().getWindow());
            stage.showAndWait();

            // Al regresar, restaurar estado
            restaurarEstado();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}