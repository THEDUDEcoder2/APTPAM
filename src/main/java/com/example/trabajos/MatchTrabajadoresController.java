package com.example.trabajos;

import com.example.trabajos.models.*;
import com.example.trabajos.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchTrabajadoresController {

    // Campos de entrada
    private TextField puestoField;
    private TextField herramientasField;
    private ComboBox<String> cantidadIdiomasComboBox;
    private VBox idiomasContainer;
    private ComboBox<String> nivelEstudioComboBox;
    private TextField anosExperienciaField;
    private TextField edadMinField;
    private TextField edadMaxField;
    private ComboBox<String> generoComboBox;
    private ComboBox<String> municipioComboBox;
    private ComboBox<String> ciudadComboBox;
    private Button buscarButton;
    private Button limpiarButton;

    // Tabla de resultados
    private TableView<MatchService.MatchResult> resultadosTable;
    private TableColumn<MatchService.MatchResult, String> nombreColumn;
    private TableColumn<MatchService.MatchResult, String> puntajeColumn;
    private TableColumn<MatchService.MatchResult, String> edadColumn;
    private TableColumn<MatchService.MatchResult, String> especialidadColumn;
    private TableColumn<MatchService.MatchResult, String> experienciaColumn;
    private TableColumn<MatchService.MatchResult, String> ubicacionColumn;
    private TableColumn<MatchService.MatchResult, Void> accionesColumn;
    private Label mensajeLabel;

    private MatchService matchService = new MatchService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();
    private IdiomaService idiomaService = new IdiomaService();
    private TrabajadorService trabajadorService = new TrabajadorService();

    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();

    // Setters para inyección desde EmpresasController
    public void setPuestoField(TextField field) { this.puestoField = field; }
    public void setHerramientasField(TextField field) { this.herramientasField = field; }
    public void setCantidadIdiomasComboBox(ComboBox<String> cb) { this.cantidadIdiomasComboBox = cb; }
    public void setIdiomasContainer(VBox container) { this.idiomasContainer = container; }
    public void setNivelEstudioComboBox(ComboBox<String> cb) { this.nivelEstudioComboBox = cb; }
    public void setAnosExperienciaField(TextField field) { this.anosExperienciaField = field; }
    public void setEdadMinField(TextField field) { this.edadMinField = field; }
    public void setEdadMaxField(TextField field) { this.edadMaxField = field; }
    public void setGeneroComboBox(ComboBox<String> cb) { this.generoComboBox = cb; }
    public void setMunicipioComboBox(ComboBox<String> cb) { this.municipioComboBox = cb; }
    public void setCiudadComboBox(ComboBox<String> cb) { this.ciudadComboBox = cb; }
    public void setResultadosTable(TableView<MatchService.MatchResult> table) { this.resultadosTable = table; }
    public void setNombreColumn(TableColumn<MatchService.MatchResult, String> col) { this.nombreColumn = col; }
    public void setPuntajeColumn(TableColumn<MatchService.MatchResult, String> col) { this.puntajeColumn = col; }
    public void setEdadColumn(TableColumn<MatchService.MatchResult, String> col) { this.edadColumn = col; }
    public void setEspecialidadColumn(TableColumn<MatchService.MatchResult, String> col) { this.especialidadColumn = col; }
    public void setExperienciaColumn(TableColumn<MatchService.MatchResult, String> col) { this.experienciaColumn = col; }
    public void setUbicacionColumn(TableColumn<MatchService.MatchResult, String> col) { this.ubicacionColumn = col; }
    public void setAccionesColumn(TableColumn<MatchService.MatchResult, Void> col) { this.accionesColumn = col; }
    public void setMensajeLabel(Label label) { this.mensajeLabel = label; }
    public void setBuscarButton(Button button) { this.buscarButton = button; }
    public void setLimpiarButton(Button button) { this.limpiarButton = button; }

    @FXML
    public void initialize() {
        configurarCombos();
        configurarTabla();

        if (buscarButton != null) {
            buscarButton.setOnAction(e -> onBuscarMatchClick());
        }
        if (limpiarButton != null) {
            limpiarButton.setOnAction(e -> onLimpiarMatchClick());
        }
    }

    private void configurarCombos() {
        // Nivel de estudio
        if (nivelEstudioComboBox != null) {
            nivelEstudioComboBox.getItems().addAll(
                    "Primaria", "Secundaria", "Bachillerato", "Técnico",
                    "Licenciatura", "Maestría", "Doctorado"
            );
            nivelEstudioComboBox.setPromptText("Selecciona nivel mínimo");
        }

        // Género
        if (generoComboBox != null) {
            generoComboBox.getItems().addAll("AMBOS", "MASCULINO", "FEMENINO");
            generoComboBox.setValue("AMBOS");
        }

        // Municipio
        if (municipioComboBox != null) {
            try {
                for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                    municipioComboBox.getItems().add(m.getNombreMunicipio());
                }
            } catch (Exception e) {
                municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
            }
            municipioComboBox.getItems().add(0, "Todos");
            municipioComboBox.setValue("Todos");
        }

        // Ciudad
        if (ciudadComboBox != null) {
            ciudadComboBox.getItems().add("Todas");
            ciudadComboBox.setValue("Todas");
        }

        if (municipioComboBox != null) {
            municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
                cargarCiudadesPorMunicipio(newVal);
            });
        }

        // Idiomas
        if (cantidadIdiomasComboBox != null) {
            cantidadIdiomasComboBox.getItems().addAll("0", "1", "2", "3", "4", "5");
            cantidadIdiomasComboBox.setValue("0");
            cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
                generarComboBoxIdiomas(newVal);
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
                        abrirPerfilTrabajador(result.getTrabajador());
                    });

                    enviarOfertaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    enviarOfertaButton.setOnAction(event -> {
                        MatchService.MatchResult result = getTableView().getItems().get(getIndex());
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

        // Puesto
        if (puestoField != null && puestoField.getText() != null && !puestoField.getText().trim().isEmpty()) {
            criterios.setPuesto(puestoField.getText().trim());
        }

        // Herramientas
        if (herramientasField != null && herramientasField.getText() != null && !herramientasField.getText().trim().isEmpty()) {
            criterios.setHerramientas(herramientasField.getText().trim());
        }

        // Idiomas
        List<String> idiomas = new ArrayList<>();
        for (ComboBox<String> cb : idiomasComboBoxes) {
            if (cb.getValue() != null && !cb.getValue().isEmpty()) {
                idiomas.add(cb.getValue());
            }
        }
        criterios.setIdiomas(idiomas);

        // Nivel estudio
        if (nivelEstudioComboBox != null && nivelEstudioComboBox.getValue() != null) {
            criterios.setNivelEstudio(nivelEstudioComboBox.getValue());
        }

        // Años experiencia
        if (anosExperienciaField != null && anosExperienciaField.getText() != null && !anosExperienciaField.getText().isEmpty()) {
            try {
                criterios.setAnosExperiencia(Integer.parseInt(anosExperienciaField.getText()));
            } catch (NumberFormatException e) {}
        }

        // Edad
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

        // Género
        if (generoComboBox != null && generoComboBox.getValue() != null) {
            criterios.setGenero(generoComboBox.getValue());
        }

        // Ubicación
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
        }
    }

    @FXML
    private void onLimpiarMatchClick() {
        if (puestoField != null) puestoField.clear();
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
    }

    private void abrirPerfilTrabajador(com.example.trabajos.models.Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajador.fxml"));
            Parent root = loader.load();

            DetalleTrabajadorController controller = loader.getController();
            controller.setTrabajador(trabajador);

            Stage stage = (Stage) resultadosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            String nombreCompleto = trabajador.getNombre() + " " +
                    (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : "");
            stage.setTitle("Perfil de " + nombreCompleto);

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
            controller.setTrabajadorDestino(trabajador);

            Stage stage = (Stage) resultadosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            String nombreCompleto = trabajador.getNombre() + " " +
                    (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : "");
            stage.setTitle("Enviar Oferta Exclusiva a " + nombreCompleto);

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