package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.OfertaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DetalleTrabajoPrivadoController {

    @FXML private Label nombreEmpresaLabel;
    @FXML private Label idiomasLabel;
    @FXML private Label domicilioLabel;
    @FXML private Label gmailLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label puestoLabel;
    @FXML private Label horarioLabel;
    @FXML private Label sueldoLabel;
    @FXML private Label nivelEstudioLabel;
    @FXML private Label descripcionLabel;
    @FXML private Label mensajePersonalLabel;
    @FXML private Label estadoActualLabel;

    @FXML private Button aceptarButton;
    @FXML private Button rechazarButton;
    @FXML private Button volverButton;

    @FXML private VBox notaContainer;
    @FXML private TextArea notaTextArea;
    @FXML private Label notaLabel;

    private Oferta ofertaActual;
    private Trabajador trabajadorActual;
    private Postulacion postulacionActual;
    private PostulacionService postulacionService = new PostulacionService();
    private OfertaService ofertaService = new OfertaService();

    public void setOferta(Oferta oferta) {
        this.ofertaActual = oferta;
        cargarDatosOferta();
    }

    public void setTrabajadorActual(Trabajador trabajador) {
        this.trabajadorActual = trabajador;
        cargarPostulacion();
    }

    private void cargarPostulacion() {
        if (trabajadorActual != null && ofertaActual != null) {
            postulacionActual = postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, ofertaActual);
            actualizarEstadoVisual();
            cargarNotaExistente();
        }
    }

    private void cargarNotaExistente() {
        if (postulacionActual != null && postulacionActual.tieneNotaEmpresa()) {
            String nota = postulacionActual.getNotaEmpresa();
            if (nota.contains("RAZÓN DEL TRABAJADOR")) {
                if (notaContainer != null) {
                    notaContainer.setVisible(true);
                    String notaLimpia = nota.replace("📝 RAZÓN DEL TRABAJADOR (ACEPTADO):\n", "")
                            .replace("📝 RAZÓN DEL TRABAJADOR (RECHAZADO):\n", "");
                    notaTextArea.setText(notaLimpia);
                    notaTextArea.setEditable(false);
                    notaLabel.setText("📝 Tu nota (ya registrada):");
                }
            }
        }
    }

    private void cargarDatosOferta() {
        if (ofertaActual == null) return;

        if (ofertaActual.getEmpresa() != null) {
            nombreEmpresaLabel.setText(ofertaActual.getEmpresa().getNombreEmpresa());
            domicilioLabel.setText(ofertaActual.getEmpresa().getDomicilioCompleto());
            gmailLabel.setText(ofertaActual.getEmpresa().getCorreoElectronico());
            telefonoLabel.setText(ofertaActual.getEmpresa().getNumTelefono());
        }

        String mensaje = ofertaActual.getMensajePersonal();
        if (mensaje != null && !mensaje.isEmpty()) {
            mensajePersonalLabel.setText(mensaje);
        } else {
            mensajePersonalLabel.setText("La empresa no ha dejado un mensaje adicional.");
        }

        puestoLabel.setText(ofertaActual.getPuesto_trabajo());
        idiomasLabel.setText(ofertaActual.getIdiomasRequeridos() != null && !ofertaActual.getIdiomasRequeridos().isEmpty() ?
                ofertaActual.getIdiomasRequeridos() : "No especificado");
        horarioLabel.setText(ofertaActual.getJornada_laboral() != null ?
                ofertaActual.getJornada_laboral() : "No especificado");
        sueldoLabel.setText(ofertaActual.getSalario() != null ?
                ofertaActual.getSalario().getTipoSalario() : "No especificado");
        nivelEstudioLabel.setText(ofertaActual.getNivel_estudio() != null ?
                ofertaActual.getNivel_estudio() : "No especificado");
        descripcionLabel.setText(ofertaActual.getDescripcion_trabajo() != null ?
                ofertaActual.getDescripcion_trabajo() : "No especificado");
    }

    private void actualizarEstadoVisual() {
        if (postulacionActual == null) {
            estadoActualLabel.setText("Estado: Pendiente ⏳");
            estadoActualLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16; -fx-font-weight: bold;");
            aceptarButton.setDisable(false);
            aceptarButton.setVisible(true);
            aceptarButton.setManaged(true);
            rechazarButton.setDisable(false);
            rechazarButton.setVisible(true);
            rechazarButton.setManaged(true);

            if (notaContainer != null) {
                notaContainer.setVisible(true);
                notaTextArea.setEditable(true);
                notaTextArea.clear();
                notaLabel.setText("📝 Escribe tu razón (obligatorio):");
            }
            return;
        }

        String estado = postulacionActual.getEstado().toUpperCase();

        switch (estado) {
            case "ACEPTADO":
                estadoActualLabel.setText("✅ Estado: ACEPTADO");
                estadoActualLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16; -fx-font-weight: bold;");
                aceptarButton.setDisable(true);
                aceptarButton.setText("✓ Aceptado");
                aceptarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                rechazarButton.setDisable(true);
                rechazarButton.setVisible(false);
                rechazarButton.setManaged(false);
                break;

            case "RECHAZADO":
                estadoActualLabel.setText("❌ Estado: RECHAZADO");
                estadoActualLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16; -fx-font-weight: bold;");
                aceptarButton.setDisable(true);
                aceptarButton.setVisible(false);
                aceptarButton.setManaged(false);
                rechazarButton.setDisable(true);
                rechazarButton.setText("✗ Rechazado");
                rechazarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                break;

            case "PENDIENTE":
            default:
                estadoActualLabel.setText("Estado: Pendiente ⏳");
                estadoActualLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16; -fx-font-weight: bold;");
                aceptarButton.setDisable(false);
                aceptarButton.setText("✓ Aceptar Oferta");
                aceptarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                rechazarButton.setDisable(false);
                rechazarButton.setText("✗ Rechazar Oferta");
                rechazarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                if (notaContainer != null) {
                    notaContainer.setVisible(true);
                    notaTextArea.setEditable(true);
                    notaTextArea.clear();
                    notaLabel.setText("📝 Escribe tu razón (obligatorio):");
                }
                break;
        }
    }

    @FXML
    private void onAceptarClick() {
        if (postulacionActual == null) {
            mostrarAlerta("Error", "No se encontró la postulación para esta oferta.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacionActual.getEstado())) {
            mostrarAlerta("Decisión ya tomada",
                    "Ya has tomado una decisión sobre esta oferta.\n" +
                            "Estado actual: " + postulacionActual.getEstado().toUpperCase() + "\n\n" +
                            "Las decisiones NO se pueden modificar.");
            return;
        }

        String nota = notaTextArea.getText().trim();
        if (nota.isEmpty()) {
            mostrarAlerta("Nota requerida",
                    "Por favor, escribe una nota explicando por qué aceptas esta oferta.\n\n" +
                            "La empresa necesita conocer tu motivación.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Aceptación");
        confirmacion.setHeaderText("¿Aceptas la oferta de " + ofertaActual.getEmpresa().getNombreEmpresa() + "?");
        confirmacion.setContentText("⚠️ ATENCIÓN: Esta decisión NO se puede cambiar después.\n\n" +
                "Tu nota será enviada a la empresa:\n" + nota + "\n\n" +
                "Al aceptar, la empresa será notificada y podrán contactarte para continuar con el proceso.");

        if (confirmacion.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            cambiarEstado("ACEPTADO", nota);
        }
    }

    @FXML
    private void onRechazarClick() {
        if (postulacionActual == null) {
            mostrarAlerta("Error", "No se encontró la postulación para esta oferta.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacionActual.getEstado())) {
            mostrarAlerta("Decisión ya tomada",
                    "Ya has tomado una decisión sobre esta oferta.\n" +
                            "Estado actual: " + postulacionActual.getEstado().toUpperCase() + "\n\n" +
                            "Las decisiones NO se pueden modificar.");
            return;
        }

        String nota = notaTextArea.getText().trim();
        if (nota.isEmpty()) {
            mostrarAlerta("Nota requerida",
                    "Por favor, escribe una nota explicando por qué rechazas esta oferta.\n\n" +
                            "Esto ayudará a la empresa a mejorar sus ofertas.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Rechazo");
        confirmacion.setHeaderText("¿Rechazas la oferta de " + ofertaActual.getEmpresa().getNombreEmpresa() + "?");
        confirmacion.setContentText("⚠️ ATENCIÓN: Esta decisión NO se puede cambiar después.\n\n" +
                "Tu nota será enviada a la empresa:\n" + nota + "\n\n" +
                "Al rechazar, la empresa será notificada.");

        if (confirmacion.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            cambiarEstado("RECHAZADO", nota);
        }
    }

    private void cambiarEstado(String nuevoEstado, String nota) {
        try {
            String notaFormateada = "📝 RAZÓN DEL TRABAJADOR (" + nuevoEstado + "):\n" + nota;

            postulacionActual.setEstado(nuevoEstado);
            postulacionActual.setNotaEmpresa(notaFormateada);
            postulacionService.actualizarPostulacion(postulacionActual);
            actualizarEstadoVisual();

            String mensaje = "";
            if ("ACEPTADO".equals(nuevoEstado)) {
                mensaje = "✅ Has ACEPTADO la oferta de " + ofertaActual.getEmpresa().getNombreEmpresa() +
                        ".\n\nTu nota ha sido enviada a la empresa.\n\n" +
                        "⚠️ Esta decisión es FINAL y no se puede cambiar.";
            } else if ("RECHAZADO".equals(nuevoEstado)) {
                mensaje = "❌ Has RECHAZADO la oferta de " + ofertaActual.getEmpresa().getNombreEmpresa() +
                        ".\n\nTu nota ha sido enviada a la empresa.\n\n" +
                        "⚠️ Esta decisión es FINAL y no se puede cambiar.";
            }

            mostrarAlerta("Decisión Registrada", mensaje);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo actualizar el estado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onVolverClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Trabajos.fxml"));
            Parent root = loader.load();

            TrabajosController controller = loader.getController();
            controller.refrescarTabla();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Buscar Trabajos");

        } catch (IOException e) {
            e.printStackTrace();
            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.close();
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