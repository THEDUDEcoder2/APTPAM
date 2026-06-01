package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.models.Ciudad;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TrabajosController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab ofertasPublicasTab;
    @FXML
    private Tab ofertasPrivadasTab;

    @FXML
    private TableView<Oferta> trabajosTable;
    @FXML
    private TableColumn<Oferta, String> empresaColumn;
    @FXML
    private TableColumn<Oferta, String> vacanteColumn;
    @FXML
    private TableColumn<Oferta, String> sueldoColumn;
    @FXML
    private TableColumn<Oferta, String> fechaColumn;
    @FXML
    private TableColumn<Oferta, String> estadoColumn;
    @FXML
    private TableColumn<Oferta, Void> accionesColumn;

    @FXML
    private TableView<Oferta> ofertasPrivadasTable;
    @FXML
    private TableColumn<Oferta, String> empresaPrivColumn;
    @FXML
    private TableColumn<Oferta, String> puestoPrivColumn;
    @FXML
    private TableColumn<Oferta, String> fechaPrivColumn;
    @FXML
    private TableColumn<Oferta, String> estadoPrivColumn;
    @FXML
    private TableColumn<Oferta, Void> accionesPrivColumn;

    // Filtros
    @FXML
    private ComboBox<String> tipoTrabajoComboBox;
    @FXML
    private ComboBox<String> tipoSueldoComboBox;
    @FXML
    private ComboBox<String> municipioComboBox;
    @FXML
    private ComboBox<String> ciudadComboBox;
    @FXML
    private Button aplicarFiltrosButton;
    @FXML
    private Button limpiarFiltrosButton;

    @FXML
    private Label mensajeLabel;
    @FXML
    private Label mensajePrivadasLabel;
    @FXML
    private Label totalOfertasLabel;
    @FXML
    private Label totalPrivadasLabel;
    @FXML
    private Label usuarioInfoLabel;
    @FXML
    private Button editarPerfilButton;
    @FXML
    private Button cerrarSesionButton;

    // NUEVOS: Botón de estado y label informativo
    @FXML
    private Button estadoButton;
    @FXML
    private Label estadoInfoLabel;

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
        configurarBotonEstado();
    }

    // NUEVO: Configurar el botón de estado según el trabajador actual
    private void configurarBotonEstado() {
        if (trabajadorActual != null) {
            actualizarInterfazEstado(trabajadorActual.isActivo());
        } else {
            cargarTrabajadorActual();
            if (trabajadorActual != null) {
                actualizarInterfazEstado(trabajadorActual.isActivo());
            }
        }
    }

    // NUEVO: Actualizar la interfaz del botón según estado activo/inactivo
    private void actualizarInterfazEstado(boolean activo) {
        if (estadoButton == null) return;

        if (activo) {
            estadoButton.setText("✅ ACTIVO - Recibiendo ofertas");
            estadoButton.setStyle("-fx-background-color: linear-gradient(to bottom, #27ae60, #1e8449); -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.55), 12, 0, 0, 4);");
            estadoInfoLabel.setText("📢 ESTÁS ACTIVO - Las empresas pueden verte y enviarte ofertas");
            estadoInfoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13; -fx-wrap-text: true; -fx-text-alignment: center; -fx-font-weight: bold;");
        } else {
            estadoButton.setText("⛔ INACTIVO - No recibiendo ofertas");
            estadoButton.setStyle("-fx-background-color: linear-gradient(to bottom, #95a5a6, #7f8c8d); -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(127,140,141,0.55), 12, 0, 0, 4);");
            estadoInfoLabel.setText("🔇 ESTÁS INACTIVO - Las empresas NO pueden verte. Presiona el botón para reactivarte.");
            estadoInfoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13; -fx-wrap-text: true; -fx-text-alignment: center;");
        }
    }

    // NUEVO: Toggle del estado (Activo <-> Inactivo)
    @FXML
    private void onToggleEstadoClick() {
        if (trabajadorActual == null) {
            cargarTrabajadorActual();
            if (trabajadorActual == null) {
                mostrarAlerta("Error", "No se pudo identificar tu cuenta.");
                return;
            }
        }

        boolean activoActual = trabajadorActual.isActivo();

        if (activoActual) {
            // Intentar desactivar
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Desactivar cuenta");
            confirmacion.setHeaderText("¿Estás seguro de desactivar tu cuenta?");
            confirmacion.setContentText("⚠️ IMPORTANTE:\n\n" +
                    "• Al desactivar tu cuenta, las empresas NO podrán ver tu perfil\n" +
                    "• NO recibirás nuevas ofertas de empleo\n" +
                    "• Puedes reactivarla en cualquier momento desde este mismo botón\n\n" +
                    "¿Deseas continuar?");

            ButtonType btnSi = new ButtonType("Sí, desactivar", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("No, cancelar", ButtonBar.ButtonData.NO);
            confirmacion.getButtonTypes().setAll(btnSi, btnNo);

            if (confirmacion.showAndWait().orElse(btnNo) == btnSi) {
                trabajadorService.cambiarEstadoActivo(trabajadorActual.getIdTrabajador(), false);
                trabajadorActual.setActivo(false);
                actualizarInterfazEstado(false);
                mostrarAlertaInfo("Cuenta desactivada", "✅ Tu cuenta ha sido desactivada.\n\nLas empresas ya no podrán verte ni enviarte ofertas.");
            }
        } else {
            // Intentar activar
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Activar cuenta");
            confirmacion.setHeaderText("¿Quieres reactivar tu cuenta?");
            confirmacion.setContentText("Al activar tu cuenta:\n\n" +
                    "• Las empresas podrán ver tu perfil nuevamente\n" +
                    "• Recibirás ofertas de empleo que coincidan con tu perfil\n" +
                    "• Podrás postularte a vacantes públicas\n\n" +
                    "¿Deseas reactivar tu cuenta?");

            ButtonType btnSi = new ButtonType("Sí, reactivar", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("No, cancelar", ButtonBar.ButtonData.NO);
            confirmacion.getButtonTypes().setAll(btnSi, btnNo);

            if (confirmacion.showAndWait().orElse(btnNo) == btnSi) {
                trabajadorService.cambiarEstadoActivo(trabajadorActual.getIdTrabajador(), true);
                trabajadorActual.setActivo(true);
                actualizarInterfazEstado(true);
                mostrarAlertaInfo("Cuenta activada", "✅ Tu cuenta ha sido reactivada.\n\nLas empresas ahora pueden ver tu perfil y enviarte ofertas.");
                refrescarTabla();
            }
        }
    }

    private void configurarColumnasPublicas() {
        if (empresaColumn != null) {
            empresaColumn.setCellValueFactory(cellData -> {
                Empresa empresa = cellData.getValue().getEmpresa();
                return new javafx.beans.property.SimpleStringProperty(
                        empresa != null ? empresa.getNombreEmpresa() : "No especificado");
            });
        }

        if (vacanteColumn != null) {
            vacanteColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));
        }

        if (sueldoColumn != null) {
            sueldoColumn.setCellValueFactory(cellData -> {
                String tipoSalario = cellData.getValue().getSalario() != null ?
                        cellData.getValue().getSalario().getTipoSalario() : "No especificado";
                return new javafx.beans.property.SimpleStringProperty(tipoSalario);
            });
        }

        if (fechaColumn != null) {
            fechaColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getFecha_publicacion() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getFecha_publicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                }
                return new javafx.beans.property.SimpleStringProperty("No especificada");
            });
        }

        if (estadoColumn != null) {
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
        }

        if (accionesColumn != null) {
            accionesColumn.setCellFactory(param -> new TableCell<>() {
                private final HBox hbox = new HBox(5);
                private final Button abrirButton = new Button("Abrir");
                private final Button verEmpresaButton = new Button("🏢 Ver Empresa");
                private final Button verNotaButton = new Button("📝 Ver Nota");

                {
                    abrirButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    abrirButton.setOnAction(event -> {
                        Oferta oferta = getTableView().getItems().get(getIndex());
                        abrirDetalleTrabajo(oferta);
                    });

                    verEmpresaButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verEmpresaButton.setOnAction(event -> {
                        Oferta oferta = getTableView().getItems().get(getIndex());
                        abrirPerfilEmpresa(oferta.getEmpresa());
                    });

                    verNotaButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    verNotaButton.setOnAction(event -> {
                        Oferta oferta = getTableView().getItems().get(getIndex());
                        mostrarNotaEmpresa(oferta);
                    });

                    hbox.getChildren().addAll(abrirButton, verEmpresaButton, verNotaButton);
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
                        verEmpresaButton.setVisible(oferta.getEmpresa() != null);
                        verEmpresaButton.setManaged(oferta.getEmpresa() != null);
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
    }

    private void configurarColumnasPrivadas() {
        if (empresaPrivColumn != null) {
            empresaPrivColumn.setCellValueFactory(cellData -> {
                Empresa empresa = cellData.getValue().getEmpresa();
                return new javafx.beans.property.SimpleStringProperty(
                        empresa != null ? empresa.getNombreEmpresa() : "No especificado");
            });
        }

        if (puestoPrivColumn != null) {
            puestoPrivColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));
        }

        if (fechaPrivColumn != null) {
            fechaPrivColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getFecha_publicacion() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getFecha_publicacion().toString());
                }
                return new javafx.beans.property.SimpleStringProperty("Fecha no especificada");
            });
        }

        if (estadoPrivColumn != null) {
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
        }

        if (accionesPrivColumn != null) {
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
    }

    private void configurarFiltros() {
        if (tipoTrabajoComboBox != null) {
            tipoTrabajoComboBox.getItems().clear();
            tipoTrabajoComboBox.getItems().add("Todos los trabajos");
            tipoTrabajoComboBox.getItems().addAll(
                    "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                    "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                    "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                    "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante"
            );
            tipoTrabajoComboBox.setValue("Todos los trabajos");
        }

        if (tipoSueldoComboBox != null) {
            tipoSueldoComboBox.getItems().clear();
            tipoSueldoComboBox.getItems().add("Todos");
            tipoSueldoComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
            tipoSueldoComboBox.setValue("Todos");
        }

        if (municipioComboBox != null) {
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
        }

        if (ciudadComboBox != null) {
            ciudadComboBox.getItems().clear();
            ciudadComboBox.getItems().add("Todas");
            ciudadComboBox.setValue("Todas");
        }

        if (municipioComboBox != null) {
            municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
                cargarCiudadesPorMunicipio(newVal);
            });
        }

        if (aplicarFiltrosButton != null) {
            aplicarFiltrosButton.setOnAction(e -> aplicarFiltros());
        }
        if (limpiarFiltrosButton != null) {
            limpiarFiltrosButton.setOnAction(e -> limpiarFiltros());
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
            ofertasPrivadas = ofertaService.obtenerOfertasPrivadasPorTrabajador(trabajadorActual);
        } else {
            ofertasPrivadas = List.of();
        }

        actualizarVistaPublicas();
        actualizarVistaPrivadas();
        actualizarContadores();

        // Actualizar estado del botón
        if (trabajadorActual != null) {
            actualizarInterfazEstado(trabajadorActual.isActivo());
        }
    }

    private void actualizarVistaPublicas() {
        List<Oferta> publicas = ofertasPublicas.stream()
                .filter(o -> !o.esOfertaPrivada())
                .collect(Collectors.toList());

        if (trabajosTable != null) {
            if (publicas != null && !publicas.isEmpty()) {
                trabajosTable.setVisible(true);
                trabajosTable.getItems().setAll(publicas);
                if (mensajeLabel != null) mensajeLabel.setVisible(false);
            } else {
                trabajosTable.setVisible(false);
                if (mensajeLabel != null) {
                    mensajeLabel.setVisible(true);
                    mensajeLabel.setText("No hay trabajos públicos disponibles en este momento.");
                }
            }
        }
        aplicarFiltros();
    }

    private void actualizarVistaPrivadas() {
        if (ofertasPrivadasTable == null) return;

        if (ofertasPrivadas == null || ofertasPrivadas.isEmpty()) {
            if (mensajePrivadasLabel != null) {
                mensajePrivadasLabel.setText("No tienes ofertas privadas en este momento.");
                mensajePrivadasLabel.setVisible(true);
            }
            ofertasPrivadasTable.setVisible(false);
        } else {
            if (mensajePrivadasLabel != null) mensajePrivadasLabel.setVisible(false);
            ofertasPrivadasTable.setVisible(true);
            ofertasPrivadasTable.getItems().setAll(ofertasPrivadas);
        }
    }

    private void actualizarContadores() {
        if (totalOfertasLabel != null) {
            totalOfertasLabel.setText("Ofertas públicas disponibles: " +
                    (ofertasPublicas != null ? ofertasPublicas.size() : 0));
        }
        if (totalPrivadasLabel != null) {
            totalPrivadasLabel.setText("Ofertas exclusivas para ti: " +
                    (ofertasPrivadas != null ? ofertasPrivadas.size() : 0));
        }
    }

    private void aplicarFiltros() {
        if (ofertasPublicas == null || ofertasPublicas.isEmpty() || trabajosTable == null) return;

        String tipoSeleccionado = tipoTrabajoComboBox != null ? tipoTrabajoComboBox.getValue() : "Todos los trabajos";
        String sueldoSeleccionado = tipoSueldoComboBox != null ? tipoSueldoComboBox.getValue() : "Todos";
        String municipioSeleccionado = municipioComboBox != null ? municipioComboBox.getValue() : "Todos";
        String ciudadSeleccionada = ciudadComboBox != null ? ciudadComboBox.getValue() : "Todas";

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
            if (mensajeLabel != null) {
                mensajeLabel.setText("No se encontraron trabajos que coincidan con los filtros seleccionados.");
                mensajeLabel.setVisible(true);
            }
            trabajosTable.setVisible(false);
        } else {
            if (mensajeLabel != null) mensajeLabel.setVisible(false);
            trabajosTable.setVisible(true);
            trabajosTable.getItems().setAll(ofertasFiltradas);
        }
    }

    private void limpiarFiltros() {
        if (tipoTrabajoComboBox != null) tipoTrabajoComboBox.setValue("Todos los trabajos");
        if (tipoSueldoComboBox != null) tipoSueldoComboBox.setValue("Todos");
        if (municipioComboBox != null) municipioComboBox.setValue("Todos");
        if (ciudadComboBox != null) ciudadComboBox.setValue("Todas");
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

    private void abrirPerfilEmpresa(Empresa empresa) {
        if (empresa == null) return;
        try {
            java.net.URL fxmlUrl = getClass().getResource("/com/example/trabajos/PerfilPublicoEmpresa.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PerfilPublicoEmpresaController controller = loader.getController();
            controller.setEmpresa(empresa);
            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Perfil de " + (empresa.getNombreEmpresa() != null ? empresa.getNombreEmpresa() : "Empresa"));
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el perfil de la empresa: " + e.getMessage());
        }
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
    protected void onPerfilClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/PerfilTrabajador.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Mi Perfil");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el perfil: " + e.getMessage());
        }
    }

    @FXML
    protected void onDashboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el Dashboard: " + e.getMessage());
        }
    }

    @FXML
    protected void onCalificacionClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/ListaCalificacion.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) trabajosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Calificaciones");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la Calificación: " + e.getMessage());
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
        System.out.println("=== CERRANDO SESIÓN DESDE TRABAJOS ===");

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

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}