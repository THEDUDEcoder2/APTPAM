package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Ciudad;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.TrabajadorService;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.MunicipioService;
import com.example.trabajos.services.CiudadService;
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
    @FXML private TableColumn<Oferta, String> ubicacionColumn;
    @FXML private TableColumn<Oferta, String> estadoColumn;
    @FXML private TableColumn<Oferta, Void> accionesColumn;

    @FXML private TableView<Oferta> ofertasPrivadasTable;
    @FXML private TableColumn<Oferta, String> empresaPrivColumn;
    @FXML private TableColumn<Oferta, String> puestoPrivColumn;
    @FXML private TableColumn<Oferta, String> fechaPrivColumn;
    @FXML private TableColumn<Oferta, String> ubicacionPrivColumn;
    @FXML private TableColumn<Oferta, String> estadoPrivColumn;
    @FXML private TableColumn<Oferta, Void> accionesPrivColumn;

    // Filtros
    @FXML private ComboBox<String> tipoTrabajoComboBox;
    @FXML private ComboBox<String> tipoSueldoComboBox;
    @FXML private ComboBox<String> municipioComboBox;
    @FXML private ComboBox<String> ciudadComboBox;
    @FXML private Button aplicarFiltrosButton;
    @FXML private Button limpiarFiltrosButton;

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
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();

    @FXML
    public void initialize() {
        configurarColumnasPublicas();
        configurarColumnasPrivadas();
        configurarFiltros();
        cargarTrabajadorActual();
        refrescarTabla();
    }

    private void configurarFiltros() {
        // Tipo de trabajo
        tipoTrabajoComboBox.getItems().clear();
        tipoTrabajoComboBox.getItems().add("Todos los trabajos");
        tipoTrabajoComboBox.getItems().addAll(
                "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante"
        );
        tipoTrabajoComboBox.setValue("Todos los trabajos");

        // Tipo de sueldo
        tipoSueldoComboBox.getItems().clear();
        tipoSueldoComboBox.getItems().add("Todos");
        tipoSueldoComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
        tipoSueldoComboBox.setValue("Todos");

        // Municipio
        municipioComboBox.getItems().clear();
        municipioComboBox.getItems().add("Todos");
        try {
            for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                municipioComboBox.getItems().add(m.getNombreMunicipio());
            }
        } catch (Exception e) {
            municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
        }
        municipioComboBox.setValue("Todos");

        // Ciudad - se carga dinámicamente al seleccionar municipio
        ciudadComboBox.getItems().clear();
        ciudadComboBox.getItems().add("Todas");
        ciudadComboBox.setValue("Todas");

        // Listener para cargar ciudades según municipio
        municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
            cargarCiudadesPorMunicipio(newVal);
        });

        // Botones
        aplicarFiltrosButton.setOnAction(e -> aplicarFiltros());
        limpiarFiltrosButton.setOnAction(e -> limpiarFiltros());
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
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
            // Ciudades por defecto según municipio
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

        ubicacionColumn.setCellValueFactory(cellData -> {
            Empresa empresa = cellData.getValue().getEmpresa();
            if (empresa != null) {
                String ubicacion = "";
                if (empresa.getMunicipio() != null) {
                    ubicacion = empresa.getMunicipio().getNombreMunicipio();
                }
                if (empresa.getCiudad() != null) {
                    if (!ubicacion.isEmpty()) ubicacion += " - ";
                    ubicacion += empresa.getCiudad().getNombreCiudad();
                }
                return new javafx.beans.property.SimpleStringProperty(ubicacion.isEmpty() ? "No especificada" : ubicacion);
            }
            return new javafx.beans.property.SimpleStringProperty("No especificada");
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

        ubicacionPrivColumn.setCellValueFactory(cellData -> {
            Empresa empresa = cellData.getValue().getEmpresa();
            if (empresa != null) {
                String ubicacion = "";
                if (empresa.getMunicipio() != null) {
                    ubicacion = empresa.getMunicipio().getNombreMunicipio();
                }
                if (empresa.getCiudad() != null) {
                    if (!ubicacion.isEmpty()) ubicacion += " - ";
                    ubicacion += empresa.getCiudad().getNombreCiudad();
                }
                return new javafx.beans.property.SimpleStringProperty(ubicacion.isEmpty() ? "No especificada" : ubicacion);
            }
            return new javafx.beans.property.SimpleStringProperty("No especificada");
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

    private void cargarTrabajadorActual() {
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();
        if (usuarioActual != null && !usuarioActual.isEsEmpresa()) {
            trabajadorActual = trabajadorService.obtenerTrabajadorPorEmail(usuarioActual.getEmail());
        }
    }

    public void refrescarTabla() {
        if (trabajadorActual == null) {
            cargarTrabajadorActual();
        }

        ofertasPublicas = ofertaService.obtenerOfertasPublicas();

        if (trabajadorActual != null) {
            ofertasPrivadas = ofertaService.obtenerOfertasPrivadasPorTrabajador(trabajadorActual);
        } else {
            ofertasPrivadas = List.of();
        }

        actualizarVistaPublicas();
        actualizarVistaPrivadas();
        actualizarContadores();
    }

    private void actualizarVistaPublicas() {
        List<Oferta> publicas = ofertasPublicas.stream()
                .filter(o -> !o.esOfertaPrivada())
                .collect(Collectors.toList());

        if (publicas != null && !publicas.isEmpty()) {
            trabajosTable.setVisible(true);
            trabajosTable.getItems().setAll(publicas);
            mensajeLabel.setVisible(false);
        } else {
            trabajosTable.setVisible(false);
            mensajeLabel.setVisible(true);
            mensajeLabel.setText("No hay trabajos públicos disponibles en este momento.");
        }
        aplicarFiltros();
    }

    private void actualizarVistaPrivadas() {
        if (ofertasPrivadas == null || ofertasPrivadas.isEmpty()) {
            mensajePrivadasLabel.setText("No tienes ofertas privadas en este momento.");
            mensajePrivadasLabel.setVisible(true);
            ofertasPrivadasTable.setVisible(false);
        } else {
            mensajePrivadasLabel.setVisible(false);
            ofertasPrivadasTable.setVisible(true);
            ofertasPrivadasTable.getItems().setAll(ofertasPrivadas);
        }
    }

    private void actualizarContadores() {
        totalOfertasLabel.setText("Ofertas públicas disponibles: " +
                (ofertasPublicas != null ? ofertasPublicas.size() : 0));
        totalPrivadasLabel.setText("Ofertas exclusivas para ti: " +
                (ofertasPrivadas != null ? ofertasPrivadas.size() : 0));
    }

    private void aplicarFiltros() {
        if (ofertasPublicas == null || ofertasPublicas.isEmpty()) return;

        String tipoSeleccionado = tipoTrabajoComboBox.getValue();
        String sueldoSeleccionado = tipoSueldoComboBox.getValue();
        String municipioSeleccionado = municipioComboBox.getValue();
        String ciudadSeleccionada = ciudadComboBox.getValue();

        List<Oferta> ofertasFiltradas = ofertasPublicas.stream()
                .filter(o -> !o.esOfertaPrivada())
                .filter(o -> {
                    if (tipoSeleccionado == null || "Todos los trabajos".equals(tipoSeleccionado)) return true;
                    return tipoSeleccionado.equals(o.getPuesto_trabajo());
                })
                .filter(o -> {
                    if (sueldoSeleccionado == null || "Todos".equals(sueldoSeleccionado)) return true;
                    String tipoSalario = o.getSalario() != null ? o.getSalario().getTipoSalario() : "";
                    return sueldoSeleccionado.equals(tipoSalario);
                })
                .filter(o -> {
                    if (municipioSeleccionado == null || "Todos".equals(municipioSeleccionado)) return true;
                    Empresa e = o.getEmpresa();
                    if (e == null || e.getMunicipio() == null) return false;
                    return municipioSeleccionado.equals(e.getMunicipio().getNombreMunicipio());
                })
                .filter(o -> {
                    if (ciudadSeleccionada == null || "Todas".equals(ciudadSeleccionada)) return true;
                    Empresa e = o.getEmpresa();
                    if (e == null || e.getCiudad() == null) return false;
                    return ciudadSeleccionada.equals(e.getCiudad().getNombreCiudad());
                })
                .collect(Collectors.toList());

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
        tipoSueldoComboBox.setValue("Todos");
        municipioComboBox.setValue("Todos");
        ciudadComboBox.setValue("Todas");
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
            if (ofertaSeleccionada.esOfertaPrivada()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajoPrivado.fxml"));
                Parent root = loader.load();

                DetalleTrabajoPrivadoController controller = loader.getController();
                controller.setOferta(ofertaSeleccionada);
                controller.setTrabajadorActual(trabajadorActual);

                Stage stage = (Stage) trabajosTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("🔷 OFERTA EXCLUSIVA - " + ofertaSeleccionada.getPuesto_trabajo());

            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleTrabajo.fxml"));
                Parent root = loader.load();

                DetalleTrabajoController controller = loader.getController();
                controller.setOferta(ofertaSeleccionada);
                controller.setTrabajadorActual(trabajadorActual);
                Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
                if (usuario != null) {
                    controller.setEmailUsuario(usuario.getEmail());
                }

                Stage stage = (Stage) trabajosTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Detalle de Vacante - " + ofertaSeleccionada.getPuesto_trabajo());
            }

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
    protected void onEditarPerfilClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/EditarPerfilTrabajador.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Editar Perfil");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el editor de perfil: " + e.getMessage());
        }
    }

    @FXML
    protected void onVolverClick() {
        try {
            boolean eraTrabajador = SesionManager.getInstancia().esTrabajador();
            SesionManager.getInstancia().cerrarSesion();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = fxmlLoader.load();

            SesionController controller = fxmlLoader.getController();
            controller.setTipoUsuario(!eraTrabajador);

            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión");

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