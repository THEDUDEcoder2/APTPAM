package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormulariosTableController {

    private TableView<Oferta> formulariosTable;
    private TableColumn<Oferta, String> tituloColumn;
    private TableColumn<Oferta, String> fechaColumn;
    private TableColumn<Oferta, Void> accionesColumn;
    private Label mensajeVacioLabel;

    @FXML private TableColumn<Oferta, String> tipoOfertaColumn;
    @FXML private TableColumn<Oferta, String> estadoPostulacionColumn;

    private final OfertaService ofertaService = new OfertaService();
    private final EmpresaService empresaService = new EmpresaService();
    private final PostulacionService postulacionService = new PostulacionService();

    // Setters
    public void setFormulariosTable(TableView<Oferta> table) { this.formulariosTable = table; }
    public void setTituloColumn(TableColumn<Oferta, String> col) { this.tituloColumn = col; }
    public void setFechaColumn(TableColumn<Oferta, String> col) { this.fechaColumn = col; }
    public void setAccionesColumn(TableColumn<Oferta, Void> col) { this.accionesColumn = col; }
    public void setMensajeVacioLabel(Label label) { this.mensajeVacioLabel = label; }
    public void setTipoOfertaColumn(TableColumn<Oferta, String> col) { this.tipoOfertaColumn = col; }
    public void setEstadoPostulacionColumn(TableColumn<Oferta, String> col) { this.estadoPostulacionColumn = col; }

    public void initialize() {
        if (tituloColumn != null) {
            tituloColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));
        }

        if (fechaColumn != null) {
            fechaColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getFecha_publicacion()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
            );
        }

        // Columna TIPO
        if (tipoOfertaColumn != null) {
            tipoOfertaColumn.setCellValueFactory(cellData -> {
                Oferta oferta = cellData.getValue();
                if (oferta.esOfertaPrivada()) {
                    return new javafx.beans.property.SimpleStringProperty("🔷 PRIVADA");
                } else {
                    return new javafx.beans.property.SimpleStringProperty("🌐 PÚBLICA");
                }
            });

            tipoOfertaColumn.setCellFactory(column -> new TableCell<Oferta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.contains("PRIVADA")) {
                            setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }

        // Columna ESTADO
        if (estadoPostulacionColumn != null) {
            estadoPostulacionColumn.setCellValueFactory(cellData -> {
                Oferta oferta = cellData.getValue();

                if (oferta.esOfertaPublica()) {
                    return new javafx.beans.property.SimpleStringProperty("📋 PÚBLICA");
                }

                if (oferta.esOfertaPrivada() && oferta.getTrabajadorDestino() != null) {
                    Postulacion postulacion = postulacionService.obtenerPostulacionPorTrabajadorYOferta(
                            oferta.getTrabajadorDestino(), oferta);
                    if (postulacion != null) {
                        String estado = postulacion.getEstado();
                        switch (estado.toUpperCase()) {
                            case "ACEPTADO":
                                return new javafx.beans.property.SimpleStringProperty("✅ ACEPTADO");
                            case "RECHAZADO":
                                return new javafx.beans.property.SimpleStringProperty("❌ RECHAZADO");
                            default:
                                return new javafx.beans.property.SimpleStringProperty("⏳ EN ESPERA");
                        }
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("⏳ SIN RESPUESTA");
                    }
                }

                return new javafx.beans.property.SimpleStringProperty("📋 PÚBLICA");
            });

            estadoPostulacionColumn.setCellFactory(column -> new TableCell<Oferta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.contains("ACEPTADO")) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if (item.contains("RECHAZADO")) {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        } else if (item.contains("EN ESPERA") || item.contains("SIN RESPUESTA")) {
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        } else if (item.contains("PÚBLICA")) {
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }

        if (accionesColumn != null) {
            agregarBotonesAcciones();
        }

        cargarOfertas();
    }

    public void refrescarTabla() {
        cargarOfertas();
    }

    private void agregarBotonesAcciones() {
        accionesColumn.setCellFactory(col -> new TableCell<>() {
            private final HBox hbox = new HBox(5);
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnEliminar = new Button("🗑️ Eliminar");
            private final Button btnAbrir = new Button("Abrir");

            {
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                btnEditar.setOnAction(e -> {
                    Oferta oferta = getTableRow().getItem();
                    if (oferta != null) {
                        abrirEditarOferta(oferta);
                    }
                });

                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                btnEliminar.setOnAction(e -> {
                    Oferta oferta = getTableRow().getItem();
                    if (oferta != null && oferta.esOfertaPublica()) {
                        confirmarEliminarOferta(oferta);
                    } else {
                        mostrarAlerta("Información", "Solo se pueden eliminar ofertas públicas");
                    }
                });

                btnAbrir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                btnAbrir.setOnAction(e -> {
                    Oferta oferta = getTableRow().getItem();
                    if (oferta != null) {
                        abrirDetalleEmpresa(oferta);
                    }
                });

                hbox.getChildren().addAll(btnEditar, btnEliminar, btnAbrir);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Oferta oferta = getTableRow().getItem();
                    if (oferta != null && oferta.esOfertaPrivada()) {
                        btnEliminar.setVisible(false);
                        btnEliminar.setManaged(false);
                    } else {
                        btnEliminar.setVisible(true);
                        btnEliminar.setManaged(true);
                    }
                    setGraphic(hbox);
                }
            }
        });
    }

    private void confirmarEliminarOferta(Oferta oferta) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de eliminar esta oferta?");
        alert.setContentText("Oferta: " + oferta.getPuesto_trabajo() + "\n\n⚠️ Esta acción no se puede deshacer. La oferta dejará de estar visible para los trabajadores.");

        ButtonType btnSi = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No, cancelar", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(btnSi, btnNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                eliminarOferta(oferta);
            }
        });
    }

    private void eliminarOferta(Oferta oferta) {
        try {
            ofertaService.eliminarOferta(oferta.getIdOferta());
            mostrarAlertaInfo("✅ Oferta eliminada correctamente", "La oferta ya no estará visible para los trabajadores.");
            refrescarTabla();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar la oferta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirEditarOferta(Oferta oferta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/EditarOferta.fxml"));
            Parent root = loader.load();

            EditarOfertaController controller = loader.getController();
            controller.setOferta(oferta);

            Stage stage = (Stage) formulariosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Editar Oferta - " + oferta.getPuesto_trabajo());

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el editor: " + e.getMessage());
        }
    }

    private void abrirDetalleEmpresa(Oferta oferta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleFormulario.fxml"));
            Parent root = loader.load();

            DetalleFormularioController controller = loader.getController();
            controller.setEsDesdeEmpresas(true);
            controller.mostrarOferta(oferta);

            Stage stage = (Stage) formulariosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Detalle de Oferta");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el detalle: " + e.getMessage());
        }
    }

    private void cargarOfertas() {
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();

        if (usuarioActual == null || !usuarioActual.isEsEmpresa()) {
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No hay sesión de empresa activa.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        Empresa empresa = empresaService.obtenerEmpresaPorEmail(usuarioActual.getEmail());

        if (empresa == null) {
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No se encontraron datos de la empresa.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        List<Oferta> ofertas = ofertaService.obtenerOfertasPorEmpresa(empresa);

        if (ofertas == null || ofertas.isEmpty()) {
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No has creado ninguna oferta aún.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        if (formulariosTable != null) {
            formulariosTable.getItems().setAll(ofertas);
            formulariosTable.setVisible(true);
        }
        if (mensajeVacioLabel != null) mensajeVacioLabel.setVisible(false);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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