package com.example.trabajos;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Postulacion;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.PostulacionService;
import com.example.trabajos.services.TrabajadorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListaCalificacionController {

    @FXML private Button volverButton;
    @FXML private VBox listaContainer;
    @FXML private Label tituloLabel;
    @FXML private Label subtituloLabel;

    private final PostulacionService postulacionService = new PostulacionService();
    private final TrabajadorService trabajadorService = new TrabajadorService();
    private final EmpresaService empresaService = new EmpresaService();

    @FXML
    public void initialize() {
        boolean esEmpresa = SesionManager.getInstancia().esEmpresa();
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();

        if (esEmpresa) {
            tituloLabel.setText("Trabajadores por calificar");
            subtituloLabel.setText("Selecciona al trabajador cuyo empleo finalizó para calificarlo");
            cargarListaParaEmpresa(usuario);
        } else {
            tituloLabel.setText("Empresas por calificar");
            subtituloLabel.setText("Selecciona la empresa donde trabajaste para calificarla");
            cargarListaParaTrabajador(usuario);
        }
    }

    // ── Empresa: ve lista de trabajadores con empleo terminado ──────────────
    private void cargarListaParaEmpresa(Usuario usuario) {
        listaContainer.getChildren().clear();

        try {
            Empresa empresa = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
            if (empresa == null) {
                mostrarVacio("No se encontró la empresa en el sistema.");
                return;
            }

            List<Postulacion> postulaciones = postulacionService.obtenerPostulacionesPorEmpresaId(empresa.getIdEmpresa());

            // Filtrar solo ACEPTADO (empleo concretado) — pendiente de un estado "TERMINADO"
            // Por ahora usamos ACEPTADO como proxy de empleo finalizado
            List<Postulacion> candidatos = postulaciones.stream()
                    .filter(p -> "ACEPTADO".equalsIgnoreCase(p.getEstado()))
                    .toList();

            if (candidatos.isEmpty()) {
                mostrarVacio("Nadie por calificar por el momento.");
                return;
            }

            for (Postulacion p : candidatos) {
                Trabajador t = p.getTrabajador();
                if (t == null) continue;

                boolean yaCalificado = Boolean.TRUE.equals(p.getEmpresaCalifico());

                String nombre = t.getNombre()
                        + (t.getApellidoPaterno() != null ? " " + t.getApellidoPaterno() : "");
                String puesto = p.getOferta() != null ? p.getOferta().getPuesto_trabajo() : "Empleo";
                String info   = "Puesto: " + puesto + (yaCalificado ? "  •  ✅ Ya calificado" : "");

                HBox fila = crearFila("👷 " + nombre, info, yaCalificado ? "✅ Ya calificado" : "Calificar →", () -> {
                    if (yaCalificado) return;
                    abrirCalificacion(p);
                }, yaCalificado);
                listaContainer.getChildren().add(fila);
            }

        } catch (Exception e) {
            mostrarVacio("Error al cargar la lista: " + e.getMessage());
        }
    }

    // ── Trabajador: ve lista de empresas donde trabajó ──────────────────────
    private void cargarListaParaTrabajador(Usuario usuario) {
        listaContainer.getChildren().clear();

        try {
            Trabajador trabajador = trabajadorService.obtenerTrabajadorPorEmail(usuario.getEmail());
            if (trabajador == null) {
                mostrarVacio("No se encontró tu perfil de trabajador.");
                return;
            }

            // Obtener todas las postulaciones aceptadas del trabajador a través de sus ofertas
            // Consultamos con un query genérico usando PostulacionService
            // Reutilizamos: buscamos por empresa de cada oferta disponible
            // Como no hay query directo por trabajador, filtramos manualmente
            List<Postulacion> todas = obtenerPostulacionesPorTrabajador(trabajador);

            List<Postulacion> candidatos = todas.stream()
                    .filter(p -> "ACEPTADO".equalsIgnoreCase(p.getEstado()))
                    .toList();

            if (candidatos.isEmpty()) {
                mostrarVacio("Nadie por calificar por el momento.");
                return;
            }

            for (Postulacion p : candidatos) {
                Empresa emp = p.getEmpresa();
                if (emp == null) continue;

                boolean yaCalificado = Boolean.TRUE.equals(p.getTrabajadorCalifico());

                String nombreEmpresa = emp.getNombreEmpresa() != null ? emp.getNombreEmpresa() : "Empresa";
                String puesto = p.getOferta() != null ? p.getOferta().getPuesto_trabajo() : "Empleo";
                String info   = "Puesto: " + puesto + (yaCalificado ? "  •  ✅ Ya calificado" : "");

                HBox fila = crearFila("🏢 " + nombreEmpresa, info, yaCalificado ? "✅ Ya calificado" : "Calificar →", () -> {
                    if (yaCalificado) return;
                    abrirCalificacion(p);
                }, yaCalificado);
                listaContainer.getChildren().add(fila);
            }

        } catch (Exception e) {
            mostrarVacio("Error al cargar la lista: " + e.getMessage());
        }
    }

    /** Obtiene postulaciones de un trabajador a partir de sus empleos */
    private List<Postulacion> obtenerPostulacionesPorTrabajador(Trabajador trabajador) {
        try {
            // Usamos el EntityManager directamente para buscar por trabajador
            com.example.trabajos.utils.HibernateUtil.getEntityManagerFactory();
            jakarta.persistence.EntityManager em =
                    com.example.trabajos.utils.HibernateUtil.getEntityManagerFactory().createEntityManager();
            try {
                return em.createQuery(
                                "SELECT p FROM Postulacion p WHERE p.trabajador.idTrabajador = :idTrabajador",
                                Postulacion.class)
                        .setParameter("idTrabajador", trabajador.getIdTrabajador())
                        .getResultList();
            } finally {
                em.close();
            }
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    // ── Navegar a Calificacion.fxml pasando la postulacion seleccionada ─────
    private void abrirCalificacion(Postulacion postulacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Calificacion.fxml"));
            Parent root = loader.load();

            CalificacionController controller = loader.getController();
            controller.setPostulacion(postulacion);

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Calificación");
        } catch (IOException e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir la calificación: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ── Helpers UI ──────────────────────────────────────────────────────────
    private HBox crearFila(String titulo, String info, String botonTexto, Runnable accion, boolean deshabilitado) {
        HBox fila = new HBox(16);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(18, 24, 18, 24));
        fila.setStyle(
            "-fx-background-color: " + (deshabilitado ? "#f0f0f0" : "white") + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 2);" +
            "-fx-cursor: " + (deshabilitado ? "default" : "hand") + ";"
        );

        VBox textos = new VBox(4);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: " + (deshabilitado ? "#94a3b8" : "#1a3a5c") + "; -fx-font-size: 16; -fx-font-weight: bold;");
        Label lblInfo = new Label(info);
        lblInfo.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        textos.getChildren().addAll(lblTitulo, lblInfo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btn = new Button(botonTexto);
        if (deshabilitado) {
            btn.setStyle(
                "-fx-background-color: #d1d5db;" +
                "-fx-text-fill: #6b7280;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: default;"
            );
            btn.setDisable(true);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #3a7bd5, #1a3a5c);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> accion.run());

            fila.setOnMouseEntered(e -> fila.setStyle(
                "-fx-background-color: #f0f7ff;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(58,123,213,0.20), 12, 0, 0, 3);" +
                "-fx-cursor: hand;"
            ));
            fila.setOnMouseExited(e -> fila.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 2);" +
                "-fx-cursor: hand;"
            ));
        }

        fila.getChildren().addAll(textos, spacer, btn);
        return fila;
    }

    private void mostrarVacio(String mensaje) {
        VBox vacio = new VBox(16);
        vacio.setAlignment(Pos.CENTER);
        vacio.setPadding(new Insets(60));

        Label icono = new Label("📋");
        icono.setStyle("-fx-font-size: 48;");

        Label lbl = new Label(mensaje);
        lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16;");
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);

        vacio.getChildren().addAll(icono, lbl);
        listaContainer.getChildren().add(vacio);
    }

    @FXML
    private void onVolver() {
        try {
            boolean esEmpresa = SesionManager.getInstancia().esEmpresa();
            String fxml   = esEmpresa ? "/com/example/trabajos/Empresas.fxml" : "/com/example/trabajos/Trabajos.fxml";
            String titulo = esEmpresa ? "Panel de Empresas" : "Buscar Trabajos";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);
        } catch (IOException e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo volver al panel: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
