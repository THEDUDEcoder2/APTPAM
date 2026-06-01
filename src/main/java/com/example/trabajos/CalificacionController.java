package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CalificacionController {

    @FXML private Button cancelarButton;
    @FXML private Button guardarButton;
    @FXML private javafx.scene.layout.VBox columnaEmpresa;
    @FXML private javafx.scene.layout.VBox columnaTrabajador;
    @FXML private Label empleoLabel;

    // Empresa → Trabajador
    @FXML private Button emp_pun_1, emp_pun_2, emp_pun_3, emp_pun_4, emp_pun_5;
    @FXML private Button emp_act_1, emp_act_2, emp_act_3, emp_act_4, emp_act_5;
    @FXML private Button emp_des_1, emp_des_2, emp_des_3, emp_des_4, emp_des_5;
    @FXML private Button emp_con_1, emp_con_2, emp_con_3, emp_con_4, emp_con_5;
    @FXML private Label  lblEmpPuntualidad, lblEmpActitud, lblEmpDesempeno, lblEmpConfiabilidad;
    @FXML private TextArea comentarioEmpresa;
    @FXML private Label  promedioEmpresaLabel;

    // Trabajador → Empresa
    @FXML private Button tra_tra_1, tra_tra_2, tra_tra_3, tra_tra_4, tra_tra_5;
    @FXML private Button tra_con_1, tra_con_2, tra_con_3, tra_con_4, tra_con_5;
    @FXML private Button tra_pag_1, tra_pag_2, tra_pag_3, tra_pag_4, tra_pag_5;
    @FXML private Button tra_cfi_1, tra_cfi_2, tra_cfi_3, tra_cfi_4, tra_cfi_5;
    @FXML private Label  lblTraTrato, lblTraCondiciones, lblTraPago, lblTraConfiabilidad;
    @FXML private TextArea comentarioTrabajador;
    @FXML private Label  promedioTrabajadorLabel;

    private Postulacion postulacionActual;
    private final Map<String, Integer> puntuaciones = new HashMap<>();
    private final PostulacionService postulacionService = new PostulacionService();

    public void setPostulacion(Postulacion postulacion) {
        this.postulacionActual = postulacion;
        if (postulacion != null && empleoLabel != null) {
            String trabajador = postulacion.getTrabajador() != null
                    ? postulacion.getTrabajador().getNombre() + " " +
                      (postulacion.getTrabajador().getApellidoPaterno() != null
                       ? postulacion.getTrabajador().getApellidoPaterno() : "")
                    : "—";
            String empresa  = postulacion.getEmpresa() != null ? postulacion.getEmpresa().getNombreEmpresa() : "—";
            String puesto   = postulacion.getOferta()  != null ? postulacion.getOferta().getPuesto_trabajo() : "—";
            empleoLabel.setText("Empleo: " + puesto + "  •  Empresa: " + empresa + "  •  Trabajador: " + trabajador);
        }
    }

    @FXML
    public void initialize() {
        boolean esEmpresa = SesionManager.getInstancia().esEmpresa();
        if (columnaEmpresa  != null) { columnaEmpresa.setVisible(esEmpresa);  columnaEmpresa.setManaged(esEmpresa);  }
        if (columnaTrabajador != null) { columnaTrabajador.setVisible(!esEmpresa); columnaTrabajador.setManaged(!esEmpresa); }
        actualizarPromedios();
    }

    @FXML
    private void onEstrella(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String userData = (String) btn.getUserData();
        if (userData == null) return;
        int lastUnder = userData.lastIndexOf('_');
        String grupo = userData.substring(0, lastUnder);
        int    valor = Integer.parseInt(userData.substring(lastUnder + 1));
        puntuaciones.put(grupo, valor);
        refrescarEstrellas(grupo, valor);
        actualizarEtiqueta(grupo, valor);
        actualizarPromedios();
    }

    private void refrescarEstrellas(String grupo, int valor) {
        for (int i = 1; i <= 5; i++) {
            Button b = buscarBoton(grupo + "_" + i);
            if (b == null) continue;
            b.setStyle(i <= valor
                ? "-fx-text-fill: #f59e0b; -fx-font-size: 22; -fx-background-color: transparent; -fx-cursor: hand;"
                : "-fx-text-fill: #cbd5e1; -fx-font-size: 22; -fx-background-color: transparent; -fx-cursor: hand;");
        }
    }

    private void actualizarEtiqueta(String grupo, int valor) {
        String texto = valor + " / 5 ★";
        switch (grupo) {
            case "emp_pun" -> { if (lblEmpPuntualidad   != null) lblEmpPuntualidad.setText(texto);   }
            case "emp_act" -> { if (lblEmpActitud        != null) lblEmpActitud.setText(texto);       }
            case "emp_des" -> { if (lblEmpDesempeno      != null) lblEmpDesempeno.setText(texto);     }
            case "emp_con" -> { if (lblEmpConfiabilidad  != null) lblEmpConfiabilidad.setText(texto); }
            case "tra_tra" -> { if (lblTraTrato          != null) lblTraTrato.setText(texto);         }
            case "tra_con" -> { if (lblTraCondiciones    != null) lblTraCondiciones.setText(texto);   }
            case "tra_pag" -> { if (lblTraPago           != null) lblTraPago.setText(texto);          }
            case "tra_cfi" -> { if (lblTraConfiabilidad  != null) lblTraConfiabilidad.setText(texto); }
        }
    }

    private void actualizarPromedios() {
        double promedioEmp = promedio(new int[]{
            puntuaciones.getOrDefault("emp_pun", 0), puntuaciones.getOrDefault("emp_act", 0),
            puntuaciones.getOrDefault("emp_des", 0), puntuaciones.getOrDefault("emp_con", 0)});
        if (promedioEmpresaLabel != null)
            promedioEmpresaLabel.setText(promedioEmp > 0 ? String.format("%.1f", promedioEmp) : "—");

        double promedioTra = promedio(new int[]{
            puntuaciones.getOrDefault("tra_tra", 0), puntuaciones.getOrDefault("tra_con", 0),
            puntuaciones.getOrDefault("tra_pag", 0), puntuaciones.getOrDefault("tra_cfi", 0)});
        if (promedioTrabajadorLabel != null)
            promedioTrabajadorLabel.setText(promedioTra > 0 ? String.format("%.1f", promedioTra) : "—");
    }

    private double promedio(int[] vals) {
        int suma = 0, count = 0;
        for (int v : vals) { if (v > 0) { suma += v; count++; } }
        return count > 0 ? (double) suma / count : 0;
    }

    private Button buscarBoton(String fxId) {
        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField(fxId);
            f.setAccessible(true);
            return (Button) f.get(this);
        } catch (Exception e) { return null; }
    }

    @FXML
    private void onGuardar() {
        if (postulacionActual == null) { mostrarError("No hay postulación seleccionada."); return; }

        boolean esEmpresa = SesionManager.getInstancia().esEmpresa();

        if (esEmpresa) {
            // Validar que puso al menos una estrella
            double prom = promedio(new int[]{
                puntuaciones.getOrDefault("emp_pun", 0), puntuaciones.getOrDefault("emp_act", 0),
                puntuaciones.getOrDefault("emp_des", 0), puntuaciones.getOrDefault("emp_con", 0)});
            if (prom == 0) { mostrarError("Por favor asigna al menos una calificación."); return; }
            postulacionActual.setCalifEmpPuntualidad((double) puntuaciones.getOrDefault("emp_pun", 0));
            postulacionActual.setCalifEmpActitud((double) puntuaciones.getOrDefault("emp_act", 0));
            postulacionActual.setCalifEmpDesempeno((double) puntuaciones.getOrDefault("emp_des", 0));
            postulacionActual.setCalifEmpConfiabilidad((double) puntuaciones.getOrDefault("emp_con", 0));
            postulacionActual.setCalifEmpPromedio(prom);
            postulacionActual.setComentarioEmpresa(comentarioEmpresa != null ? comentarioEmpresa.getText() : "");
            postulacionActual.setEmpresaCalifico(true);
        } else {
            double prom = promedio(new int[]{
                puntuaciones.getOrDefault("tra_tra", 0), puntuaciones.getOrDefault("tra_con", 0),
                puntuaciones.getOrDefault("tra_pag", 0), puntuaciones.getOrDefault("tra_cfi", 0)});
            if (prom == 0) { mostrarError("Por favor asigna al menos una calificación."); return; }
            postulacionActual.setCalifTraTrato((double) puntuaciones.getOrDefault("tra_tra", 0));
            postulacionActual.setCalifTraCondiciones((double) puntuaciones.getOrDefault("tra_con", 0));
            postulacionActual.setCalifTraPago((double) puntuaciones.getOrDefault("tra_pag", 0));
            postulacionActual.setCalifTraConfiabilidad((double) puntuaciones.getOrDefault("tra_cfi", 0));
            postulacionActual.setCalifTraPromedio(prom);
            postulacionActual.setComentarioTrabajador(comentarioTrabajador != null ? comentarioTrabajador.getText() : "");
            postulacionActual.setTrabajadorCalifico(true);
        }

        try {
            postulacionService.actualizarPostulacion(postulacionActual);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Calificación guardada!");
            alert.setHeaderText(null);
            alert.setContentText("La calificación se guardó correctamente y ya se refleja en el perfil.");
            alert.showAndWait();
        } catch (Exception e) {
            mostrarError("No se pudo guardar en BD: " + e.getMessage() + "\n(Los datos se perdieron al cerrar la app)");
        }

        volverAlPanel();
    }

    @FXML private void onCancelar() { volverAlPanel(); }

    private void volverAlPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/ListaCalificacion.fxml"));
            Parent root = loader.load();
            Button ref = cancelarButton != null ? cancelarButton : guardarButton;
            Stage stage = (Stage) ref.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Calificaciones");
        } catch (IOException e) { mostrarError("No se pudo navegar: " + e.getMessage()); }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error"); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }
}
