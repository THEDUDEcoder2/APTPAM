package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.TrabajadorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class PerfilTrabajadorController {

    @FXML private Button volverButton;

    // Datos personales
    @FXML private ImageView fotoImageView;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblEmail;
    @FXML private Label lblTelefono;
    @FXML private Label lblFechaNacimiento;
    @FXML private Label lblGenero;
    @FXML private Label lblEstadoCivil;
    @FXML private Label lblNacionalidad;
    @FXML private Label lblCurp;
    @FXML private Label lblRfc;

    // Datos de trabajo
    @FXML private Label lblEspecialidad;
    @FXML private Label lblNivelEstudio;
    @FXML private Label lblAnosExperiencia;
    @FXML private Label lblHabilidades;
    @FXML private Label lblHerramientas;
    @FXML private Label lblExperienciaLaboral;
    @FXML private Label lblDiscapacidad;

    // Ubicación
    @FXML private Label lblDomicilio;
    @FXML private Label lblMunicipio;
    @FXML private Label lblCiudad;
    @FXML private Label lblCodigoPostal;

    // Calificación
    @FXML private Label lblPromedioCalificacion;
    @FXML private Label lblEstrellas;
    @FXML private Label lblTotalCalificaciones;
    @FXML private VBox calificacionesContainer;

    private final TrabajadorService trabajadorService = new TrabajadorService();
    private final PostulacionService postulacionService = new PostulacionService();

    @FXML
    public void initialize() {
        try {
            Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
            if (usuario == null) return;

            Trabajador t = trabajadorService.obtenerTrabajadorPorEmail(usuario.getEmail());
            if (t == null) return;

            // Foto
            if (fotoImageView != null && t.getFotoPerfil() != null && t.getFotoPerfil().length > 0) {
                try {
                    fotoImageView.setImage(new Image(new ByteArrayInputStream(t.getFotoPerfil())));
                } catch (Exception ignored) {}
            }

            // Datos personales
            setText(lblNombreCompleto, t.getNombre()
                    + (t.getApellidoPaterno() != null ? " " + t.getApellidoPaterno() : "")
                    + (t.getApellidoMaterno() != null ? " " + t.getApellidoMaterno() : ""));
            setText(lblEmail, t.getCorreoElectronico());
            setText(lblTelefono, t.getNumTelefono());
            setText(lblFechaNacimiento, t.getFechaNacimiento() != null ? t.getFechaNacimiento().toString() : null);
            setText(lblGenero, t.getGenero() != null ? t.getGenero().getTipoGenero() : null);
            setText(lblEstadoCivil, t.getEstadoCivil() != null ? t.getEstadoCivil().getEstadoCivil() : null);
            setText(lblNacionalidad, t.getNacionalidad() != null ? t.getNacionalidad().getNombreNacionalidad() : null);
            setText(lblCurp, t.getCurp());
            setText(lblRfc, t.getRfc());

            // Datos laborales
            setText(lblEspecialidad, t.getEspecialidad());
            setText(lblNivelEstudio, t.getNivelEstudio());
            setText(lblAnosExperiencia, t.getAnosExperiencia() != null ? t.getAnosExperiencia() + " años" : null);
            setText(lblHabilidades, t.getHabilidades());
            setText(lblHerramientas, t.getConocimientosHerramientas());
            setText(lblExperienciaLaboral, t.getExperienciaLaboral());
            setText(lblDiscapacidad, t.getDiscapacidad());

            // Ubicación
            setText(lblDomicilio, (t.getCalle() != null ? t.getCalle() : "") + (t.getColonia() != null ? ", " + t.getColonia() : ""));
            setText(lblMunicipio, t.getMunicipio() != null ? t.getMunicipio().getNombreMunicipio() : null);
            setText(lblCiudad, t.getCiudad() != null ? t.getCiudad().getNombreCiudad() : null);
            setText(lblCodigoPostal, t.getCodigoPostal());

            // Calificaciones recibidas (empresa → este trabajador)
            cargarCalificaciones(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarCalificaciones(Trabajador t) {
        try {
            // Obtener postulaciones donde este trabajador fue calificado por alguna empresa
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
                setText(lblPromedioCalificacion, "Sin calificaciones aún");
                setText(lblEstrellas, "☆☆☆☆☆");
                setText(lblTotalCalificaciones, "0 calificaciones recibidas");
                return;
            }

            double suma = calificadas.stream()
                    .mapToDouble(p -> p.getCalifEmpPromedio() != null ? p.getCalifEmpPromedio() : 0)
                    .sum();
            double promedio = suma / calificadas.size();

            setText(lblPromedioCalificacion, String.format("%.1f / 5.0", promedio));
            setText(lblEstrellas, generarEstrellas(promedio));
            setText(lblTotalCalificaciones, calificadas.size() + " calificación(es) recibida(s)");

            // Desglose de cada calificación
            if (calificacionesContainer != null) {
                calificacionesContainer.getChildren().clear();
                for (Postulacion p : calificadas) {
                    String empresa = p.getEmpresa() != null ? p.getEmpresa().getNombreEmpresa() : "Empresa";
                    String puesto  = p.getOferta()  != null ? p.getOferta().getPuesto_trabajo()  : "Puesto";

                    javafx.scene.layout.HBox fila = new javafx.scene.layout.HBox(12);
                    fila.setStyle("-fx-background-color: #f8faff; -fx-background-radius: 8; -fx-padding: 10 14;");
                    fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    Label lblEmp   = new Label("🏢 " + empresa + " — " + puesto);
                    lblEmp.setStyle("-fx-text-fill: #1a3a5c; -fx-font-weight: bold; -fx-font-size: 13;");

                    javafx.scene.layout.Region sp = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(sp, javafx.scene.layout.Priority.ALWAYS);

                    Label lblProm = new Label(generarEstrellas(p.getCalifEmpPromedio()) + " " +
                            String.format("%.1f", p.getCalifEmpPromedio()));
                    lblProm.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 14;");

                    fila.getChildren().addAll(lblEmp, sp, lblProm);
                    calificacionesContainer.getChildren().add(fila);

                    // Comentario
                    if (p.getComentarioEmpresa() != null && !p.getComentarioEmpresa().isBlank()) {
                        Label lblComent = new Label("💬 " + p.getComentarioEmpresa());
                        lblComent.setStyle("-fx-text-fill: #3a5070; -fx-font-size: 12; -fx-padding: 0 14 6 14;");
                        lblComent.setWrapText(true);
                        calificacionesContainer.getChildren().add(lblComent);
                    }
                }
            }
        } catch (Exception e) {
            setText(lblPromedioCalificacion, "No disponible");
            setText(lblEstrellas, "—");
        }
    }

    private String generarEstrellas(double promedio) {
        int llenas = (int) Math.round(promedio);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= llenas ? "★" : "☆");
        return sb.toString();
    }

    private void setText(Label lbl, String val) {
        if (lbl != null) lbl.setText(val != null && !val.isBlank() ? val : "—");
    }

    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Trabajos.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Buscar Trabajos");
        } catch (IOException e) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo volver: " + e.getMessage()).showAndWait();
        }
    }
}
