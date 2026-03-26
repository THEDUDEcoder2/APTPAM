package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.EmpresaService;
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

    private final OfertaService ofertaService = new OfertaService();
    private final EmpresaService empresaService = new EmpresaService();

    // Setters para inyección desde EmpresasController
    public void setFormulariosTable(TableView<Oferta> table) { this.formulariosTable = table; }
    public void setTituloColumn(TableColumn<Oferta, String> col) { this.tituloColumn = col; }
    public void setFechaColumn(TableColumn<Oferta, String> col) { this.fechaColumn = col; }
    public void setAccionesColumn(TableColumn<Oferta, Void> col) { this.accionesColumn = col; }
    public void setMensajeVacioLabel(Label label) { this.mensajeVacioLabel = label; }

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

        if (accionesColumn != null) {
            agregarBotonAbrir();
        }

        cargarOfertas();
    }

    // Método para refrescar la tabla desde otros controladores
    public void refrescarTabla() {
        cargarOfertas();
    }

    private void agregarBotonAbrir() {
        accionesColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Abrir");
            {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                btn.setOnAction(e -> {
                    Oferta oferta = getTableRow().getItem();
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
    }

    private void abrirDetalleEmpresa(Oferta oferta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleFormulario.fxml"));
            Parent root = loader.load();

            // Importante: usar el nombre completo de la clase
            com.example.trabajos.DetalleFormularioController controller = loader.getController();
            controller.setEsDesdeEmpresas(true);
            controller.mostrarOferta(oferta);

            Stage stage = (Stage) formulariosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Detalle de Oferta");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el detalle de la oferta: " + e.getMessage());
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
}