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
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormulariosTableController {

    private TableView<Oferta> formulariosTable;
    private TableColumn<Oferta, String> tituloColumn;
    private TableColumn<Oferta, String> fechaColumn;
    private TableColumn<Oferta, Void> accionesColumn;
    private Label mensajeVacioLabel;
    private TableColumn<Oferta, String> tipoOfertaColumn;
    private TableColumn<Oferta, String> estadoPostulacionColumn;

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
        System.out.println("=== INICIALIZANDO FormulariosTableController ===");

        // Configurar columnas
        if (tituloColumn != null) {
            tituloColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));
            System.out.println("✅ tituloColumn configurada");
        }

        if (fechaColumn != null) {
            fechaColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getFecha_publicacion()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
            );
            System.out.println("✅ fechaColumn configurada");
        }

        // Columna TIPO
        if (tipoOfertaColumn != null) {
            tipoOfertaColumn.setCellValueFactory(cellData -> {
                Oferta oferta = cellData.getValue();
                String tipo = oferta.getTipoOferta();
                if ("PRIVADA".equals(tipo)) {
                    return new javafx.beans.property.SimpleStringProperty("🔷 PRIVADA");
                } else {
                    return new javafx.beans.property.SimpleStringProperty("🌐 PÚBLICA");
                }
            });
            System.out.println("✅ tipoOfertaColumn configurada");
        } else {
            System.out.println("❌ tipoOfertaColumn es NULL");
        }

        // Columna ESTADO
        if (estadoPostulacionColumn != null) {
            estadoPostulacionColumn.setCellValueFactory(cellData -> {
                Oferta oferta = cellData.getValue();
                String tipo = oferta.getTipoOferta();

                if ("PUBLICA".equals(tipo)) {
                    return new javafx.beans.property.SimpleStringProperty("📋 PÚBLICA");
                } else if ("PRIVADA".equals(tipo) && oferta.getTrabajadorDestino() != null) {
                    Postulacion postulacion = postulacionService.obtenerPostulacionPorTrabajadorYOferta(
                            oferta.getTrabajadorDestino(), oferta);
                    if (postulacion != null) {
                        String estado = postulacion.getEstado();
                        if ("ACEPTADO".equals(estado)) {
                            return new javafx.beans.property.SimpleStringProperty("✅ ACEPTADO");
                        } else if ("RECHAZADO".equals(estado)) {
                            return new javafx.beans.property.SimpleStringProperty("❌ RECHAZADO");
                        } else {
                            return new javafx.beans.property.SimpleStringProperty("⏳ EN ESPERA");
                        }
                    }
                    return new javafx.beans.property.SimpleStringProperty("⏳ SIN RESPUESTA");
                }
                return new javafx.beans.property.SimpleStringProperty("📋 PÚBLICA");
            });
            System.out.println("✅ estadoPostulacionColumn configurada");
        } else {
            System.out.println("❌ estadoPostulacionColumn es NULL");
        }

        // Columna ACCIONES
        if (accionesColumn != null) {
            accionesColumn.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("Abrir");
                {
                    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                    btn.setOnAction(e -> {
                        Oferta oferta = getTableView().getItems().get(getIndex());
                        if (oferta != null) {
                            abrirDetalleEmpresa(oferta);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else setGraphic(btn);
                }
            });
            System.out.println("✅ accionesColumn configurada");
        }

        // Cargar datos
        cargarOfertas();
    }

    public void refrescarTabla() {
        cargarOfertas();
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
        System.out.println("=== CARGANDO OFERTAS ===");

        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();
        if (usuarioActual == null || !usuarioActual.isEsEmpresa()) {
            System.out.println("❌ No hay sesión de empresa activa");
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No hay sesión de empresa activa.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        Empresa empresa = empresaService.obtenerEmpresaPorEmail(usuarioActual.getEmail());
        if (empresa == null) {
            System.out.println("❌ Empresa no encontrada");
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No se encontraron datos de la empresa.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        System.out.println("✅ Empresa encontrada: " + empresa.getNombreEmpresa());

        List<Oferta> ofertas = ofertaService.obtenerOfertasPorEmpresa(empresa);

        System.out.println("📊 Ofertas encontradas: " + (ofertas != null ? ofertas.size() : 0));

        if (ofertas == null || ofertas.isEmpty()) {
            if (formulariosTable != null) formulariosTable.setVisible(false);
            if (mensajeVacioLabel != null) {
                mensajeVacioLabel.setText("No has creado ninguna oferta aún.");
                mensajeVacioLabel.setVisible(true);
            }
            return;
        }

        // Mostrar debug de cada oferta
        for (Oferta o : ofertas) {
            System.out.println("  - " + o.getPuesto_trabajo() + " | Tipo: " + o.getTipoOferta());
        }

        // Limpiar y agregar datos
        if (formulariosTable != null) {
            formulariosTable.getItems().clear();
            formulariosTable.getItems().addAll(ofertas);
            formulariosTable.setVisible(true);
            formulariosTable.refresh();
            System.out.println("✅ Tabla actualizada con " + ofertas.size() + " ofertas");
        }

        if (mensajeVacioLabel != null) {
            mensajeVacioLabel.setVisible(false);
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