package com.example.trabajos;

import com.example.trabajos.services.RecuperacionService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NuevaContrasenaController {

    @FXML private PasswordField nuevaPasswordField;
    @FXML private TextField nuevaPasswordVisibleField;
    @FXML private PasswordField confirmarPasswordField;
    @FXML private CheckBox mostrarPasswordCheckBox;
    @FXML private Label mensajeLabel;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    private RecuperacionService recuperacionService = RecuperacionService.getInstancia();
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    public void initialize() {
        if (nuevaPasswordVisibleField != null && nuevaPasswordField != null) {
            nuevaPasswordVisibleField.textProperty().bindBidirectional(nuevaPasswordField.textProperty());
        }
    }

    @FXML
    private void onGuardarClick() {
        String nuevaPassword = nuevaPasswordField.getText();
        String confirmarPassword = confirmarPasswordField.getText();

        if (nuevaPassword.isEmpty()) {
            mostrarMensaje("Por favor, ingresa una nueva contraseña.", "error");
            return;
        }

        if (nuevaPassword.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres.", "error");
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            mostrarMensaje("Las contraseñas no coinciden.", "error");
            return;
        }

        if (recuperacionService.actualizarContrasena(email, nuevaPassword)) {
            mostrarMensaje("✅ Contraseña actualizada correctamente.", "exito");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) guardarButton.getScene().getWindow();
                        stage.close();
                    });
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) guardarButton.getScene().getWindow();
                        stage.close();
                    });
                }
            }).start();
        } else {
            mostrarMensaje("Error al actualizar la contraseña. Intenta nuevamente.", "error");
        }
    }

    @FXML
    private void onMostrarPasswordChanged() {
        if (mostrarPasswordCheckBox.isSelected()) {
            nuevaPasswordVisibleField.setText(nuevaPasswordField.getText());
            nuevaPasswordVisibleField.setVisible(true);
            nuevaPasswordVisibleField.setManaged(true);
            nuevaPasswordField.setVisible(false);
            nuevaPasswordField.setManaged(false);
        } else {
            nuevaPasswordField.setText(nuevaPasswordVisibleField.getText());
            nuevaPasswordField.setVisible(true);
            nuevaPasswordField.setManaged(true);
            nuevaPasswordVisibleField.setVisible(false);
            nuevaPasswordVisibleField.setManaged(false);
        }
    }

    @FXML
    private void onCancelarClick() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
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