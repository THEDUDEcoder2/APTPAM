package com.example.trabajos;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class TomarFotoController {

    @FXML private ImageView imageViewWebcam;
    @FXML private Button btnCapturar;
    @FXML private Button btnCancelar;
    @FXML private Label lblMensaje;

    private Webcam webcam;
    private Thread webcamThread;
    private volatile boolean running = false;
    private Consumer<Image> callback;

    public void setCallback(Consumer<Image> callback) {
        this.callback = callback;
    }

    @FXML
    public void initialize() {
        iniciarCamara();
    }

    private void iniciarCamara() {
        try {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                mostrarError("No se encontró ninguna cámara en el sistema.");
                return;
            }
            webcam.open();

            running = true;
            webcamThread = new Thread(() -> {
                while (running) {
                    try {
                        BufferedImage image = webcam.getImage();
                        Platform.runLater(() -> {
                            if (image != null) {
                                Image fxImage = SwingFXUtils.toFXImage(image, null);
                                imageViewWebcam.setImage(fxImage);
                            }
                        });
                        Thread.sleep(30);
                    } catch (Exception e) { }
                }
            });
            webcamThread.setDaemon(true);
            webcamThread.start();

        } catch (Exception e) {
            mostrarError("Error al iniciar la cámara: " + e.getMessage());
        }
    }

    @FXML
    private void onCapturarFoto() {
        if (webcam != null && webcam.isOpen()) {
            BufferedImage imagenActual = webcam.getImage();
            if (imagenActual != null) {
                Image foto = SwingFXUtils.toFXImage(imagenActual, null);
                if (callback != null) {
                    callback.accept(foto);
                }
                lblMensaje.setText("✅ Foto capturada exitosamente.");
                lblMensaje.setStyle("-fx-text-fill: #27ae60;");
                lblMensaje.setVisible(true);

                new Thread(() -> {
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    Platform.runLater(this::cerrarVentana);
                }).start();
            }
        }
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        detenerCamara();
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void detenerCamara() {
        running = false;
        if (webcamThread != null) {
            webcamThread.interrupt();
        }
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);
        btnCapturar.setDisable(true);
    }
}