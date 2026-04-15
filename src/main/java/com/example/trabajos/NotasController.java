package com.example.trabajos;

import javafx.fxml.FXML;
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

    private Postulacion postulacionActual;
    private PostulacionService postulacionService = new PostulacionService();
    private boolean modoRespuesta = false; // true = trabajador responde a empresa, false = empresa envía nota a trabajador

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
        actualizarVista();
    }

    private void actualizarVista() {
        if (postulacionActual == null) return;

        if (modoRespuesta) {
            // TRABAJADOR → EMPRESA (respondiendo a una oferta privada)
            if (infoTrabajadorLabel != null) {
                if (postulacionActual.getOferta() != null && postulacionActual.getOferta().getEmpresa() != null) {
                    String nombreEmpresa = postulacionActual.getOferta().getEmpresa().getNombreEmpresa();
                    String puesto = postulacionActual.getOferta().getPuesto_trabajo();
                    infoTrabajadorLabel.setText("Respondiendo a: " + nombreEmpresa + " - Puesto: " + puesto);
                } else {
                    infoTrabajadorLabel.setText("Respondiendo a la empresa");
                }
            }
            if (enviarButton != null) {
                enviarButton.setText("Enviar Respuesta");
            }
            if (notaExistenteLabel != null) {
                notaExistenteLabel.setVisible(false);
            }
            if (notaTextArea != null) {
                notaTextArea.clear();
                notaTextArea.setPromptText("Escribe tu respuesta para la empresa...");
            }
        } else {
            // EMPRESA → TRABAJADOR (enviando nota a trabajador aceptado)
            if (infoTrabajadorLabel != null) {
                if (postulacionActual.getTrabajador() != null) {
                    String nombreTrabajador = postulacionActual.getTrabajador().getNombreCompleto();
                    String email = postulacionActual.getTrabajador().getCorreoElectronico() != null ?
                            postulacionActual.getTrabajador().getCorreoElectronico() : "Email no disponible";
                    infoTrabajadorLabel.setText("Trabajador: " + nombreTrabajador + " - " + email);
                } else {
                    infoTrabajadorLabel.setText("Cargando información del trabajador...");
                }
            }

            // Verificar si ya existe una nota
            if (postulacionActual.tieneNotaEmpresa()) {
                String nota = postulacionActual.getNotaEmpresa();
                // Si es nota del trabajador (respuesta), mostrarla pero no editable
                if (nota.contains("RESPUESTA DEL TRABAJADOR") || nota.contains("RAZÓN DEL TRABAJADOR")) {
                    String notaLimpia = nota.replace("📝 RESPUESTA DEL TRABAJADOR:\n", "")
                            .replace("📝 RAZÓN DEL TRABAJADOR (ACEPTADO):\n", "")
                            .replace("📝 RAZÓN DEL TRABAJADOR (RECHAZADO):\n", "");
                    if (notaTextArea != null) {
                        notaTextArea.setText(notaLimpia);
                        notaTextArea.setEditable(false);
                    }
                    if (enviarButton != null) {
                        enviarButton.setVisible(false);
                        enviarButton.setManaged(false);
                    }
                    if (notaExistenteLabel != null) {
                        notaExistenteLabel.setText("📝 Esta es la respuesta del trabajador. No puedes modificarla.");
                        notaExistenteLabel.setVisible(true);
                    }
                } else {
                    // Nota existente de la empresa - editable
                    if (notaTextArea != null) {
                        notaTextArea.setText(nota);
                        notaTextArea.setEditable(true);
                    }
                    if (notaExistenteLabel != null) {
                        notaExistenteLabel.setText("📝 Ya existe una nota. Puedes modificarla.");
                        notaExistenteLabel.setVisible(true);
                    }
                    if (enviarButton != null) {
                        enviarButton.setText("Actualizar Nota");
                        enviarButton.setVisible(true);
                        enviarButton.setManaged(true);
                    }
                }
            } else {
                if (notaTextArea != null) {
                    notaTextArea.clear();
                    notaTextArea.setEditable(true);
                    notaTextArea.setPromptText("Escribe una nota para el trabajador...");
                }
                if (notaExistenteLabel != null) {
                    notaExistenteLabel.setVisible(false);
                }
                if (enviarButton != null) {
                    enviarButton.setText("Enviar Nota");
                    enviarButton.setVisible(true);
                    enviarButton.setManaged(true);
                }
            }
        }
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
            // TRABAJADOR responde a EMPRESA
            String respuestaCompleta = "📝 RESPUESTA DEL TRABAJADOR:\n" + notaFormateada;
            postulacionService.actualizarNotaEmpresa(postulacionActual, respuestaCompleta);
            mostrarMensajeExito("✅ Respuesta enviada correctamente.\n\nLa empresa recibirá tu mensaje.");
        } else {
            // EMPRESA envía nota a TRABAJADOR
            postulacionService.actualizarNotaEmpresa(postulacionActual, notaFormateada);
            mostrarMensajeExito("✅ Nota enviada correctamente al trabajador.");
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
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}