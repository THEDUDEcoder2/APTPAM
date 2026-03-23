package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import java.time.LocalDateTime;

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
    @FXML private Label descripcionLabel;
    @FXML private Label mensajePersonalLabel;
    @FXML private Label estadoLabel;

    @FXML private Button cerrarButton;
    @FXML private Button verPostulantesButton;
    @FXML private Button verNotaButton;

    private boolean soloLectura = false;
    private boolean esDesdeEmpresas = false;
    private Oferta ofertaActual;
    private Trabajador trabajadorActual;

    private PostulacionService postulacionService = new PostulacionService();

    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
        if (soloLectura) {
            if (cerrarButton != null) cerrarButton.setText("Volver");
            if (verPostulantesButton != null) verPostulantesButton.setVisible(false);
        } else {
            if (verPostulantesButton != null) verPostulantesButton.setVisible(false);
        }
    }

    public void setEsDesdeEmpresas(boolean esDesdeEmpresas) {
        this.esDesdeEmpresas = esDesdeEmpresas;
        if (verPostulantesButton != null) verPostulantesButton.setVisible(false);

        if (esDesdeEmpresas) {
            if (verPostulantesButton != null) {
                verPostulantesButton.setText("Ver Postulantes");
                verPostulantesButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
                verPostulantesButton.setVisible(true);
            }
            if (verNotaButton != null) verNotaButton.setVisible(false);
        }
    }

    public void setTrabajadorActual(Trabajador trabajador) {
        this.trabajadorActual = trabajador;
        actualizarEstadoPostulacion();
    }

    public void mostrarOferta(Oferta oferta) {
        this.ofertaActual = oferta;

        if (nombreEmpresaLabel != null) nombreEmpresaLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getNombreEmpresa() : "No especificado");
        if (herramientaLabel != null) herramientaLabel.setText("No especificado");
        if (idiomasLabel != null) idiomasLabel.setText(oferta.getIdiomasRequeridos());
        if (domicilioLabel != null) domicilioLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getDomicilioCompleto() : "No especificado");
        if (gmailLabel != null) gmailLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getCorreoElectronico() : "No especificado");
        if (telefonoLabel != null) telefonoLabel.setText(oferta.getEmpresa() != null ? oferta.getEmpresa().getNumTelefono() : "No especificado");
        if (puestoLabel != null) puestoLabel.setText(oferta.getPuesto_trabajo());
        if (horarioLabel != null) horarioLabel.setText(oferta.getJornada_laboral());
        if (sueldoLabel != null) sueldoLabel.setText(oferta.getSalario() != null ? oferta.getSalario().getTipoSalario() : "No especificado");
        if (descripcionLabel != null) descripcionLabel.setText(oferta.getDescripcion_trabajo());

        if (oferta.esOfertaPrivada() && oferta.getMensajePersonal() != null && !oferta.getMensajePersonal().isEmpty()) {
            if (mensajePersonalLabel != null) mensajePersonalLabel.setText(oferta.getMensajePersonal());
        } else {
            if (mensajePersonalLabel != null) mensajePersonalLabel.setText("Sin mensaje personal");
        }

        if (soloLectura && trabajadorActual != null) {
            actualizarEstadoPostulacion();
        }
    }

    private void actualizarEstadoPostulacion() {
        if (esDesdeEmpresas) return;

        if (trabajadorActual != null && ofertaActual != null) {
            Postulacion postulacion = obtenerPostulacionActual();

            if (postulacion != null) {
                String estado = postulacion.getEstado();
                if (estadoLabel != null) {
                    estadoLabel.setVisible(true);
                    switch (estado) {
                        case "PENDIENTE":
                            estadoLabel.setText("⏳ Estado: EN ESPERA");
                            estadoLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "ACEPTADO":
                            estadoLabel.setText("✅ Estado: ACEPTADO");
                            estadoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "RECHAZADO":
                            estadoLabel.setText("❌ Estado: RECHAZADO");
                            estadoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        default:
                            estadoLabel.setVisible(false);
                            break;
                    }
                }
                if (verNotaButton != null) verNotaButton.setVisible(postulacion.tieneNotaEmpresa());
            } else {
                if (estadoLabel != null) {
                    estadoLabel.setText("⏳ Estado: PENDIENTE");
                    estadoLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    estadoLabel.setVisible(true);
                }
                if (verNotaButton != null) verNotaButton.setVisible(false);
            }
        }
    }

    private Postulacion obtenerPostulacionActual() {
        if (trabajadorActual == null || ofertaActual == null) return null;
        return postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, ofertaActual);
    }

    @FXML
    private void onVerNotaClick() {
        Postulacion postulacion = obtenerPostulacionActual();
        if (postulacion == null || !postulacion.tieneNotaEmpresa()) return;
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("📝 Mensaje de la Empresa");
        alert.setHeaderText("Mensaje de " + (ofertaActual.getEmpresa() != null ? ofertaActual.getEmpresa().getNombreEmpresa() : "la Empresa"));
        alert.setContentText(postulacion.getNotaEmpresa());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(400);
        alert.showAndWait();
    }

    @FXML
    private void onCerrarClick() {
        if (esDesdeEmpresas || soloLectura) {
            regresarATablaOfertas();
            return;
        }
        if (cerrarButton != null) {
            Stage stage = (Stage) cerrarButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void onVerPostulantesClick() {
        abrirVentanaPostulantes();
    }

    private void abrirVentanaPostulantes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Postulantes.fxml"));
            Parent root = fxmlLoader.load();
            PostulantesController controller = fxmlLoader.getController();
            controller.setOferta(ofertaActual);
            Stage stage = (Stage) cerrarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana de postulantes.");
        }
    }

    private void regresarATablaOfertas() {
        try {
            String fxml;
            String titulo;

            if (soloLectura) {
                fxml = "Trabajos.fxml";
                titulo = "Buscar Trabajos";
            } else {
                fxml = "FormulariosTable.fxml";
                titulo = "Mis Ofertas de Trabajo";
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/" + fxml));
            Parent root = fxmlLoader.load();

            if (fxml.equals("Trabajos.fxml")) {
                TrabajosController controller = fxmlLoader.getController();
                controller.refrescarTabla();
            } else if (fxml.equals("FormulariosTable.fxml")) {
                FormulariosTableController controller = fxmlLoader.getController();
                controller.refrescarTabla();
            }

            Stage stage = (Stage) cerrarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al regresar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}