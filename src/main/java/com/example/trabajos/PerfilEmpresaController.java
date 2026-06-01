package com.example.trabajos;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.EmpresaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class PerfilEmpresaController {

    @FXML private Button volverButton;

    // Datos empresa
    @FXML private Label lblNombreEmpresa;
    @FXML private Label lblRazonSocial;
    @FXML private Label lblRfc;
    @FXML private Label lblTipoEmpresa;
    @FXML private Label lblActEconomica;
    @FXML private Label lblEmail;
    @FXML private Label lblTelefono;
    @FXML private Label lblDomicilio;
    @FXML private Label lblMunicipio;
    @FXML private Label lblCiudad;
    @FXML private Label lblCodigoPostal;
    @FXML private Label lblSector;

    // Calificaciones
    @FXML private Label lblPromedioCalificacion;
    @FXML private Label lblEstrellas;
    @FXML private Label lblTotalCalificaciones;
    @FXML private VBox calificacionesContainer;

    private final EmpresaService empresaService = new EmpresaService();

    @FXML
    public void initialize() {
        try {
            Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
            if (usuario == null) return;

            Empresa emp = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
            if (emp == null) return;

            setText(lblNombreEmpresa, emp.getNombreEmpresa());
            setText(lblRazonSocial, emp.getRazonSocial());
            setText(lblRfc, emp.getRfc());
            setText(lblTipoEmpresa, emp.getTipoEmpresa());
            setText(lblActEconomica, emp.getActEconomicaPrincipal());
            setText(lblEmail, emp.getCorreoElectronico());
            setText(lblTelefono, emp.getNumTelefono());
            setText(lblDomicilio, (emp.getCalle() != null ? emp.getCalle() : "") +
                    (emp.getColonia() != null ? ", " + emp.getColonia() : ""));
            setText(lblMunicipio, emp.getMunicipio() != null ? emp.getMunicipio().getNombreMunicipio() : null);
            setText(lblCiudad, emp.getCiudad() != null ? emp.getCiudad().getNombreCiudad() : null);
            setText(lblCodigoPostal, emp.getCodigoPostal());
            setText(lblSector, emp.getSectorActividad() != null ? emp.getSectorActividad().getTipoSectorActividad() : null);

            cargarCalificaciones(emp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarCalificaciones(Empresa emp) {
        try {
            var em = com.example.trabajos.utils.HibernateUtil.getEntityManagerFactory().createEntityManager();
            List<Postulacion> calificadas;
            try {
                calificadas = em.createQuery(
                        "SELECT p FROM Postulacion p WHERE p.empresa.idEmpresa = :id AND p.califTraPromedio IS NOT NULL",
                        Postulacion.class)
                    .setParameter("id", emp.getIdEmpresa())
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
                    .mapToDouble(p -> p.getCalifTraPromedio() != null ? p.getCalifTraPromedio() : 0)
                    .sum();
            double promedio = suma / calificadas.size();

            setText(lblPromedioCalificacion, String.format("%.1f / 5.0", promedio));
            setText(lblEstrellas, generarEstrellas(promedio));
            setText(lblTotalCalificaciones, calificadas.size() + " calificación(es) recibida(s)");

            if (calificacionesContainer != null) {
                calificacionesContainer.getChildren().clear();
                for (Postulacion p : calificadas) {
                    String trabajador = p.getTrabajador() != null
                            ? p.getTrabajador().getNombre() + " " +
                              (p.getTrabajador().getApellidoPaterno() != null ? p.getTrabajador().getApellidoPaterno() : "")
                            : "Trabajador";
                    String puesto = p.getOferta() != null ? p.getOferta().getPuesto_trabajo() : "Puesto";

                    HBox fila = new HBox(12);
                    fila.setStyle("-fx-background-color: #f8faff; -fx-background-radius: 8; -fx-padding: 10 14;");
                    fila.setAlignment(Pos.CENTER_LEFT);

                    Label lblTrab = new Label("👷 " + trabajador + " — " + puesto);
                    lblTrab.setStyle("-fx-text-fill: #1a3a5c; -fx-font-weight: bold; -fx-font-size: 13;");

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    Label lblProm = new Label(generarEstrellas(p.getCalifTraPromedio()) + " " +
                            String.format("%.1f", p.getCalifTraPromedio()));
                    lblProm.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 14;");

                    fila.getChildren().addAll(lblTrab, sp, lblProm);
                    calificacionesContainer.getChildren().add(fila);

                    if (p.getComentarioTrabajador() != null && !p.getComentarioTrabajador().isBlank()) {
                        Label lblComent = new Label("💬 " + p.getComentarioTrabajador());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");
        } catch (IOException e) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo volver: " + e.getMessage()).showAndWait();
        }
    }
}
