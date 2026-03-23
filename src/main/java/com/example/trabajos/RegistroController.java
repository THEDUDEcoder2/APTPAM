package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.SectorActividad;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.models.Ciudad;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.SectorActividadService;
import com.example.trabajos.services.MunicipioService;
import com.example.trabajos.services.CiudadService;

public class RegistroController {
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox mostrarPasswordCheckBox;
    @FXML private Label mensajeLabel;
    @FXML private Label confirmacionLabel;
    @FXML private Label tipoUsuarioLabel;
    @FXML private VBox camposEmpresaBox;
    @FXML private ComboBox<String> tipoEmpresaComboBox;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> sectorActividadComboBox;
    @FXML private TextArea actividadEconomicaArea;
    @FXML private TextField calleField;
    @FXML private TextField coloniaField;
    @FXML private ComboBox<String> municipioComboBox;
    @FXML private ComboBox<String> ciudadComboBox;
    @FXML private TextField codigoPostalField;
    @FXML private TextField rfcField;
    @FXML private TextField razonSocialField;

    private EmpresaService empresaService = new EmpresaService();
    private SectorActividadService sectorActividadService = new SectorActividadService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();

    private boolean esEmpresa;

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        if (passwordVisibleField != null && passwordField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }
        inicializarComboBoxes();
        if (camposEmpresaBox != null) {
            camposEmpresaBox.setVisible(false);
            camposEmpresaBox.setManaged(false);
        }

        configurarValidadoresSimples();

