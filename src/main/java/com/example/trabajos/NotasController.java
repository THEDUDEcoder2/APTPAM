package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;

public class NotasController {

    @FXML
    private Label infoTrabajadorLabel;

    @FXML
    private TextArea notaTextArea;

    @FXML
    private Label notaExistenteLabel;

    @FXML
    private Label mensajeLabel;

    @FXML
    private Button enviarButton;

    @FXML
    private Button cancelarButton;

    @FXML
    private Button aceptarButton;

    @FXML
    private Button rechazarButton;

    private Postulacion postulacionActual;
    private PostulacionService postulacionService = new PostulacionService();
    private boolean modoRespuesta = false;

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacionActual = postulacion;
        actualizarVista();
    }

    public void setModoRespuesta(boolean modo) {
        this.modoRespuesta = modo;
    }

    private void actualizarVista() {
        if (postulacionActual != null) {
            if (modoRespuesta) {
                // Modo respuesta del trabajador - mostrar botones Aceptar/Rechazar
                String info = String.format(
                        "Responder a oferta de: %s\nPuesto: %s\nEstado actual: %s\n\nEscribe tu respuesta (opcional):",
                        postulacionActual.getOferta().getEmpresa().getNombreEmpresa(),
                        postulacionActual.getOferta().getPuesto_trabajo(),
                        postulacionActual.getEstadoFormateado()
                );
                infoTrabajadorLabel.setText(info);

                // Mostrar botones de aceptar/rechazar
                if (aceptarButton != null) aceptarButton.setVisible(true);
                if (rechazarButton != null) rechazarButton.setVisible(true);
                if (enviarButton != null) enviarButton.setVisible(false);
                if (notaExistenteLabel != null) notaExistenteLabel.setVisible(false);
            } else {
                // Modo empresa - solo nota
                String info = String.format(
                        "Trabajador: %s\nVacante: %s - %s\nEstado actual: %s",
                        postulacionActual.getTrabajador().getNombreCompleto(),
                        postulacionActual.getOferta().getPuesto_trabajo(),
                        postulacionActual.getOferta().getEmpresa().getNombreEmpresa(),
                        postulacionActual.getEstadoFormateado()
                );
                infoTrabajadorLabel.setText(info);

                // Ocultar botones de aceptar/rechazar
                if (aceptarButton != null) aceptarButton.setVisible(false);
                if (rechazarButton != null) rechazarButton.setVisible(false);
                if (enviarButton != null) enviarButton.setVisible(true);

                if (postulacionActual.tieneNotaEmpresa()) {
                    notaTextArea.setText(postulacionActual.getNotaEmpresa());
                    notaExistenteLabel.setText("📝 Ya existe una nota. Puedes modificarla.");
                    notaExistenteLabel.setVisible(true);
                    enviarButton.setText("Actualizar Nota");
                } else {
                    notaTextArea.clear();
                    notaExistenteLabel.setVisible(false);
                    enviarButton.setText("Enviar Nota");
                }
            }
        }
    }

    @FXML
    protected void onAceptarClick() {
        if (postulacionActual == null) {
            mostrarError("Error: No hay postulación seleccionada.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacionActual.getEstado())) {
            mostrarError("Ya has respondido a esta oferta. No puedes cambiar tu respuesta.");
            return;
        }

        String respuesta = notaTextArea.getText().trim();
        String respuestaFormateada = respuesta.isEmpty() ?
                "El trabajador ha ACEPTADO la oferta." :
                "El trabajador ha ACEPTADO la oferta.\n\nMensaje:\n" + formatearPrimeraLetraMayuscula(respuesta);

        // Actualizar estado de la postulación
        postulacionActual.setEstado("ACEPTADO");
        postulacionService.actualizarPostulacion(postulacionActual);

        // Guardar la nota con la respuesta
        postulacionService.actualizarNotaEmpresa(postulacionActual, respuestaFormateada);

        mostrarMensajeExito("✅ Has ACEPTADO la oferta.\n\nTu respuesta ha sido enviada a la empresa.");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                javafx.application.Platform.runLater(this::cerrarVentana);
            } catch (Exception e) {
                javafx.application.Platform.runLater(this::cerrarVentana);
            }
        }).start();
    }

    @FXML
    protected void onRechazarClick() {
        if (postulacionActual == null) {
            mostrarError("Error: No hay postulación seleccionada.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(postulacionActual.getEstado())) {
            mostrarError("Ya has respondido a esta oferta. No puedes cambiar tu respuesta.");
            return;
        }

        String respuesta = notaTextArea.getText().trim();
        String respuestaFormateada = respuesta.isEmpty() ?
                "El trabajador ha RECHAZADO la oferta." :
                "El trabajador ha RECHAZADO la oferta.\n\nMensaje:\n" + formatearPrimeraLetraMayuscula(respuesta);

        // Actualizar estado de la postulación
        postulacionActual.setEstado("RECHAZADO");
        postulacionService.actualizarPostulacion(postulacionActual);

        // Guardar la nota con la respuesta
        postulacionService.actualizarNotaEmpresa(postulacionActual, respuestaFormateada);

        mostrarMensajeExito("❌ Has RECHAZADO la oferta.\n\nTu respuesta ha sido enviada a la empresa.");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                javafx.application.Platform.runLater(this::cerrarVentana);
            } catch (Exception e) {
                javafx.application.Platform.runLater(this::cerrarVentana);
            }
        }).start();
    }

    @FXML
    protected void onEnviarClick() {
        if (postulacionActual == null) {
            mostrarError("Error: No hay postulación seleccionada.");
            return;
        }

        String nota = notaTextArea.getText().trim();

        if (nota.isEmpty()) {
            mostrarError("Por favor, escribe un mensaje.");
            return;
        }

        String notaFormateada = formatearPrimeraLetraMayuscula(nota);

        if (modoRespuesta) {
            // Modo trabajador - esto ya no debería ocurrir porque los botones están ocultos
            String respuestaCompleta = "RESPUESTA DEL TRABAJADOR:\n" + notaFormateada;
            postulacionService.actualizarNotaEmpresa(postulacionActual, respuestaCompleta);
            mostrarMensajeExito("✅ Respuesta enviada correctamente.");
        } else {
            // Modo empresa - guardar nota normal
            postulacionService.actualizarNotaEmpresa(postulacionActual, notaFormateada);
            mostrarMensajeExito("✅ Nota guardada correctamente.");
        }

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                javafx.application.Platform.runLater(this::cerrarVentana);
            } catch (Exception e) {
                javafx.application.Platform.runLater(this::cerrarVentana);
            }
        }).start();
    }

    @FXML
    protected void onCancelarClick() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) notaTextArea.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensajeExito(String mensaje) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}