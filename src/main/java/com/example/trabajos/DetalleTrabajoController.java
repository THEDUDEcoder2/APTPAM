package com.example.trabajos;

import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.TrabajadorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.time.LocalDateTime;

public class DetalleTrabajoController {

    @FXML private Label nombreEmpresaLabel;
    @FXML private Label herramientaLabel;
    @FXML private Label idiomasLabel;
    @FXML private Label domicilioLabel;
    @FXML private Label gmailLabel;
    @FXML private Label telefonoLabel;

    @FXML private Label puestoLabel;
    @FXML private Label horarioLabel;
    @FXML private Label sueldoLabel;
    @FXML private Label nivelEstudioLabel;
    @FXML private Label experienciaLabel;
    @FXML private Label descripcionLabel;
    @FXML private Label estadoLabel;

    // NUEVOS: Fecha de expiración
    @FXML private Label fechaExpiracionLabel;
    @FXML private Label diasRestantesLabel;

    @FXML private Button contactarButton;
    @FXML private Button cerrarButton;

    private Oferta oferta;
    private Trabajador trabajadorActual;
    private Postulacion postulacionExistente;
    private String emailUsuario;

    private PostulacionService postulacionService = new PostulacionService();
    private TrabajadorService trabajadorService = new TrabajadorService();
    private OfertaService ofertaService = new OfertaService();

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
        cargarDatos();
    }

    public void setTrabajadorActual(Trabajador trabajador) {
        this.trabajadorActual = trabajador;
    }

    public void setEmailUsuario(String email) {
        this.emailUsuario = email;
    }

    private void cargarDatos() {
        if (oferta == null) return;

        mostrarDetallesOferta();
        cargarTrabajadorSiEsNecesario();
        verificarEstadoPostulacion();
        mostrarFechaExpiracion();
    }

    private void mostrarFechaExpiracion() {
        if (fechaExpiracionLabel != null) {
            fechaExpiracionLabel.setText(oferta.getFechaExpiracionFormateada());
        }
        if (diasRestantesLabel != null) {
            if (oferta.isExpirada()) {
                diasRestantesLabel.setText("⚠️ ESTA OFERTA HA EXPIRADO ⚠️");
                diasRestantesLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                if (contactarButton != null) {
                    contactarButton.setDisable(true);
                    contactarButton.setText("Oferta Expirada");
                }
            } else {
                diasRestantesLabel.setText(oferta.getDiasRestantesTexto());
                if (oferta.getDiasRestantes() <= 7) {
                    diasRestantesLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                } else {
                    diasRestantesLabel.setStyle("-fx-text-fill: #27ae60;");
                }
            }
        }
    }

    private void mostrarDetallesOferta() {
        if (oferta == null) return;

        if (oferta.getEmpresa() != null) {
            nombreEmpresaLabel.setText(oferta.getEmpresa().getNombreEmpresa() != null ?
                    oferta.getEmpresa().getNombreEmpresa() : "No especificado");
            domicilioLabel.setText(oferta.getEmpresa().getDomicilioCompleto());
            gmailLabel.setText(oferta.getEmpresa().getCorreoElectronico() != null ?
                    oferta.getEmpresa().getCorreoElectronico() : "No especificado");
            telefonoLabel.setText(oferta.getEmpresa().getNumTelefono() != null ?
                    oferta.getEmpresa().getNumTelefono() : "No especificado");
        } else {
            nombreEmpresaLabel.setText("No especificado");
            domicilioLabel.setText("No especificado");
            gmailLabel.setText("No especificado");
            telefonoLabel.setText("No especificado");
        }

        if (oferta.getEmpresa() != null && oferta.getEmpresa().getSectorActividad() != null) {
            herramientaLabel.setText(oferta.getEmpresa().getSectorActividad().getTipoSectorActividad() != null ?
                    oferta.getEmpresa().getSectorActividad().getTipoSectorActividad() : "No especificado");
        } else {
            herramientaLabel.setText("No especificado");
        }

        idiomasLabel.setText(oferta.getIdiomasRequeridos() != null && !oferta.getIdiomasRequeridos().isEmpty() ?
                oferta.getIdiomasRequeridos() : "No especificado");

        puestoLabel.setText(oferta.getPuesto_trabajo() != null ? oferta.getPuesto_trabajo() : "No especificado");
        horarioLabel.setText(oferta.getJornada_laboral() != null ? oferta.getJornada_laboral() : "No especificado");

        if (oferta.getSalario() != null && oferta.getSalario().getTipoSalario() != null) {
            sueldoLabel.setText(oferta.getSalario().getTipoSalario());
        } else {
            sueldoLabel.setText("No especificado");
        }

        nivelEstudioLabel.setText(oferta.getNivel_estudio() != null ? oferta.getNivel_estudio() : "No especificado");
        experienciaLabel.setText(oferta.getExperiencia() != null ? oferta.getExperiencia() : "No especificado");
        descripcionLabel.setText(oferta.getDescripcion_trabajo() != null ? oferta.getDescripcion_trabajo() : "No especificado");
    }

    private void cargarTrabajadorSiEsNecesario() {
        if (trabajadorActual == null) {
            if (emailUsuario != null) {
                trabajadorActual = trabajadorService.obtenerTrabajadorPorEmail(emailUsuario);
            }

            if (trabajadorActual == null) {
                Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
                if (usuario != null && !usuario.isEsEmpresa()) {
                    trabajadorActual = trabajadorService.obtenerTrabajadorPorEmail(usuario.getEmail());
                }
            }
        }
    }

    private void verificarEstadoPostulacion() {
        if (trabajadorActual != null && oferta != null) {
            Oferta ofertaBD = ofertaService.obtenerOfertaPorId(oferta.getIdOferta());
            if (ofertaBD != null) {
                postulacionExistente = postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, ofertaBD);
                actualizarInterfazSegunEstado();
            }
        }
    }

    private void actualizarInterfazSegunEstado() {
        if (postulacionExistente == null) {
            estadoLabel.setText("NO POSTULADO");
            estadoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 17; -fx-font-weight: bold;");
            contactarButton.setText("✓ Postularme");
            contactarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            contactarButton.setDisable(false);
        } else {
            String estado = postulacionExistente.getEstado().toUpperCase();

            switch (estado) {
                case "PENDIENTE":
                    estadoLabel.setText("⏳ EN ESPERA - La empresa revisará tu solicitud");
                    estadoLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 17; -fx-font-weight: bold;");
                    contactarButton.setText("Postulación Enviada");
                    contactarButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                    contactarButton.setDisable(true);
                    break;

                case "ACEPTADO":
                    estadoLabel.setText("✅ ACEPTADO - ¡Felicidades! La empresa te contactará pronto");
                    estadoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 17; -fx-font-weight: bold;");
                    contactarButton.setText("Aceptado");
                    contactarButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    contactarButton.setDisable(true);
                    break;

                case "RECHAZADO":
                    estadoLabel.setText("❌ RECHAZADO - No fuiste seleccionado para esta vacante");
                    estadoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 17; -fx-font-weight: bold;");
                    contactarButton.setText("Rechazado");
                    contactarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    contactarButton.setDisable(true);
                    break;

                default:
                    estadoLabel.setText(estado);
                    estadoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 17; -fx-font-weight: bold;");
                    contactarButton.setText("Ver Detalles");
                    contactarButton.setDisable(false);
                    break;
            }
        }
    }

    @FXML
    private void aceptarVacante() {
        // VALIDACIÓN: Oferta expirada
        if (oferta != null && oferta.isExpirada()) {
            mostrarAlerta("Oferta Expirada",
                    "❌ Esta oferta ya expiró el " + oferta.getFechaExpiracionFormateada() +
                            ".\n\nNo es posible postularse a ofertas vencidas.");
            return;
        }

        if (postulacionExistente != null) {
            mostrarAlerta("Información",
                    "Ya estás postulado a esta vacante.\n" +
                            "Estado actual: " + postulacionExistente.getEstado().toUpperCase() + "\n\n" +
                            "No puedes postularte dos veces a la misma oferta.");
            return;
        }

        if (trabajadorActual == null) {
            cargarTrabajadorSiEsNecesario();

            if (trabajadorActual == null) {
                mostrarAlerta("Error", "No se pudo identificar tu cuenta de trabajador.");
                return;
            }
        }

        // VALIDACIÓN: Cuenta debe estar activa
        if (!trabajadorActual.isActivo()) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("❌ Cuenta desactivada");
            alerta.setHeaderText("No puedes postularte a ofertas");
            alerta.setContentText(
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "         CUENTA DESACTIVADA\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "❌ Tu cuenta está INACTIVA.\n\n" +
                            "📌 Para poder postularte a nuevas vacantes:\n" +
                            "   • Ve al panel principal\n" +
                            "   • Haz clic en el botón ⛔ INACTIVO\n" +
                            "   • Confirma que quieres reactivar tu cuenta\n" +
                            "   • Espera a que el botón se ponga VERDE (✅ ACTIVO)\n\n" +
                            "¡Reactivar tu cuenta es gratuito y toma solo unos segundos!\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );
            alerta.getDialogPane().setMinHeight(400);
            alerta.getDialogPane().setMinWidth(500);
            alerta.showAndWait();
            return;
        }

        if (oferta == null) {
            mostrarAlerta("Error", "No se pudo identificar la oferta.");
            return;
        }

        try {
            Oferta ofertaBD = ofertaService.obtenerOfertaPorId(oferta.getIdOferta());
            if (ofertaBD == null) {
                mostrarAlerta("Error", "La oferta ya no está disponible.");
                return;
            }

            postulacionExistente = postulacionService.obtenerPostulacionPorTrabajadorYOferta(trabajadorActual, ofertaBD);
            if (postulacionExistente != null) {
                mostrarAlerta("Información", "Ya estás postulado a esta vacante.");
                actualizarInterfazSegunEstado();
                return;
            }

            Postulacion nuevaPostulacion = new Postulacion(trabajadorActual, ofertaBD);
            nuevaPostulacion.setEstado("PENDIENTE");
            nuevaPostulacion.setFechaPostulacion(LocalDateTime.now());

            postulacionService.guardarPostulacion(nuevaPostulacion);

            postulacionExistente = nuevaPostulacion;
            actualizarInterfazSegunEstado();

            mostrarAlerta("✅ Postulación Exitosa",
                    "¡Te has postulado exitosamente!\n\n" +
                            "📋 Detalles de tu postulación:\n" +
                            "• Puesto: " + ofertaBD.getPuesto_trabajo() + "\n" +
                            "• Empresa: " + ofertaBD.getEmpresa().getNombreEmpresa() + "\n" +
                            "• Estado: PENDIENTE ⏳\n\n" +
                            "La empresa revisará tu solicitud y te notificará su decisión.\n" +
                            "Puedes ver el estado actualizado en la lista de ofertas.");

        } catch (Exception e) {
            mostrarAlerta("❌ Error",
                    "No se pudo completar la postulación.\n" +
                            "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onCerrarClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Trabajos.fxml"));
            Parent root = fxmlLoader.load();

            TrabajosController controller = fxmlLoader.getController();
            controller.refrescarTabla();

            Stage stage = (Stage) cerrarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Buscar Trabajos");

        } catch (IOException e) {
            e.printStackTrace();
            Stage stage = (Stage) cerrarButton.getScene().getWindow();
            stage.close();
        }
    }
}