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

public class ValidarCodigoController {

    @FXML private Label emailLabel;
    @FXML private TextField codigoField;
    @FXML private Label mensajeLabel;
    @FXML private Button verificarButton;
    @FXML private Button reenviarButton;

    private RecuperacionService recuperacionService = RecuperacionService.getInstancia();
    private String email;

    public void setEmail(String email) {
        this.email = email;
        emailLabel.setText(email);
    }

    @FXML
    private void onVerificarClick() {
        String codigo = codigoField.getText().trim();

        if (codigo.isEmpty()) {
            mostrarMensaje("Por favor, ingresa el código de verificación.", "error");
            return;
        }

        if (!codigo.matches("\\d{6}")) {
            mostrarMensaje("El código debe tener 6 dígitos.", "error");
            return;
        }

        if (recuperacionService.verificarCodigo(email, codigo)) {
            mostrarMensaje("✅ Código verificado correctamente.", "exito");
            abrirNuevaContrasena();
        } else {
            mostrarMensaje("❌ Código incorrecto o expirado. Solicita un nuevo código.", "error");
        }
    }

    @FXML
    private void onReenviarClick() {
        reenviarButton.setDisable(true);
        reenviarButton.setText("Enviando...");

        boolean reenviado = recuperacionService.reenviarCodigo(email);

        if (reenviado) {
            mostrarMensaje("✅ Se ha reenviado un nuevo código a tu correo electrónico.", "exito");
        } else {
            mostrarMensaje("❌ Error al reenviar el código. Intenta nuevamente.", "error");
        }

        reenviarButton.setDisable(false);
        reenviarButton.setText("⟳ Reenviar código");
    }

    private void abrirNuevaContrasena() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/NuevaContrasena.fxml"));
            Parent root = loader.load();

            NuevaContrasenaController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = new Stage();
            stage.setTitle("Nueva Contraseña");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) codigoField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir la ventana de nueva contraseña.", "error");
        }
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