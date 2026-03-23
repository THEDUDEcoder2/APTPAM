package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import java.util.List;

public class PostulantesController {

    @FXML private TableView<Postulacion> postulantesTable;
    @FXML private TableColumn<Postulacion, String> nombreColumn;
    @FXML private TableColumn<Postulacion, String> telefonoColumn;
    @FXML private TableColumn<Postulacion, String> estadoColumn;
    @FXML private TableColumn<Postulacion, Void> accionesColumn;
    @FXML private Label tituloLabel;
    @FXML private Button volverButton;

    private Oferta oferta;
    private PostulacionService postulacionService = new PostulacionService();

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
        if (oferta != null) {
            cargarPostulantes();
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccionTabla();
    }

    private void configurarColumnas() {
        nombreColumn.setCellValueFactory(cellData -> {
            String nombre = "No disponible";
            if (cellData.getValue().getTrabajador() != null) {
                nombre = cellData.getValue().getTrabajador().getNombreCompleto();
            }
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        telefonoColumn.setCellValueFactory(cellData -> {
            String telefono = "No disponible";
            if (cellData.getValue().getTrabajador() != null) {
                telefono = cellData.getValue().getTrabajador().getNumTelefono();
            }
            return new javafx.beans.property.SimpleStringProperty(telefono);
        });

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

        accionesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button verPerfilButton = new Button("👤 Ver Perfil");
            private final Button notasButton = new Button("✏️ Agregar Nota");

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

                    setGraphic(hbox);
                }
            }
        });
    }

    private void configurarSeleccionTabla() {
        postulantesTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Postulacion> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Postulacion postulacion = row.getItem();
                    abrirDetalleTrabajador(postulacion);
                }
            });
            return row;
        });
    }

    public void refrescarTabla() {
        cargarPostulantes();
    }

    private void cargarPostulantes() {
        if (oferta == null) {
            System.out.println("No se ha seleccionado una oferta");
            return;
        }

        List<Postulacion> postulaciones = postulacionService.obtenerPostulacionesPorOferta(oferta);

        if (postulaciones == null || postulaciones.isEmpty()) {
            System.out.println("No hay postulaciones para esta oferta");
            postulantesTable.getItems().clear();
        } else {
            System.out.println("Cargando " + postulaciones.size() + " postulaciones");
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
            controller.setPostulantesController(this);
            Stage stage = (Stage) postulantesTable.getScene().getWindow();
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
    private void onCerrarClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleFormulario.fxml"));
            Parent root = loader.load();

            DetalleFormularioController controller = loader.getController();
            controller.setEsDesdeEmpresas(true);
            if (oferta != null) {
                controller.mostrarOferta(oferta);
            }

            Stage stage = (Stage) postulantesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/FormulariosTable.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) postulantesTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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