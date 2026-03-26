package com.example.trabajos;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.TrabajadorService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TrabajadoresDisponiblesController {

    private TableView<Trabajador> trabajadoresTable;
    private TableColumn<Trabajador, String> nombreColumn;
    private TableColumn<Trabajador, String> especialidadColumn;
    private TableColumn<Trabajador, String> nivelEstudioColumn;
    private TableColumn<Trabajador, String> experienciaColumn;
    private TableColumn<Trabajador, String> ubicacionColumn;
    private TableColumn<Trabajador, Void> accionesColumn;
    private TextField buscarField;
    private Label totalTrabajadoresLabel;

    private TrabajadorService trabajadorService = new TrabajadorService();
    private List<Trabajador> todosLosTrabajadores;

    // Setters para inyección desde EmpresasController
    public void setTrabajadoresTable(TableView<Trabajador> table) { this.trabajadoresTable = table; }
    public void setNombreColumn(TableColumn<Trabajador, String> col) { this.nombreColumn = col; }
    public void setEspecialidadColumn(TableColumn<Trabajador, String> col) { this.especialidadColumn = col; }
    public void setNivelEstudioColumn(TableColumn<Trabajador, String> col) { this.nivelEstudioColumn = col; }
    public void setExperienciaColumn(TableColumn<Trabajador, String> col) { this.experienciaColumn = col; }
    public void setUbicacionColumn(TableColumn<Trabajador, String> col) { this.ubicacionColumn = col; }
    public void setAccionesColumn(TableColumn<Trabajador, Void> col) { this.accionesColumn = col; }
    public void setBuscarField(TextField field) { this.buscarField = field; }
    public void setTotalTrabajadoresLabel(Label label) { this.totalTrabajadoresLabel = label; }

    public void initialize() {
        configurarColumnas();
        configurarBusqueda();
        cargarTrabajadores();
    }

    private void configurarColumnas() {
        if (nombreColumn != null) {
            nombreColumn.setCellValueFactory(cellData -> {
                Trabajador t = cellData.getValue();
                String nombreCompleto = t.getNombre() + " " +
                        (t.getApellidoPaterno() != null ? t.getApellidoPaterno() : "") + " " +
                        (t.getApellidoMaterno() != null ? t.getApellidoMaterno() : "");
                return new javafx.beans.property.SimpleStringProperty(nombreCompleto.trim());
            });
        }

        if (especialidadColumn != null) {
            especialidadColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getEspecialidad() != null && !cellData.getValue().getEspecialidad().isEmpty() ?
                                    cellData.getValue().getEspecialidad() : "No especificada"));
        }

        if (nivelEstudioColumn != null) {
            nivelEstudioColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getNivelEstudio() != null && !cellData.getValue().getNivelEstudio().isEmpty() ?
                                    cellData.getValue().getNivelEstudio() : "No especificado"));
        }

        if (experienciaColumn != null) {
            experienciaColumn.setCellValueFactory(cellData -> {
                Integer anos = cellData.getValue().getAnosExperiencia();
                String texto = anos != null ? anos + " años" : "No especificada";
                return new javafx.beans.property.SimpleStringProperty(texto);
            });
        }

        if (ubicacionColumn != null) {
            ubicacionColumn.setCellValueFactory(cellData -> {
                Trabajador t = cellData.getValue();
                StringBuilder ubicacion = new StringBuilder();
                if (t.getMunicipio() != null && t.getMunicipio().getNombreMunicipio() != null) {
                    ubicacion.append(t.getMunicipio().getNombreMunicipio());
                }
                if (t.getCiudad() != null && t.getCiudad().getNombreCiudad() != null) {
                    if (ubicacion.length() > 0) ubicacion.append(" - ");
                    ubicacion.append(t.getCiudad().getNombreCiudad());
                }
                return new javafx.beans.property.SimpleStringProperty(
                        ubicacion.length() > 0 ? ubicacion.toString() : "No especificada");
            });
        }

        if (accionesColumn != null) {
            accionesColumn.setCellFactory(param -> new TableCell<>() {
                private final Button verPerfilButton = new Button("👤 Ver Perfil");
                private final Button enviarOfertaButton = new Button("📨 Enviar Oferta Exclusiva");
                {
                    verPerfilButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verPerfilButton.setOnAction(event -> {
                        Trabajador trabajador = getTableView().getItems().get(getIndex());
                        abrirPerfilTrabajador(trabajador);
                    });
                    enviarOfertaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    enviarOfertaButton.setOnAction(event -> {
                        Trabajador trabajador = getTableView().getItems().get(getIndex());
                        abrirFormularioOfertaDirecta(trabajador);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else {
                        HBox hbox = new HBox(5);
                        hbox.getChildren().addAll(verPerfilButton, enviarOfertaButton);
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    private void configurarBusqueda() {
        if (buscarField != null) {
            buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarTrabajadores(newValue);
            });
            buscarField.setPromptText("Buscar por nombre, especialidad o habilidades...");
        }
    }

    private void cargarTrabajadores() {
        try {
            todosLosTrabajadores = trabajadorService.obtenerTodosTrabajadores();

            if (todosLosTrabajadores == null || todosLosTrabajadores.isEmpty()) {
                if (totalTrabajadoresLabel != null) totalTrabajadoresLabel.setText("Total de trabajadores: 0");
                return;
            }

            actualizarTabla(todosLosTrabajadores);
            if (totalTrabajadoresLabel != null) totalTrabajadoresLabel.setText("Total de trabajadores: " + todosLosTrabajadores.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarTrabajadores(String textoBusqueda) {
        if (todosLosTrabajadores == null || todosLosTrabajadores.isEmpty()) return;

        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            actualizarTabla(todosLosTrabajadores);
            if (totalTrabajadoresLabel != null) totalTrabajadoresLabel.setText("Total de trabajadores: " + todosLosTrabajadores.size());
            return;
        }

        String busqueda = textoBusqueda.toLowerCase().trim();
        List<Trabajador> filtrados = todosLosTrabajadores.stream()
                .filter(t -> {
                    String nombreCompleto = (t.getNombre() + " " +
                            (t.getApellidoPaterno() != null ? t.getApellidoPaterno() : "") + " " +
                            (t.getApellidoMaterno() != null ? t.getApellidoMaterno() : "")).toLowerCase();
                    String especialidad = t.getEspecialidad() != null ? t.getEspecialidad().toLowerCase() : "";
                    String habilidades = t.getHabilidades() != null ? t.getHabilidades().toLowerCase() : "";
                    String nivelEstudio = t.getNivelEstudio() != null ? t.getNivelEstudio().toLowerCase() : "";
                    return nombreCompleto.contains(busqueda) || especialidad.contains(busqueda) ||
                            habilidades.contains(busqueda) || nivelEstudio.contains(busqueda);
                })
                .collect(Collectors.toList());

        actualizarTabla(filtrados);
        if (totalTrabajadoresLabel != null) {
            totalTrabajadoresLabel.setText("Mostrando: " + filtrados.size() + " de " + todosLosTrabajadores.size() + " trabajadores");
        }
    }

    private void actualizarTabla(List<Trabajador> trabajadores) {
        if (trabajadoresTable == null) return;
        if (trabajadores.isEmpty()) {
            trabajadoresTable.setVisible(false);
        } else {
            trabajadoresTable.getItems().setAll(trabajadores);
            trabajadoresTable.setVisible(true);
        }
    }

    private void abrirPerfilTrabajador(Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajador.fxml"));
            Parent root = loader.load();

            DetalleTrabajadorController controller = loader.getController();
            controller.setTrabajador(trabajador);

            Stage stage = (Stage) trabajadoresTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            String nombreCompleto = trabajador.getNombre() + " " +
                    (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : "") + " " +
                    (trabajador.getApellidoMaterno() != null ? trabajador.getApellidoMaterno() : "");
            stage.setTitle("Perfil de " + nombreCompleto.trim());

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el perfil del trabajador: " + e.getMessage());
        }
    }

    private void abrirFormularioOfertaDirecta(Trabajador trabajador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/FormularioPrivado.fxml"));
            Parent root = loader.load();

            FormularioPrivadoController controller = loader.getController();
            controller.setTrabajadorDestino(trabajador);

            Stage stage = (Stage) trabajadoresTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            String nombreCompleto = trabajador.getNombre() + " " +
                    (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : "") + " " +
                    (trabajador.getApellidoMaterno() != null ? trabajador.getApellidoMaterno() : "");

            stage.setTitle("Enviar Oferta Exclusiva a " + nombreCompleto.trim());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de oferta exclusiva: " + e.getMessage());
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