        agregarValidadorTelefono();
        agregarValidadorCodigoPostal();
        agregarValidadorRFC();
    }

    private void configurarValidadoresSimples() {
        configurarCampoSinEspaciosInicio(nombreField);
        configurarCampoSinEspaciosInicio(razonSocialField);
        configurarCampoSinEspaciosInicio(calleField);
        configurarCampoSinEspaciosInicio(coloniaField);
        configurarTextAreaSinEspaciosInicio(actividadEconomicaArea);
    }

    private void configurarCampoSinEspaciosInicio(TextField textField) {
        if (textField == null) return;
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.startsWith(" ")) {
                    textField.setText(newValue.trim());
                    return;
                }
                if (oldValue == null || oldValue.isEmpty()) {
                    String textoFormateado = formatearPrimeraLetraMayuscula(newValue);
                    if (!textoFormateado.equals(newValue)) {
                        int cursorPos = textField.getCaretPosition();
                        textField.setText(textoFormateado);
                        textField.positionCaret(Math.min(cursorPos, textoFormateado.length()));
                    }
                }
            }
        });
    }

    private void configurarTextAreaSinEspaciosInicio(TextArea textArea) {
        if (textArea == null) return;
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.startsWith(" ")) {
                    textArea.setText(newValue.trim());
                    return;
                }
                if (oldValue == null || oldValue.isEmpty()) {
                    String textoFormateado = formatearPrimeraLetraMayuscula(newValue);
                    if (!textoFormateado.equals(newValue)) {
                        int cursorPos = textArea.getCaretPosition();
                        textArea.setText(textoFormateado);
                        textArea.positionCaret(Math.min(cursorPos, textoFormateado.length()));
                    }
                }
            }
        });
    }

    private void inicializarComboBoxes() {
        if (tipoEmpresaComboBox != null) {
            tipoEmpresaComboBox.getItems().addAll(
                    "Persona Física", "Persona Moral", "Sociedad Anónima",
                    "Sociedad de Responsabilidad Limitada", "Empresa Individual"
            );
        }

        if (municipioComboBox != null) {
            try {
                for (Municipio municipio : municipioService.obtenerTodosMunicipios()) {
                    municipioComboBox.getItems().add(municipio.getNombreMunicipio());
                }
            } catch (Exception e) {
                municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
            }

            municipioComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    cargarCiudadesPorMunicipio(newValue);
                } else if (ciudadComboBox != null) {
                    ciudadComboBox.getItems().clear();
                }
            });
        }

        if (sectorActividadComboBox != null) {
            try {
                for (SectorActividad sector : sectorActividadService.obtenerTodosSectores()) {
                    sectorActividadComboBox.getItems().add(sector.getTipoSectorActividad());
                }
            } catch (Exception e) {
                sectorActividadComboBox.getItems().addAll(
                        "Agricultura, ganadería y pesca", "Industria manufacturera", "Comercio",
                        "Servicios", "Tecnología e informática", "Construcción", "Salud",
                        "Educación", "Finanzas y seguros", "Transporte y logística", "Turismo y hospedaje"
                );
            }
        }
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
        if (ciudadComboBox == null) return;
        try {
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(nombreMunicipio);
            if (municipio != null) {
                List<Ciudad> ciudades = ciudadService.obtenerCiudadesPorMunicipio(municipio);
                ciudadComboBox.getItems().clear();
                for (Ciudad ciudad : ciudades) {
                    ciudadComboBox.getItems().add(ciudad.getNombreCiudad());
                }
            } else {
                ciudadComboBox.getItems().clear();
                switch (nombreMunicipio) {
                    case "Comondú":
                        ciudadComboBox.getItems().addAll("Ciudad Constitución", "Puerto San Carlos", "Puerto Adolfo López Mateos");
                        break;
                    case "La Paz":
                        ciudadComboBox.getItems().addAll("La Paz", "El Centenario", "El Sargento", "La Ventana", "La Ribera");
                        break;
                    case "Loreto":
                        ciudadComboBox.getItems().addAll("Loreto", "Puerto Agua Verde", "Ensenada Blanca", "Ligüí", "San Javier");
                        break;
                    case "Los Cabos":
                        ciudadComboBox.getItems().addAll("Cabo San Lucas", "San José del Cabo", "Santiago", "Miraflores", "Todos Santos");
                        break;
                    case "Mulegé":
                        ciudadComboBox.getItems().addAll("Santa Rosalía", "Mulegé", "Guerrero Negro", "San Ignacio", "Bahía Tortugas");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ciudadComboBox.getItems().clear();
        }
    }

    private void agregarValidadorTelefono() {
        if (telefonoField == null) return;
        telefonoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                telefonoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 10) {
                telefonoField.setText(newValue.substring(0, 10));
            }
        });
    }

    private void agregarValidadorCodigoPostal() {
        if (codigoPostalField == null) return;
        codigoPostalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                codigoPostalField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 5) {
                codigoPostalField.setText(newValue.substring(0, 5));
            }
        });
    }

    private void agregarValidadorRFC() {
        if (rfcField == null) return;
        rfcField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String filteredValue = newValue.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                if (!filteredValue.equals(newValue)) {
                    rfcField.setText(filteredValue);
                }
                if (filteredValue.length() > 13) {
                    rfcField.setText(filteredValue.substring(0, 13));
                }
            }
        });
    }

    public void setTipoUsuario(boolean esEmpresa) {
        this.esEmpresa = esEmpresa;
        if (tipoUsuarioLabel != null) {
            if (esEmpresa) {
                tipoUsuarioLabel.setText("Te registras como: Empresa");
                if (camposEmpresaBox != null) {
                    camposEmpresaBox.setVisible(true);
                    camposEmpresaBox.setManaged(true);
                }
            } else {
                tipoUsuarioLabel.setText("Te registras como: Trabajador");
                if (camposEmpresaBox != null) {
                    camposEmpresaBox.setVisible(false);
                    camposEmpresaBox.setManaged(false);
                }
            }
        }
    }

    @FXML
    protected void onRegistrarClick() {
        aplicarFormatoACampos();

        String nombre = formatearPrimeraLetraMayuscula(nombreField.getText().trim());
        String email = emailField.getText();
        String password = passwordField.getText();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlertaError("Campos obligatorios vacíos", "Por favor, completa todos los campos obligatorios");
            return;
        }

        if (!email.contains("@")) {
            mostrarAlertaError("Email inválido", "Por favor, ingresa un email válido");
            return;
        }

        if (!validarDominioEmail(email)) {
            mostrarAlertaError("Dominio de email no permitido",
                    "Solo se permiten correos con terminación:\n\n• @gmail.com\n• @hotmail.com\n\nPor favor, usa una de estas extensiones.");
            return;
        }

        if (password.length() < 6) {
            mostrarAlertaError("Contraseña muy corta", "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (empresaService.existeEmail(email)) {
            mostrarAlertaError("Email ya registrado", "Este email ya está registrado. Por favor, inicia sesión.");
            return;
        }

        if (esEmpresa) {
            if (tipoEmpresaComboBox.getValue() == null || telefonoField.getText().isEmpty() ||
                    sectorActividadComboBox.getValue() == null || actividadEconomicaArea.getText().isEmpty() ||
                    calleField.getText().isEmpty() || coloniaField.getText().isEmpty() ||
                    municipioComboBox.getValue() == null || ciudadComboBox.getValue() == null ||
                    codigoPostalField.getText().isEmpty() || rfcField.getText().isEmpty() ||
                    razonSocialField.getText().isEmpty()) {

                mostrarAlertaError("Campos de empresa incompletos", "Por favor, completa todos los campos de información de la empresa");
                return;
            }

            if (!validarTelefono(telefonoField.getText())) {
                mostrarAlertaError("Teléfono inválido",
                        "El teléfono debe tener exactamente 10 dígitos numéricos.\n\n" +
                                "Teléfono ingresado: " + telefonoField.getText() + " (" + telefonoField.getText().length() + " dígitos)\n\n" +
                                "Por favor, ingresa un número de teléfono válido de 10 dígitos.");
                return;
            }

            if (!validarRFC(rfcField.getText())) {
                mostrarAlertaError("RFC inválido",
                        "El RFC debe contener solo caracteres alfanuméricos y tener entre 12 y 13 caracteres.\n\n" +
                                "RFC ingresado: " + rfcField.getText() + "\n\n" +
                                "Por favor, ingresa un RFC válido.");
                return;
            }

            if (!validarCodigoPostal(codigoPostalField.getText())) {
                mostrarAlertaError("Código Postal inválido",
                        "El código postal debe contener exactamente 5 dígitos numéricos.\n\n" +
                                "Código Postal ingresado: " + codigoPostalField.getText() + " (" + codigoPostalField.getText().length() + " dígitos)\n\n" +
                                "Por favor, ingresa un código postal válido de 5 dígitos.");
                return;
            }
        }

        if (!mostrarConfirmacionDatos()) {
            return;
        }

        realizarRegistro(nombre, email, password);
    }

    private void aplicarFormatoACampos() {
        aplicarFormatoCampo(nombreField);
        aplicarFormatoCampo(razonSocialField);
        aplicarFormatoCampo(calleField);
        aplicarFormatoCampo(coloniaField);
        aplicarFormatoTextArea(actividadEconomicaArea);
    }

    private void aplicarFormatoCampo(TextField textField) {
        if (textField == null) return;
        String texto = textField.getText();
        if (texto != null && !texto.trim().isEmpty()) {
            String textoFormateado = formatearPrimeraLetraMayuscula(texto.trim());
            if (!textoFormateado.equals(texto)) {
                textField.setText(textoFormateado);
            }
        }
    }

    private void aplicarFormatoTextArea(TextArea textArea) {
        if (textArea == null) return;
        String texto = textArea.getText();
        if (texto != null && !texto.trim().isEmpty()) {
            String textoFormateado = formatearPrimeraLetraMayuscula(texto.trim());
            if (!textoFormateado.equals(texto)) {
                textArea.setText(textoFormateado);
            }
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error en los datos");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean validarDominioEmail(String email) {
        String emailLowerCase = email.toLowerCase();
        return emailLowerCase.endsWith("@gmail.com") || emailLowerCase.endsWith("@hotmail.com");
    }

    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{10}");
    }

    private boolean validarRFC(String rfc) {
        if (rfc == null || rfc.isEmpty()) return false;
        return rfc.matches("^[A-Z0-9]{12,13}$");
    }

    private boolean validarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isEmpty()) return false;
        return codigoPostal.matches("^\\d{5}$");
    }

    private boolean mostrarConfirmacionDatos() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("¿Estás seguro de que los siguientes datos son correctos?\n\n");

        resumen.append("INFORMACIÓN BÁSICA:\n");
        resumen.append("• Nombre: ").append(nombreField.getText()).append("\n");
        resumen.append("• Email: ").append(emailField.getText()).append("\n");
        resumen.append("• Tipo de usuario: ").append(esEmpresa ? "Empresa" : "Buscador").append("\n");

        if (esEmpresa) {
            resumen.append("\nINFORMACIÓN DE LA EMPRESA:\n");
            resumen.append("• Tipo de empresa: ").append(tipoEmpresaComboBox.getValue()).append("\n");
            resumen.append("• Teléfono: ").append(telefonoField.getText()).append("\n");
            resumen.append("• Sector de actividad: ").append(sectorActividadComboBox.getValue()).append("\n");
            resumen.append("• Actividad económica: ").append(actividadEconomicaArea.getText()).append("\n");
            resumen.append("• Calle: ").append(calleField.getText()).append("\n");
            resumen.append("• Colonia: ").append(coloniaField.getText()).append("\n");
            resumen.append("• Municipio: ").append(municipioComboBox.getValue()).append("\n");
            resumen.append("• Ciudad: ").append(ciudadComboBox.getValue()).append("\n");
            resumen.append("• Código Postal: ").append(codigoPostalField.getText()).append("\n");
            resumen.append("• RFC: ").append(rfcField.getText()).append("\n");
            resumen.append("• Razón Social: ").append(razonSocialField.getText()).append("\n");
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Registro");
        confirmacion.setHeaderText("Revisa tus datos antes de continuar");
        confirmacion.setContentText(resumen.toString());

        ButtonType botonSi = new ButtonType("Sí, guardar datos");
        ButtonType botonNo = new ButtonType("No, corregir datos");

        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent()) {
            if (resultado.get() == botonSi) {
                return true;
            } else if (resultado.get() == botonNo) {
                mostrarError("Por favor, revisa y corrige tus datos antes de continuar.");
                return false;
            }
        }

        return false;
    }

    private void realizarRegistro(String nombre, String email, String password) {
        if (esEmpresa) {
            String razonSocial = formatearPrimeraLetraMayuscula(razonSocialField.getText().trim());
            String actividadEconomica = formatearPrimeraLetraMayuscula(actividadEconomicaArea.getText().trim());
            SectorActividad sector = sectorActividadService.obtenerSectorPorNombre(sectorActividadComboBox.getValue());
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(municipioComboBox.getValue());
            Ciudad ciudad = ciudadService.obtenerCiudadPorNombre(ciudadComboBox.getValue());

            Empresa empresa = new Empresa();
            empresa.setNombreEmpresa(nombre);
            empresa.setCorreoElectronico(email);
            empresa.setContrasena(password);
            empresa.setTipoEmpresa(tipoEmpresaComboBox.getValue());
            empresa.setNumTelefono(telefonoField.getText());
            empresa.setSectorActividad(sector);
            empresa.setActEconomicaPrincipal(actividadEconomica);
            empresa.setCalle(calleField.getText());
            empresa.setColonia(coloniaField.getText());
            empresa.setMunicipio(municipio);
            empresa.setCiudad(ciudad);
            empresa.setCodigoPostal(codigoPostalField.getText());
            empresa.setRfc(rfcField.getText().toUpperCase());
            empresa.setRazonSocial(razonSocial);

            try {
                empresaService.guardarEmpresa(empresa);

                if (confirmacionLabel != null) {
                    confirmacionLabel.setText("¡Registro exitoso! Bienvenido/a " + nombre);
                    confirmacionLabel.setVisible(true);
                }
                if (mensajeLabel != null) mensajeLabel.setVisible(false);

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            regresarASesion();
                        });
                    } catch (InterruptedException e) {
                        javafx.application.Platform.runLater(() -> {
                            regresarASesion();
                        });
                    }
                }).start();

            } catch (Exception e) {
                mostrarError("Error al guardar la empresa: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            mostrarError("Por favor, usa el formulario de registro de trabajador");
            return;
        }
    }

    @FXML
    protected void onIniciarSesionClick() {
        regresarASesion();
    }

    @FXML
    protected void onRegresarClick() {
        regresarASesion();
    }

    @FXML
    protected void onMostrarPasswordChanged() {
        if (mostrarPasswordCheckBox != null && passwordField != null && passwordVisibleField != null) {
            if (mostrarPasswordCheckBox.isSelected()) {
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordVisibleField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordVisibleField.setVisible(false);
                passwordVisibleField.setManaged(false);
            }
        }
    }

    private void regresarASesion() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = fxmlLoader.load();

            SesionController controller = fxmlLoader.getController();
            controller.setTipoUsuario(esEmpresa);

            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
        }
        if (confirmacionLabel != null) confirmacionLabel.setVisible(false);
    }
}