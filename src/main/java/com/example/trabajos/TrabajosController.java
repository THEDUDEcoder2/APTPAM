package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.TrabajadorService;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TrabajosController {

    @FXML private TabPane tabPane;
    @FXML private Tab ofertasPublicasTab;
    @FXML private Tab ofertasPrivadasTab;

    @FXML private TableView<Oferta> trabajosTable;
    @FXML private TableColumn<Oferta, String> empresaColumn;
    @FXML private TableColumn<Oferta, String> vacanteColumn;
    @FXML private TableColumn<Oferta, String> sueldoColumn;
    @FXML private TableColumn<Oferta, String> estadoColumn;
    @FXML private TableColumn<Oferta, Void> accionesColumn;

    @FXML private TableView<Oferta> ofertasPrivadasTable;
    @FXML private TableColumn<Oferta, String> empresaPrivColumn;
    @FXML private TableColumn<Oferta, String> puestoPrivColumn;
    @FXML private TableColumn<Oferta, String> fechaPrivColumn;
    @FXML private TableColumn<Oferta, String> estadoPrivColumn;
    @FXML private TableColumn<Oferta, Void> accionesPrivColumn;

    @FXML private ComboBox<String> tipoTrabajoComboBox;
    @FXML private Button limpiarFiltrosButton;
    @FXML private HBox filtrosContainer;

    @FXML private Label mensajeLabel;
    @FXML private Label mensajePrivadasLabel;
    @FXML private Label totalOfertasLabel;
    @FXML private Label totalPrivadasLabel;
    @FXML private Label usuarioInfoLabel;

    private Trabajador trabajadorActual;
    private List<Oferta> ofertasPublicas;
    private List<Oferta> ofertasPrivadas;

    private OfertaService ofertaService = new OfertaService();
    private TrabajadorService trabajadorService = new TrabajadorService();
    private PostulacionService postulacionService = new PostulacionService();

    @FXML
    public void initialize() {
        configurarColumnasPublicas();
        configurarColumnasPrivadas();
        configurarFiltros();
        cargarTrabajadorActual();
        refrescarTabla();
    }

    private void configurarColumnasPublicas() {
        empresaColumn.setCellValueFactory(cellData -> {
            Empresa empresa = cellData.getValue().getEmpresa();
            return new javafx.beans.property.SimpleStringProperty(
                    empresa != null ? empresa.getNombreEmpresa() : "No especificado");
        });

        vacanteColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));

        sueldoColumn.setCellValueFactory(cellData -> {
            String tipoSalario = cellData.getValue().getSalario() != null ?
                    cellData.getValue().getSalario().getTipoSalario() : "No especificado";
            return new javafx.beans.property.SimpleStringProperty(tipoSalario);
        });

        estadoColumn.setCellValueFactory(cellData -> {
            String estado = obtenerEstadoPostulacion(cellData.getValue());
            return new javafx.beans.property.SimpleStringProperty(estado);
        });

        estadoColumn.setCellFactory(column -> new TableCell<Oferta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    String estado = obtenerEstadoPostulacion(oferta);

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
                            setText("No postulado");
                            setStyle("-fx-text-fill: #7f8c8d;");
                            break;
                    }
                }
            }
        });

        accionesColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox hbox = new HBox(5);
            private final Button abrirButton = new Button("Abrir");
            private final Button verNotaButton = new Button("📝 Ver Nota");

            {
                abrirButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                abrirButton.setOnAction(event -> {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    abrirDetalleTrabajo(oferta);
                });

                verNotaButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                verNotaButton.setOnAction(event -> {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    mostrarNotaEmpresa(oferta);
                });

                hbox.getChildren().addAll(abrirButton, verNotaButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Oferta oferta = getTableView().getItems().get(getIndex());

                    if (oferta.esOfertaPrivada()) {
                        setGraphic(null);
                        return;
                    }

                    abrirButton.setVisible(true);
                    abrirButton.setManaged(true);
                    Postulacion postulacion = obtenerPostulacion(oferta);
                    if (postulacion != null && postulacion.tieneNotaEmpresa()) {
                        verNotaButton.setVisible(true);
                        verNotaButton.setManaged(true);
                    } else {
                        verNotaButton.setVisible(false);
                        verNotaButton.setManaged(false);
                    }
                    setGraphic(hbox);
                }
            }
        });
    }

    private void configurarColumnasPrivadas() {
        empresaPrivColumn.setCellValueFactory(cellData -> {
            Empresa empresa = cellData.getValue().getEmpresa();
            return new javafx.beans.property.SimpleStringProperty(
                    empresa != null ? empresa.getNombreEmpresa() : "No especificado");
        });

        puestoPrivColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));

        fechaPrivColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFecha_publicacion() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFecha_publicacion().toString());
            }
            return new javafx.beans.property.SimpleStringProperty("Fecha no especificada");
        });

        estadoPrivColumn.setCellValueFactory(cellData -> {
            String estado = obtenerEstadoPostulacion(cellData.getValue());
            return new javafx.beans.property.SimpleStringProperty(estado);
        });

        estadoPrivColumn.setCellFactory(column -> new TableCell<Oferta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    String estado = obtenerEstadoPostulacion(oferta);

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
                            setText("No postulado");
                            setStyle("-fx-text-fill: #7f8c8d;");
                            break;
                    }
                }
            }
        });

        accionesPrivColumn.setCellFactory(param -> new TableCell<>() {
            private final Button verButton = new Button("Abrir");
            private final Button responderButton = new Button("Responder");

            {
                verButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                verButton.setOnAction(event -> {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    abrirDetalleTrabajo(oferta);
                });

                responderButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                responderButton.setOnAction(event -> {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    abrirNotasParaResponder(oferta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Oferta oferta = getTableView().getItems().get(getIndex());

                    if (!oferta.esOfertaPrivada() ||
                            trabajadorActual == null ||
                            oferta.getTrabajadorDestino() == null ||
                            !oferta.getTrabajadorDestino().getIdTrabajador().equals(trabajadorActual.getIdTrabajador())) {

                        setGraphic(null);
                        return;
                    }

                    String estado = obtenerEstadoPostulacion(oferta);
                    HBox hbox = new HBox(5);
                    hbox.getChildren().add(verButton);

                    if ("PENDIENTE".equals(estado)) {
                        responderButton.setText("📨 Responder");
                        hbox.getChildren().add(responderButton);
                    } else if ("ACEPTADO".equals(estado)) {
                        Label aceptadoLabel = new Label("✅ Aceptado");
                        aceptadoLabel.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 3;");
                        hbox.getChildren().add(aceptadoLabel);
                    } else if ("RECHAZADO".equals(estado)) {
                        Label rechazadoLabel = new Label("❌ Rechazado");
                        rechazadoLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 3;");
                        hbox.getChildren().add(rechazadoLabel);
                    }

                    setGraphic(hbox);
                }
            }
        });
    }

    private void configurarFiltros() {
        tipoTrabajoComboBox.getItems().clear();
        tipoTrabajoComboBox.getItems().add("Todos los trabajos");
        tipoTrabajoComboBox.getItems().addAll(
                "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante"
        );
        tipoTrabajoComboBox.setValue("Todos los trabajos");

        tipoTrabajoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        limpiarFiltrosButton.setOnAction(event -> limpiarFiltros());
    }

    private void cargarTrabajadorActual() {
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();
        if (usuarioActual != null && !usuarioActual.isEsEmpresa()) {
            trabajadorActual = trabajadorService.obtenerTrabajadorPorEmail(usuarioActual.getEmail());
            if (usuarioInfoLabel != null && trabajadorActual != null) {
                String nombreCompleto = trabajadorActual.getNombre();
                if (trabajadorActual.getApellidoPaterno() != null && !trabajadorActual.getApellidoPaterno().isEmpty()) {
                    nombreCompleto += " " + trabajadorActual.getApellidoPaterno();
                }
                usuarioInfoLabel.setText("Sesión iniciada como: " + nombreCompleto);
            }
        }
    }

    public void refrescarTabla() {
        if (trabajadorActual == null) {
            cargarTrabajadorActual();
        }

        ofertasPublicas = ofertaService.obtenerOfertasPublicas();
        System.out.println("📋 Ofertas públicas: " + (ofertasPublicas != null ? ofertasPublicas.size() : 0));

        if (trabajadorActual != null) {
            System.out.println("🔍 Buscando ofertas privadas para trabajador ID: " + trabajadorActual.getIdTrabajador());
            ofertasPrivadas = ofertaService.obtenerOfertasPrivadasPorTrabajador(trabajadorActual);
            System.out.println("🔷 Resultado: " + (ofertasPrivadas != null ? ofertasPrivadas.size() : 0) + " ofertas privadas");
        } else {
            ofertasPrivadas = List.of();
        }

        List<Oferta> publicasFiltradas = ofertasPublicas.stream()
                .filter(o -> !o.esOfertaPrivada())
                .collect(Collectors.toList());

        if (publicasFiltradas != null && !publicasFiltradas.isEmpty()) {
            trabajosTable.setVisible(true);
            filtrosContainer.setVisible(true);
            mensajeLabel.setVisible(false);
            trabajosTable.getItems().setAll(publicasFiltradas);
        } else {
            trabajosTable.setVisible(false);
            filtrosContainer.setVisible(false);
            mensajeLabel.setVisible(true);
            mensajeLabel.setText("No hay trabajos públicos disponibles en este momento.");
        }

        actualizarVistaPrivadas();
        actualizarContadores();
    }

    private void actualizarVistaPrivadas() {
        if (ofertasPrivadas == null || ofertasPrivadas.isEmpty()) {
            if (mensajePrivadasLabel != null) {
                mensajePrivadasLabel.setText("No tienes ofertas privadas en este momento.");
                mensajePrivadasLabel.setVisible(true);
            }
            ofertasPrivadasTable.setVisible(false);
            System.out.println("🔷 No hay ofertas privadas para mostrar");
        } else {
            if (mensajePrivadasLabel != null) {
                mensajePrivadasLabel.setVisible(false);
            }
            ofertasPrivadasTable.setVisible(true);
            ofertasPrivadasTable.getItems().setAll(ofertasPrivadas);
            System.out.println("🔷 Mostrando " + ofertasPrivadas.size() + " ofertas privadas");
        }
    }

    private void actualizarContadores() {
        if (totalOfertasLabel != null) {
            totalOfertasLabel.setText("Ofertas públicas disponibles: " +
                    (ofertasPublicas != null ? ofertasPublicas.size() : 0));
        }
        if (totalPrivadasLabel != null) {
            totalPrivadasLabel.setText("Ofertas privadas para ti: " +
                    (ofertasPrivadas != null ? ofertasPrivadas.size() : 0));
        }
    }

    private void aplicarFiltros() {
        if (ofertasPublicas == null || ofertasPublicas.isEmpty()) return;

        String tipoSeleccionado = tipoTrabajoComboBox.getValue();

        List<Oferta> ofertasFiltradas;

        if (tipoSeleccionado == null || "Todos los trabajos".equals(tipoSeleccionado)) {
            ofertasFiltradas = ofertasPublicas.stream()
                    .filter(o -> !o.esOfertaPrivada())
                    .collect(Collectors.toList());
        } else {
            ofertasFiltradas = ofertasPublicas.stream()
                    .filter(o -> !o.esOfertaPrivada())
                    .filter(o -> tipoSeleccionado.equals(o.getPuesto_trabajo()))
                    .collect(Collectors.toList());
        }

        if (ofertasFiltradas.isEmpty()) {
            mensajeLabel.setText("No se encontraron trabajos que coincidan con los filtros seleccionados.");
            mensajeLabel.setVisible(true);
            trabajosTable.setVisible(false);
        } else {
            mensajeLabel.setVisible(false);
            trabajosTable.setVisible(true);
            trabajosTable.getItems().setAll(ofertasFiltradas);
        }
    }

    private void limpiarFiltros() {
        tipoTrabajoComboBox.setValue("Todos los trabajos");
        aplicarFiltros();
    }

    private String obtenerEstadoPostulacion(Oferta oferta) {
        if (trabajadorActual == null) return "NO_POSTULADO";
        Postulacion postulacion = obtenerPostulacion(oferta);
        if (postulacion != null) {
            return postulacion.getEstado();
        }
        return "NO_POSTULADO";
    }

    private Postulacion obtenerPostulacion(Oferta oferta) {
        if (trabajadorActual == null) return null;
        return postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, oferta);
    }

    private void mostrarNotaEmpresa(Oferta oferta) {
        if (trabajadorActual == null || oferta == null) {
            mostrarAlerta("Información", "No se pudo obtener la información necesaria.");
            return;
        }

        Postulacion postulacion = obtenerPostulacion(oferta);

        if (postulacion == null) {
            mostrarAlerta("Información", "No tienes una postulación para esta vacante.");
            return;
        }

        if (!postulacion.tieneNotaEmpresa()) {
            mostrarAlerta("Información", "La empresa no ha dejado ninguna nota para ti.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📝 Nota de la Empresa");
        alert.setHeaderText("Mensaje de " + (oferta.getEmpresa() != null ? oferta.getEmpresa().getNombreEmpresa() : "la Empresa"));
        alert.setContentText(postulacion.getNotaEmpresa());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(400);
        alert.showAndWait();
    }

    private void abrirDetalleTrabajo(Oferta ofertaSeleccionada) {
        if (ofertaSeleccionada == null) return;

        try {
            String fxmlPath;
            String titulo;

            if (ofertaSeleccionada.esOfertaPrivada()) {
                fxmlPath = "/com/example/trabajos/DetalleFormulario.fxml";
                titulo = "🔷 OFERTA PRIVADA - " + ofertaSeleccionada.getPuesto_trabajo();
            } else {
                fxmlPath = "/com/example/trabajos/DetalleTrabajo.fxml";
                titulo = "Detalle de Vacante - " + ofertaSeleccionada.getPuesto_trabajo();
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            if (ofertaSeleccionada.esOfertaPrivada()) {
                DetalleFormularioController controller = fxmlLoader.getController();
                controller.setSoloLectura(true);
                controller.setTrabajadorActual(trabajadorActual);
                controller.mostrarOferta(ofertaSeleccionada);
            } else {
                DetalleTrabajoController controller = fxmlLoader.getController();
                controller.setOferta(ofertaSeleccionada);
                controller.setTrabajadorActual(trabajadorActual);
                Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
                if (usuario != null) {
                    controller.setEmailUsuario(usuario.getEmail());
                }
            }

            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el detalle de la oferta: " + e.getMessage());
        }
    }

    private void abrirNotasParaResponder(Oferta oferta) {
        try {
            if (trabajadorActual == null) {
                mostrarAlerta("Error", "No se pudo identificar tu cuenta.");
                return;
            }

            Postulacion postulacion = postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, oferta);

            if (postulacion == null) {
                mostrarAlerta("Error", "No se encontró la postulación para esta oferta.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Notas.fxml"));
            Parent root = loader.load();

            NotasController controller = loader.getController();
            controller.setPostulacion(postulacion);
            controller.setModoRespuesta(true);

            Stage stage = new Stage();
            stage.setTitle("Responder a Oferta - " + oferta.getPuesto_trabajo());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de respuesta: " + e.getMessage());
        }
    }

    @FXML
    protected void onVolverClick() {
        System.out.println("=== CERRANDO SESIÓN DESDE TRABAJOS ===");

        try {
            // Obtener el tipo de usuario antes de cerrar sesión (era trabajador)
            boolean eraTrabajador = SesionManager.getInstancia().esTrabajador();

            // Cerrar sesión
            SesionManager.getInstancia().cerrarSesion();

            // Cargar la pantalla de inicio de sesión
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = fxmlLoader.load();

            SesionController controller = fxmlLoader.getController();
            controller.setTipoUsuario(!eraTrabajador);

            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión");

            System.out.println("✅ Sesión cerrada correctamente desde trabajos");

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cerrar la sesión");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
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