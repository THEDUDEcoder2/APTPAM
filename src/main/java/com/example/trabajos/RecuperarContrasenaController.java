package com.example.trabajos;

import com.example.trabajos.services.RecuperacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RecuperarContrasenaController {

    @FXML private TextField emailField;
    @FXML private Label mensajeLabel;
    @FXML private Button enviarButton;
    @FXML private Button cancelarButton;

    private RecuperacionService recuperacionService = RecuperacionService.getInstancia();
    private String emailActual;

    @FXML
    private void onEnviarClick() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            mostrarMensaje("Por favor, ingresa tu correo electrónico.", "error");
            return;
        }

        if (!email.contains("@")) {
            mostrarMensaje("Por favor, ingresa un correo válido.", "error");
            return;
        }

        enviarButton.setDisable(true);
        enviarButton.setText("Enviando...");

        if (!recuperacionService.existeEmail(email)) {
            mostrarMensaje("No encontramos una cuenta con ese correo electrónico.", "error");
            enviarButton.setDisable(false);
            enviarButton.setText("✓ Enviar código");
            return;
        }

        emailActual = email;

        boolean enviado = recuperacionService.guardarCodigoYEnviar(email);

        if (enviado) {
            mostrarMensaje("✅ Se ha enviado un código de verificación a tu correo electrónico.", "exito");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::abrirValidarCodigo);
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(this::abrirValidarCodigo);
                }
            }).start();
        } else {
            mostrarMensaje("❌ Error al enviar el correo. Verifica tu conexión o intenta más tarde.", "error");
            enviarButton.setDisable(false);
            enviarButton.setText("✓ Enviar código");
        }
    }

    private void abrirValidarCodigo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/ValidarCodigo.fxml"));
            Parent root = loader.load();

            ValidarCodigoController controller = loader.getController();
            controller.setEmail(emailActual);

            Stage stage = new Stage();
            stage.setTitle("Verificar Código");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir la ventana de verificación.", "error");
            enviarButton.setDisable(false);
            enviarButton.setText("✓ Enviar código");
        }
    }

    @FXML
    private void onCancelarClick() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setVisible(true);

        if ("error".equals(tipo)) {
            mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
        }

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> mensajeLabel.setVisible(false));
            } catch (InterruptedException e) {}
        }).start();
    }
}