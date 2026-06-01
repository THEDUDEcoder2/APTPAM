package com.example.trabajos;

import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.TrabajadorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML private ImageView fotoPerfilImageView;

    @FXML private Label calificacionPromedioLabel;
    @FXML private Label calificacionEstrellasLabel;
    @FXML private Label calificacionTotalLabel;
    @FXML private VBox calificacionesDetalleContainer;

    @FXML private Button volverButton;
    @FXML private Button aceptarButton;
    @FXML private Button rechazarButton;

    private Trabajador trabajador;
    private Postulacion postulacion;
    private PostulacionService postulacionService = new PostulacionService();
    private TrabajadorService trabajadorService = new TrabajadorService();
    private PostulantesController postulantesController;
    private String origen;

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        this.postulacion = null;
        this.origen = "trabajadoresDisponibles";
        if (trabajador != null) {
            mostrarDetalles();
            if (aceptarButton != null) aceptarButton.setVisible(false);
            if (rechazarButton != null) rechazarButton.setVisible(false);
        }
    }

    public void setTrabajadorDesdeMatch(Trabajador trabajador) {
        this.trabajador = trabajador;
        this.postulacion = null;
        this.origen = "match";
        if (trabajador != null) {
            mostrarDetalles();
            if (aceptarButton != null) aceptarButton.setVisible(false);
            if (rechazarButton != null) rechazarButton.setVisible(false);
        }
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
        this.origen = "postulantes";
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
        if (nombreLabel != null) nombreLabel.setText(nombreCompleto.trim());

        if (emailLabel != null) emailLabel.setText(trabajador.getCorreoElectronico() != null ? trabajador.getCorreoElectronico() : "No especificado");

        if (trabajador.getFechaNacimiento() != null) {
            if (fechaNacimientoLabel != null) {
                int edad = trabajador.getEdad();
                fechaNacimientoLabel.setText(trabajador.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (" + edad + " años)");
            }
        } else {
            if (fechaNacimientoLabel != null) fechaNacimientoLabel.setText("No especificado");
        }

        if (generoLabel != null) generoLabel.setText(trabajador.getGenero() != null ? trabajador.getGenero().getTipoGenero() : "No especificado");
        if (nacionalidadLabel != null) nacionalidadLabel.setText(trabajador.getNacionalidad() != null ? trabajador.getNacionalidad().getNombreNacionalidad() : "No especificado");
        if (estadoCivilLabel != null) estadoCivilLabel.setText(trabajador.getEstadoCivil() != null ? trabajador.getEstadoCivil().getEstadoCivil() : "No especificado");
        if (rfcLabel != null) rfcLabel.setText(trabajador.getRfc() != null ? trabajador.getRfc() : "No especificado");
        if (curpLabel != null) curpLabel.setText(trabajador.getCurp() != null ? trabajador.getCurp() : "No especificado");
        if (domicilioLabel != null) domicilioLabel.setText(trabajador.getDomicilioCompleto());
        if (codigoPostalLabel != null) codigoPostalLabel.setText(trabajador.getCodigoPostal() != null ? trabajador.getCodigoPostal() : "No especificado");
        if (telefonoLabel != null) telefonoLabel.setText(trabajador.getNumTelefono() != null ? trabajador.getNumTelefono() : "No especificado");
        if (herramientasLabel != null) herramientasLabel.setText(trabajador.getConocimientosHerramientas() != null ? trabajador.getConocimientosHerramientas() : "No especificado");

        if (fotoPerfilImageView != null) {
            byte[] fotoBytes = trabajador.getFotoPerfil();
            if (fotoBytes != null && fotoBytes.length > 0) {
                try {
                    Image foto = byteArrayAImagen(fotoBytes);
                    if (foto != null && !foto.isError()) {
                        fotoPerfilImageView.setImage(foto);
                        fotoPerfilImageView.setFitHeight(120);
                        fotoPerfilImageView.setFitWidth(120);
                        fotoPerfilImageView.setPreserveRatio(true);
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar foto: " + e.getMessage());
                }
            }
        }

        String idiomasStr = "No especificado";
        if (trabajador.getTrabajadorIdiomas() != null && !trabajador.getTrabajadorIdiomas().isEmpty()) {
            StringBuilder idiomasBuilder = new StringBuilder();
            for (com.example.trabajos.models.TrabajadorIdioma ti : trabajador.getTrabajadorIdiomas()) {
                if (idiomasBuilder.length() > 0) idiomasBuilder.append(", ");
                idiomasBuilder.append(ti.getIdioma().getNombreIdioma());
            }
            idiomasStr = idiomasBuilder.toString();
        }
        if (idiomasLabel != null) idiomasLabel.setText(idiomasStr);

        if (nivelEstudioLabel != null) nivelEstudioLabel.setText(trabajador.getNivelEstudio() != null ? trabajador.getNivelEstudio() : "No especificado");
        if (especialidadLabel != null) especialidadLabel.setText(trabajador.getEspecialidad() != null ? trabajador.getEspecialidad() : "No especificado");
        if (anosExperienciaLabel != null) anosExperienciaLabel.setText(trabajador.getAnosExperiencia() != null ? trabajador.getAnosExperiencia().toString() + " años" : "No especificado");
        if (discapacidadLabel != null) discapacidadLabel.setText(trabajador.getDiscapacidad() != null ? trabajador.getDiscapacidad() : "No especificado");
        if (experienciaLabel != null) experienciaLabel.setText(trabajador.getExperienciaLaboral() != null ? trabajador.getExperienciaLaboral() : "No especificado");
        if (habilidadesLabel != null) habilidadesLabel.setText(trabajador.getHabilidades() != null ? trabajador.getHabilidades() : "No especificado");

        cargarCalificacionesTrabajador(trabajador);
    }

    private void cargarCalificacionesTrabajador(Trabajador t) {
        try {
            var em = com.example.trabajos.utils.HibernateUtil.getEntityManagerFactory().createEntityManager();
            List<Postulacion> calificadas;
            try {
                calificadas = em.createQuery(
                                "SELECT p FROM Postulacion p WHERE p.trabajador.idTrabajador = :id AND p.califEmpPromedio IS NOT NULL",
                                Postulacion.class)
                        .setParameter("id", t.getIdTrabajador())
                        .getResultList();
            } finally {
                em.close();
            }

            if (calificadas.isEmpty()) {
                if (calificacionPromedioLabel != null) calificacionPromedioLabel.setText("Sin calificaciones aún");
                if (calificacionEstrellasLabel != null) calificacionEstrellasLabel.setText("☆☆☆☆☆");
                if (calificacionTotalLabel != null) calificacionTotalLabel.setText("0 calificaciones recibidas");
                return;
            }

            double suma = calificadas.stream()
                    .mapToDouble(p -> p.getCalifEmpPromedio() != null ? p.getCalifEmpPromedio() : 0)
                    .sum();
            double promedio = suma / calificadas.size();

            String estrellas = generarEstrellas(promedio);
            if (calificacionPromedioLabel != null) calificacionPromedioLabel.setText(String.format("%.1f / 5.0", promedio));
            if (calificacionEstrellasLabel != null) calificacionEstrellasLabel.setText(estrellas);
            if (calificacionTotalLabel != null) calificacionTotalLabel.setText(calificadas.size() + " calificación(es) recibida(s)");

            if (calificacionesDetalleContainer != null) {
                calificacionesDetalleContainer.getChildren().clear();
                for (Postulacion p : calificadas) {
                    String empresa = p.getEmpresa() != null ? p.getEmpresa().getNombreEmpresa() : "Empresa";
                    String puesto = p.getOferta() != null ? p.getOferta().getPuesto_trabajo() : "Puesto";

                    HBox fila = new HBox(12);
                    fila.setStyle("-fx-background-color: #f8faff; -fx-background-radius: 8; -fx-padding: 10 14;");
                    fila.setAlignment(Pos.CENTER_LEFT);

                    Label lblEmp = new Label("🏢 " + empresa + " — " + puesto);
                    lblEmp.setStyle("-fx-text-fill: #1a3a5c; -fx-font-weight: bold; -fx-font-size: 13;");

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    Label lblProm = new Label(generarEstrellas(p.getCalifEmpPromedio()) + " " +
                            String.format("%.1f", p.getCalifEmpPromedio()));
                    lblProm.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 14;");

                    fila.getChildren().addAll(lblEmp, sp, lblProm);
                    calificacionesDetalleContainer.getChildren().add(fila);

                    if (p.getComentarioEmpresa() != null && !p.getComentarioEmpresa().isBlank()) {
                        Label lblComent = new Label("💬 " + p.getComentarioEmpresa());
                        lblComent.setStyle("-fx-text-fill: #3a5070; -fx-font-size: 12; -fx-padding: 0 14 6 14;");
                        lblComent.setWrapText(true);
                        calificacionesDetalleContainer.getChildren().add(lblComent);
                    }
                }
            }
        } catch (Exception e) {
            if (calificacionPromedioLabel != null) calificacionPromedioLabel.setText("No disponible");
        }
    }

    private String generarEstrellas(double promedio) {
        int llenas = (int) Math.round(promedio);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= llenas ? "★" : "☆");
        return sb.toString();
    }

    private Image byteArrayAImagen(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return new Image(bis);
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
        confirmacion.setContentText("⚠️ ATENCIÓN: Esta decisión NO se puede cambiar después.\n\n");

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

            // NUEVO: Si se ACEPTA al trabajador, desactivar su cuenta automáticamente
            if ("ACEPTADO".equals(nuevoEstado) && trabajador != null && trabajador.isActivo()) {
                trabajadorService.cambiarEstadoActivo(trabajador.getIdTrabajador(), false);
                trabajador.setActivo(false);
                System.out.println("⛔ Trabajador " + trabajador.getNombreCompleto() + " desactivado automáticamente por aceptación de empresa");
            }

            actualizarEstadoBotones();

            if (postulantesController != null) {
                postulantesController.refrescarTabla();
            }

            String mensaje = "";
            if ("ACEPTADO".equals(nuevoEstado)) {
                mensaje = "✅ Has ACEPTADO a " + trabajador.getNombre() +
                        " para la vacante.\n\n";
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
            if ("match".equals(origen)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/MatchTrabajadores.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) volverButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Match de Talento");
            } else if ("postulantes".equals(origen)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/DetalleFormulario.fxml"));
                Parent root = loader.load();

                if (postulacion != null && postulacion.getOferta() != null) {
                    DetalleFormularioController controller = loader.getController();
                    controller.setEsDesdeEmpresas(true);
                    controller.mostrarOferta(postulacion.getOferta());
                }

                Stage stage = (Stage) volverButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Detalle de Oferta");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) volverButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Panel de Empresas");
            }
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