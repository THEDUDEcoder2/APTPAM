package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDateTime;
import java.util.List;

public class DetalleFormularioController {

    @FXML private Label nombreEmpresaLabel;
    @FXML private Label herramientaLabel;
    @FXML private Label idiomasLabel;
    @FXML private Label domicilioLabel;
    @FXML private Label gmailLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label puestoLabel;
    @FXML private Label horarioLabel;
    @FXML private Label sueldoLabel;
    @FXML private Label montoSueldoLabel;
    @FXML private Label nivelEstudioLabel;
    @FXML private Label experienciaLabel;
    @FXML private Label descripcionLabel;

    @FXML private TabPane tabPane;
    @FXML private Tab detalleTab;
    @FXML private Tab postulantesTab;

    @FXML private TableView<Postulacion> postulantesTable;
    @FXML private TableColumn<Postulacion, String> nombreColumn;
    @FXML private TableColumn<Postulacion, String> telefonoColumn;
    @FXML private TableColumn<Postulacion, String> edadColumn;
    @FXML private TableColumn<Postulacion, String> estadoColumn;
    @FXML private TableColumn<Postulacion, Void> accionesColumn;

    @FXML private Button volverButton;
    @FXML private Button editarButton;

    private boolean soloLectura = false;
    private boolean esDesdeEmpresas = false;
    private Oferta ofertaActual;
    private Trabajador trabajadorActual;

    private PostulacionService postulacionService = new PostulacionService();

    @FXML
    public void initialize() {
        configurarTablaPostulantes();
        configurarSeleccionTabla();

        if (!esDesdeEmpresas && postulantesTab != null) {
            tabPane.getTabs().remove(postulantesTab);
        }
    }

    private void configurarTablaPostulantes() {
        if (nombreColumn != null) {
            nombreColumn.setCellValueFactory(cellData -> {
                String nombre = "No disponible";
                if (cellData.getValue().getTrabajador() != null) {
                    nombre = cellData.getValue().getTrabajador().getNombreCompleto();
                }
                return new javafx.beans.property.SimpleStringProperty(nombre);
            });
        }

        if (telefonoColumn != null) {
            telefonoColumn.setCellValueFactory(cellData -> {
                String telefono = "No disponible";
                if (cellData.getValue().getTrabajador() != null) {
                    telefono = cellData.getValue().getTrabajador().getNumTelefono();
                }
                return new javafx.beans.property.SimpleStringProperty(telefono);
            });
        }

        if (edadColumn != null) {
            edadColumn.setCellValueFactory(cellData -> {
                Postulacion postulacion = cellData.getValue();
                if (postulacion.getTrabajador() != null && postulacion.getTrabajador().getFechaNacimiento() != null) {
                    int edad = Period.between(postulacion.getTrabajador().getFechaNacimiento(), LocalDate.now()).getYears();
                    return new javafx.beans.property.SimpleStringProperty(edad + " años");
                }
                return new javafx.beans.property.SimpleStringProperty("No especificada");
            });
        }

        if (estadoColumn != null) {
            estadoColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));

