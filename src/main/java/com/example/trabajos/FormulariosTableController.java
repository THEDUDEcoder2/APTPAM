package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.EmpresaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FormulariosTableController {

    @FXML
    private TableView<Oferta> formulariosTable;

    @FXML
    private TableColumn<Oferta, String> tituloColumn;

    @FXML
    private TableColumn<Oferta, String> fechaColumn;

    @FXML
    private TableColumn<Oferta, Void> accionesColumn;

    @FXML
    private Label mensajeVacioLabel;

    private final OfertaService ofertaService = new OfertaService();
    private final EmpresaService empresaService = new EmpresaService();

    @FXML
    public void initialize() {
        tituloColumn.setCellValueFactory(new PropertyValueFactory<>("puesto_trabajo"));

        fechaColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFecha_publicacion()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
        );

        agregarBotonAbrir();

        cargarOfertas();
    }

    public void refrescarTabla() {
        cargarOfertas();
    }

    private void cargarOfertas() {
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();

        if (usuarioActual == null || !usuarioActual.isEsEmpresa()) {
            formulariosTable.setVisible(false);
            mensajeVacioLabel.setText("No hay sesión de empresa activa.");
            mensajeVacioLabel.setVisible(true);
            return;
        }

        Empresa empresa = empresaService.obtenerEmpresaPorEmail(usuarioActual.getEmail());

        if (empresa == null) {
            formulariosTable.setVisible(false);
            mensajeVacioLabel.setText("No se encontraron datos de la empresa.");
            mensajeVacioLabel.setVisible(true);
            return;
        }

        List<Oferta> ofertas = ofertaService.obtenerOfertasPorEmpresa(empresa);

        if (ofertas == null || ofertas.isEmpty()) {
            formulariosTable.setVisible(false);
            mensajeVacioLabel.setText("No has creado ninguna oferta aún.");
            mensajeVacioLabel.setVisible(true);
            return;
        }

        formulariosTable.getItems().setAll(ofertas);
        formulariosTable.setVisible(true);
        mensajeVacioLabel.setVisible(false);
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
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
            mostrarAlerta("Error", "No se pudo abrir el detalle de la oferta: " + e.getMessage());
        }
    }

    @FXML
    private void onVolverClick() {
        try {
            Parent root = cargarFXMLSeguro("Empresas.fxml");
            if (root == null) return;

            Stage stage = (Stage) mensajeVacioLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parent cargarFXMLSeguro(String nombre) {
        try {
            var url = getClass().getResource("/com/example/trabajos/" + nombre);
            if (url == null) {
                System.out.println("❌ Archivo FXML NO encontrado: " + nombre);
                return null;
            }
            FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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