package com.example.trabajos;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class DetalleTrabajadorController {

    @FXML private Label nombreLabel;
    @FXML private Label emailLabel;
    @FXML private Label fechaNacimientoLabel;
    @FXML private Label generoLabel;
    @FXML private Label nacionalidadLabel;
    @FXML private Label estadoCivilLabel;
    @FXML private Label rfcLabel;
    @FXML private Label curpLabel;
    @FXML private Label domicilioLabel;
    @FXML private Label codigoPostalLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label herramientasLabel;
    @FXML private Label idiomasLabel;
    @FXML private Label nivelEstudioLabel;
    @FXML private Label especialidadLabel;
    @FXML private Label anosExperienciaLabel;
    @FXML private Label discapacidadLabel;
    @FXML private Label experienciaLabel;
    @FXML private Label habilidadesLabel;

    @FXML private Button volverButton;
    @FXML private Button aceptarButton;
    @FXML private Button rechazarButton;

    private Trabajador trabajador;
    private Postulacion postulacion;
    private PostulacionService postulacionService = new PostulacionService();
    private PostulantesController postulantesController;
    private String origen; // Para saber de dónde viene

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        this.postulacion = null;
        this.origen = "trabajadoresDisponibles"; // Viene desde trabajadores disponibles
        if (trabajador != null) {
            mostrarDetalles();
            if (aceptarButton != null) aceptarButton.setVisible(false);
            if (rechazarButton != null) rechazarButton.setVisible(false);
        }
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
        this.origen = "postulantes"; // Viene desde postulantes
        if (postulacion != null && postulacion.getTrabajador() != null) {
            this.trabajador = postulacion.getTrabajador();
            mostrarDetalles();
            actualizarEstadoBotones();
        }
    }

    public void setPostulantesController(PostulantesController controller) {
        this.postulantesController = controller;
    }

    private void mostrarDetalles() {
        if (trabajador == null) return;

        String nombreCompleto = trabajador.getNombre() + " " +
                (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : "") + " " +
                (trabajador.getApellidoMaterno() != null ? trabajador.getApellidoMaterno() : "");

        nombreLabel.setText(nombreCompleto.trim());
        emailLabel.setText(trabajador.getCorreoElectronico() != null ? trabajador.getCorreoElectronico() : "No especificado");

        if (trabajador.getFechaNacimiento() != null) {
            fechaNacimientoLabel.setText(trabajador.getFechaNacimiento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            fechaNacimientoLabel.setText("No especificado");
        }

        generoLabel.setText(trabajador.getGenero() != null ? trabajador.getGenero().getTipoGenero() : "No especificado");
        nacionalidadLabel.setText(trabajador.getNacionalidad() != null ? trabajador.getNacionalidad().getNombreNacionalidad() : "No especificado");
        estadoCivilLabel.setText(trabajador.getEstadoCivil() != null ? trabajador.getEstadoCivil().getEstadoCivil() : "No especificado");
        rfcLabel.setText(trabajador.getRfc() != null ? trabajador.getRfc() : "No especificado");
        curpLabel.setText(trabajador.getCurp() != null ? trabajador.getCurp() : "No especificado");
        domicilioLabel.setText(trabajador.getDomicilioCompleto());
        codigoPostalLabel.setText(trabajador.getCodigoPostal() != null ? trabajador.getCodigoPostal() : "No especificado");
        telefonoLabel.setText(trabajador.getNumTelefono() != null ? trabajador.getNumTelefono() : "No especificado");
        herramientasLabel.setText(trabajador.getConocimientosHerramientas() != null ? trabajador.getConocimientosHerramientas() : "No especificado");

        String idiomasStr = "No especificado";
        if (trabajador.getTrabajadorIdiomas() != null && !trabajador.getTrabajadorIdiomas().isEmpty()) {
            StringBuilder idiomasBuilder = new StringBuilder();
            for (com.example.trabajos.models.TrabajadorIdioma ti : trabajador.getTrabajadorIdiomas()) {
                if (idiomasBuilder.length() > 0) idiomasBuilder.append(", ");
                idiomasBuilder.append(ti.getIdioma().getNombreIdioma());
            }
            idiomasStr = idiomasBuilder.toString();
        }
        idiomasLabel.setText(idiomasStr);

        nivelEstudioLabel.setText(trabajador.getNivelEstudio() != null ? trabajador.getNivelEstudio() : "No especificado");
        especialidadLabel.setText(trabajador.getEspecialidad() != null ? trabajador.getEspecialidad() : "No especificado");
        anosExperienciaLabel.setText(trabajador.getAnosExperiencia() != null ? trabajador.getAnosExperiencia().toString() + " años" : "No especificado");
        discapacidadLabel.setText(trabajador.getDiscapacidad() != null ? trabajador.getDiscapacidad() : "No especificado");
        experienciaLabel.setText(trabajador.getExperienciaLaboral() != null ? trabajador.getExperienciaLaboral() : "No especificado");
        habilidadesLabel.setText(trabajador.getHabilidades() != null ? trabajador.getHabilidades() : "No especificado");
    }

    private void actualizarEstadoBotones() {
        if (postulacion == null) {
            if (aceptarButton != null) aceptarButton.setDisable(false);
            if (rechazarButton != null) rechazarButton.setDisable(false);
            return;
        }

        String estado = postulacion.getEstado().toUpperCase();

        switch (estado) {
            case "ACEPTADO":
                if (aceptarButton != null) {
                    aceptarButton.setText("✅ ACEPTADO");
                    aceptarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                    aceptarButton.setDisable(true);
                }
                if (rechazarButton != null) rechazarButton.setDisable(true);
                break;

            case "RECHAZADO":
                if (rechazarButton != null) {
                    rechazarButton.setText("❌ RECHAZADO");
                    rechazarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    rechazarButton.setDisable(true);
                }
                if (aceptarButton != null) aceptarButton.setDisable(true);
                break;

            case "PENDIENTE":
            default:
                if (aceptarButton != null) {
                    aceptarButton.setDisable(false);
                    aceptarButton.setText("Aceptar");
                    aceptarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                }
                if (rechazarButton != null) {
                    rechazarButton.setDisable(false);
                    rechazarButton.setText("Rechazar");
                    rechazarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                }
                break;
        }
    }

    @FXML
    private void onAceptarClick() {
        if (postulacion == null) {
            mostrarAlerta("Error", "No se encontró la postulación para este trabajador.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacion.getEstado())) {
            mostrarAlerta("Decisión Tomada",
                    "Ya has tomado una decisión sobre este trabajador.\n" +
                            "Estado actual: " + postulacion.getEstado() + "\n\n" +
                            "Las decisiones NO se pueden modificar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Aceptación");
        confirmacion.setHeaderText("¿Estás seguro de aceptar a " + trabajador.getNombre() + "?");
        confirmacion.setContentText("⚠️ ATENCIÓN: Esta decisión NO se puede cambiar después.");

        if (confirmacion.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            cambiarEstado("ACEPTADO");
        }
    }

    @FXML
    private void onRechazarClick() {
        if (postulacion == null) {
            mostrarAlerta("Error", "No se encontró la postulación para este trabajador.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacion.getEstado())) {
            mostrarAlerta("Decisión Tomada",
                    "Ya has tomado una decisión sobre este trabajador.\n" +
                            "Estado actual: " + postulacion.getEstado() + "\n\n" +
                            "Las decisiones NO se pueden modificar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Rechazo");
        confirmacion.setHeaderText("¿Estás seguro de rechazar a " + trabajador.getNombre() + "?");
        confirmacion.setContentText("⚠️ ATENCIÓN: Esta decisión NO se puede cambiar después.");

        if (confirmacion.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            cambiarEstado("RECHAZADO");
        }
    }

    private void cambiarEstado(String nuevoEstado) {
        try {
            postulacion.setEstado(nuevoEstado);
            postulacionService.actualizarPostulacion(postulacion);
            actualizarEstadoBotones();

            if (postulantesController != null) {
                postulantesController.refrescarTabla();
            }

            String mensaje = "";
            if ("ACEPTADO".equals(nuevoEstado)) {
                mensaje = "✅ Has ACEPTADO a " + trabajador.getNombre() +
                        " para la vacante.\n\n" +
                        "⚠️ Esta decisión es FINAL y no se puede cambiar.";
            } else if ("RECHAZADO".equals(nuevoEstado)) {
                mensaje = "❌ Has RECHAZADO a " + trabajador.getNombre() +
                        " para la vacante.\n\n" +
                        "⚠️ Esta decisión es FINAL y no se puede cambiar.";
            }

            mostrarAlerta("Decisión Tomada", mensaje);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo actualizar el estado: " + e.getMessage());
        }
    }

    @FXML
    private void onVolverClick() {
        try {
            String fxmlDestino;
            String titulo;

            if ("postulantes".equals(origen)) {
                // Viene desde postulantes (ofertas públicas)
                fxmlDestino = "Postulantes.fxml";
                titulo = "Postulantes";
            } else {
                // Viene desde trabajadores disponibles (ofertas privadas)
                fxmlDestino = "TrabajadoresDisponibles.fxml";
                titulo = "Trabajadores Disponibles";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/" + fxmlDestino));
            Parent root = loader.load();

            if ("postulantes".equals(origen) && postulacion != null && postulacion.getOferta() != null) {
                PostulantesController controller = loader.getController();
                controller.setOferta(postulacion.getOferta());
            }

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);

        } catch (Exception e) {
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