            estadoColumn.setCellFactory(column -> new TableCell<Postulacion, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String estado = item.toUpperCase();
                        switch (estado) {
                            case "PENDIENTE":
                                setText("⏳ EN ESPERA");
                                setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                                break;
                            case "ACEPTADO":
                                setText("✅ ACEPTADO");
                                setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                                break;
                            case "RECHAZADO":
                                setText("❌ RECHAZADO");
                                setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                                break;
                            default:
                                setText(estado);
                                setStyle("-fx-text-fill: #7f8c8d;");
                                break;
                        }
                    }
                }
            });
        }

        if (accionesColumn != null) {
            accionesColumn.setCellFactory(param -> new TableCell<>() {
                private final Button verPerfilButton = new Button("👤 Ver Perfil");
                private final Button notasButton = new Button("✏️ Agregar Nota");
                private final Button verNotaTrabajadorButton = new Button("📝 Ver Nota");

                {
                    verPerfilButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verPerfilButton.setOnAction(event -> {
                        Postulacion postulacion = getTableView().getItems().get(getIndex());
                        abrirDetalleTrabajador(postulacion);
                    });

                    notasButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    notasButton.setOnAction(event -> {
                        Postulacion postulacion = getTableView().getItems().get(getIndex());
                        abrirNotas(postulacion);
                    });

                    verNotaTrabajadorButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verNotaTrabajadorButton.setOnAction(event -> {
                        Postulacion postulacion = getTableView().getItems().get(getIndex());
                        mostrarNotaTrabajador(postulacion);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Postulacion postulacion = getTableView().getItems().get(getIndex());
                        HBox hbox = new HBox(5);
                        hbox.getChildren().add(verPerfilButton);

                        if (postulacion != null && "ACEPTADO".equalsIgnoreCase(postulacion.getEstado())) {
                            hbox.getChildren().add(notasButton);
                        }

                        if (postulacion != null && postulacion.tieneNotaEmpresa() &&
                                postulacion.getNotaEmpresa().contains("RAZÓN DEL TRABAJADOR")) {
                            hbox.getChildren().add(verNotaTrabajadorButton);
                        }

                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    private void configurarSeleccionTabla() {
        if (postulantesTable != null) {
            postulantesTable.setRowFactory(tv -> {
                TableRow<Postulacion> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && !row.isEmpty()) {
                        Postulacion postulacion = row.getItem();
                        abrirDetalleTrabajador(postulacion);
                    }
                });
                return row;
            });
        }
    }

    private void mostrarNotaTrabajador(Postulacion postulacion) {
        if (postulacion != null && postulacion.tieneNotaEmpresa()) {
            String nota = postulacion.getNotaEmpresa();
            if (nota.contains("RAZÓN DEL TRABAJADOR")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("📝 Nota del Trabajador");
                alert.setHeaderText("Razón proporcionada por el candidato");

                String notaLimpia = nota.replace("📝 RAZÓN DEL TRABAJADOR (ACEPTADO):\n", "")
                        .replace("📝 RAZÓN DEL TRABAJADOR (RECHAZADO):\n", "");

                if (nota.contains("ACEPTADO")) {
                    alert.setHeaderText("✅ Razón de ACEPTACIÓN del candidato");
                } else if (nota.contains("RECHAZADO")) {
                    alert.setHeaderText("❌ Razón de RECHAZO del candidato");
                }

                alert.setContentText(notaLimpia);
                alert.getDialogPane().setMinHeight(400);
                alert.getDialogPane().setMinWidth(500);
                alert.showAndWait();
            }
        }
    }

    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
        if (soloLectura && volverButton != null) {
            volverButton.setText("Volver");
        }
    }

    public void setEsDesdeEmpresas(boolean esDesdeEmpresas) {
        this.esDesdeEmpresas = esDesdeEmpresas;

        if (esDesdeEmpresas && postulantesTab != null && tabPane != null) {
            if (!tabPane.getTabs().contains(postulantesTab)) {
                tabPane.getTabs().add(postulantesTab);
            }
        }

        if (esDesdeEmpresas && editarButton != null) {
            editarButton.setVisible(true);
            editarButton.setManaged(true);
        }
    }

    public void setTrabajadorActual(Trabajador trabajador) {
        this.trabajadorActual = trabajador;
    }

    public void mostrarOferta(Oferta oferta) {
        this.ofertaActual = oferta;

        if (nombreEmpresaLabel != null) {
            nombreEmpresaLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getNombreEmpresa() : "No especificado");
        }
        if (herramientaLabel != null) herramientaLabel.setText("No especificado");
        if (idiomasLabel != null) idiomasLabel.setText(oferta.getIdiomasRequeridos());
        if (domicilioLabel != null) domicilioLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getDomicilioCompleto() : "No especificado");
        if (gmailLabel != null) gmailLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getCorreoElectronico() : "No especificado");
        if (telefonoLabel != null) telefonoLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getNumTelefono() : "No especificado");
        if (puestoLabel != null) puestoLabel.setText(oferta.getPuesto_trabajo());
        if (horarioLabel != null) horarioLabel.setText(oferta.getJornada_laboral());

        if (sueldoLabel != null) {
            sueldoLabel.setText(oferta.getSalario() != null ? oferta.getSalario().getTipoSalario() : "No especificado");
        }

        if (montoSueldoLabel != null) {
            if (oferta.getSueldo() != null && oferta.getSueldo() > 0) {
                montoSueldoLabel.setText("$" + String.format("%,.2f", oferta.getSueldo()));
            } else {
                montoSueldoLabel.setText("No especificado");
            }
        }

        if (nivelEstudioLabel != null) nivelEstudioLabel.setText(oferta.getNivel_estudio());
        if (experienciaLabel != null) experienciaLabel.setText(oferta.getExperiencia());
        if (descripcionLabel != null) descripcionLabel.setText(oferta.getDescripcion_trabajo());

        if (esDesdeEmpresas && ofertaActual != null && postulantesTable != null) {
            cargarPostulantes();
        }
    }

    @FXML
    private void onEditarClick() {
        if (ofertaActual == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/EditarOferta.fxml"));
            Parent root = loader.load();

            EditarOfertaController controller = loader.getController();
            controller.setOferta(ofertaActual);

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Editar Oferta - " + ofertaActual.getPuesto_trabajo());

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el editor: " + e.getMessage());
        }
    }

    private void cargarPostulantes() {
        if (ofertaActual == null) return;

        List<Postulacion> postulaciones = postulacionService.obtenerPostulacionesPorOferta(ofertaActual);

        if (postulaciones == null || postulaciones.isEmpty()) {
            postulantesTable.setVisible(false);
        } else {
            postulantesTable.setVisible(true);
            postulantesTable.getItems().setAll(postulaciones);
        }
    }

    private void abrirDetalleTrabajador(Postulacion postulacion) {
        if (postulacion == null || postulacion.getTrabajador() == null) {
            mostrarAlerta("Información", "No hay información del trabajador disponible.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajador.fxml"));
            Parent root = loader.load();

            DetalleTrabajadorController controller = loader.getController();
            controller.setPostulacion(postulacion);

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el perfil del trabajador: " + e.getMessage());
        }
    }

    private void abrirNotas(Postulacion postulacion) {
        try {
            if (!"ACEPTADO".equalsIgnoreCase(postulacion.getEstado())) {
                mostrarAlerta("Restricción",
                        "Solo puedes agregar notas a postulaciones ACEPTADAS.\n" +
                                "Estado actual: " + postulacion.getEstado());
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Notas.fxml"));
            Parent root = loader.load();

            NotasController controller = loader.getController();
            controller.setPostulacion(postulacion);

            Stage stage = new Stage();
            stage.setTitle("Agregar Nota - " +
                    (postulacion.getTrabajador() != null ?
                            postulacion.getTrabajador().getNombreCompleto() : "Postulante"));
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de notas: " + e.getMessage());
        }
    }

    @FXML
    private void onVolverClick() {
        try {
            String fxml;
            String titulo;

            if (soloLectura) {
                fxml = "Trabajos.fxml";
                titulo = "Buscar Trabajos";
            } else {
                fxml = "Empresas.fxml";
                titulo = "Panel de Empresas";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/" + fxml));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al regresar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}