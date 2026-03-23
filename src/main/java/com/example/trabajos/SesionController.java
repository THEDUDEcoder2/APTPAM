package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.TrabajadorService;

public class SesionController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private CheckBox mostrarPasswordCheckBox;

    @FXML
    private Label mensajeLabel;

    @FXML
    private Label tipoUsuarioLabel;

    private EmpresaService empresaService = new EmpresaService();
    private TrabajadorService trabajadorService = new TrabajadorService();
    private boolean esEmpresa;

    @FXML
    public void initialize() {
        if (passwordVisibleField != null && passwordField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }
    }

    public void setTipoUsuario(boolean esEmpresa) {
        this.esEmpresa = esEmpresa;

        if (tipoUsuarioLabel != null) {
            if (esEmpresa) {
                tipoUsuarioLabel.setText("Inicias sesión como: Empresa");
            } else {
                tipoUsuarioLabel.setText("Inicias sesión como: Trabajador");
            }
        }
    }

    @FXML
    protected void onAccederClick() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (mensajeLabel != null) {
            mensajeLabel.setVisible(false);
        }

        if (email.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor, completa todos los campos");
            return;
        }

        if (!email.contains("@")) {
            mostrarMensaje("Por favor, ingresa un email válido");
            return;
        }

        try {
            if (esEmpresa) {
                Empresa empresa = empresaService.obtenerEmpresaPorEmail(email);

                if (empresa != null && empresa.getContrasena().equals(password)) {
                    Usuario usuario = new Usuario(
                            empresa.getNombreEmpresa(),
                            empresa.getCorreoElectronico(),
                            empresa.getContrasena(),
                            true,
                            empresa.getTipoEmpresa(),
                            empresa.getNumTelefono(),
                            empresa.getSectorActividad() != null ? empresa.getSectorActividad().getTipoSectorActividad() : "No especificado",
                            empresa.getActEconomicaPrincipal(),
                            empresa.getCalle(),
                            empresa.getColonia(),
                            empresa.getCodigoPostal(),
                            empresa.getRfc(),
                            empresa.getRazonSocial()
                    );

                    SesionManager.getInstancia().iniciarSesion(usuario);
                    // Ir directamente a la pantalla de empresas sin mostrar mensaje
                    abrirPantallaEmpresas();
                } else {
                    mostrarMensaje("Email o contraseña incorrectos. Verifica tus datos.");
                }
            } else {
                Trabajador trabajador = trabajadorService.obtenerTrabajadorPorEmail(email);

                if (trabajador != null && trabajador.getContrasena().equals(password)) {
                    Usuario usuario = new Usuario(
                            trabajador.getNombre(),
                            trabajador.getCorreoElectronico(),
                            trabajador.getContrasena(),
                            false
                    );

                    SesionManager.getInstancia().iniciarSesion(usuario);
                    // Ir directamente a la pantalla de trabajos sin mostrar mensaje
                    abrirPantallaTrabajos();
                } else {
                    mostrarMensaje("Email o contraseña incorrectos. Verifica tus datos.");
                }
            }
        } catch (Exception e) {
            mostrarAlertaError("Error de conexión",
                    "No se pudo conectar a la base de datos. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCrearCuentaClick() {
        if (esEmpresa) {
            abrirRegistroEmpresa();
        } else {
            abrirRegistroTrabajador();
        }
    }

    @FXML
    protected void onVolverClick() {
        regresarAPantallaPrincipal();
    }

    @FXML
    protected void onMostrarPasswordChanged() {
        if (mostrarPasswordCheckBox.isSelected()) {
            if (passwordVisibleField != null && passwordField != null) {
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordVisibleField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            }
        } else {
            if (passwordVisibleField != null && passwordField != null) {
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordVisibleField.setVisible(false);
                passwordVisibleField.setManaged(false);
            }
        }
    }

    private void abrirRegistroEmpresa() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Registro.fxml"));
            Parent root = fxmlLoader.load();

            RegistroController controller = fxmlLoader.getController();
            controller.setTipoUsuario(true);

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Registro de Empresa");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo abrir el registro de empresa: " + e.getMessage());
        }
    }

    private void abrirRegistroTrabajador() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/RegistroTrabajador.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Registro de Trabajador");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo abrir el registro de trabajador: " + e.getMessage());
        }
    }

    private void abrirPantallaEmpresas() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

            System.out.println("✅ Pantalla de empresas cargada correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo cargar el panel de empresas: " + e.getMessage());
        }
    }

    private void abrirPantallaTrabajos() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Trabajos.fxml"));
            Parent root = fxmlLoader.load();

            TrabajosController controller = fxmlLoader.getController();
            controller.refrescarTabla();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Buscar Trabajos");

            System.out.println("✅ Pantalla de trabajos cargada correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo cargar la pantalla de trabajos: " + e.getMessage());
        }
    }

    private void regresarAPantallaPrincipal() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/hello-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Sistema de Trabajos");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al cargar la pantalla principal");
        }
    }

    private void mostrarMensaje(String mensaje) {
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}