package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.models.Genero;
import com.example.trabajos.models.Nacionalidad;
import com.example.trabajos.models.EstadoCivil;
import com.example.trabajos.models.Idioma;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.models.Ciudad;
import com.example.trabajos.services.TrabajadorService;
import com.example.trabajos.services.GeneroService;
import com.example.trabajos.services.NacionalidadService;
import com.example.trabajos.services.EstadoCivilService;
import com.example.trabajos.services.IdiomaService;
import com.example.trabajos.services.TrabajadorIdiomaService;
import com.example.trabajos.services.MunicipioService;
import com.example.trabajos.services.CiudadService;

public class RegistroTrabajadorController {
    @FXML private TextField nombreField;
    @FXML private TextField apellidoPaternoField;
    @FXML private TextField apellidoMaternoField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField emailField;
    @FXML private RadioButton masculinoRadio;
    @FXML private RadioButton femeninoRadio;
    @FXML private ComboBox<String> nacionalidadComboBox;
    @FXML private ComboBox<String> estadoCivilComboBox;
    @FXML private TextField rfcField;
    @FXML private TextField curpField;
    @FXML private TextField calleField;
    @FXML private TextField coloniaField;
    @FXML private ComboBox<String> municipioComboBox;
    @FXML private ComboBox<String> ciudadComboBox;
    @FXML private TextField codigoPostalField;
    @FXML private TextField telefonoField;
    @FXML private TextField herramientasField;
    @FXML private ComboBox<String> cantidadIdiomasComboBox;
    @FXML private VBox idiomasContainer;
    @FXML private ComboBox<String> nivelEstudioComboBox;
    @FXML private TextField especialidadField;
    @FXML private TextField anosExperienciaField;
    @FXML private ComboBox<String> discapacidadComboBox;
    @FXML private TextField experienciaField;
    @FXML private TextArea habilidadesArea;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox mostrarPasswordCheckBox;
    @FXML private Label mensajeLabel;
    @FXML private Label confirmacionLabel;
    @FXML private Label fechaSeleccionadaLabel;
    @FXML private ToggleGroup generoGroup;

    private TrabajadorService trabajadorService = new TrabajadorService();
    private GeneroService generoService = new GeneroService();
    private NacionalidadService nacionalidadService = new NacionalidadService();
    private EstadoCivilService estadoCivilService = new EstadoCivilService();
    private IdiomaService idiomaService = new IdiomaService();
    private TrabajadorIdiomaService trabajadorIdiomaService = new TrabajadorIdiomaService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();

    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        if (passwordVisibleField != null && passwordField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }

        configurarDatePicker();

        try {
            for (Nacionalidad nacionalidad : nacionalidadService.obtenerTodasNacionalidades()) {
                nacionalidadComboBox.getItems().add(nacionalidad.getNombreNacionalidad());
            }
        } catch (Exception e) {
            nacionalidadComboBox.getItems().addAll(
                    "Mexicana", "Estadounidense", "Canadiense", "Española", "Colombiana",
                    "Argentina", "Chilena", "Peruana", "Brasileña", "Francesa",
                    "Alemana", "Italiana", "Británica", "China", "Japonesa", "Coreana"
            );
        }
        nacionalidadComboBox.setPromptText("Selecciona nacionalidad");

        try {
            for (EstadoCivil estadoCivil : estadoCivilService.obtenerTodosEstadosCiviles()) {
                estadoCivilComboBox.getItems().add(estadoCivil.getEstadoCivil());
            }
        } catch (Exception e) {
            estadoCivilComboBox.getItems().addAll(
                    "Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión libre"
            );
        }

        try {
            for (Municipio municipio : municipioService.obtenerTodosMunicipios()) {
                municipioComboBox.getItems().add(municipio.getNombreMunicipio());
            }
        } catch (Exception e) {
            municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
        }
        municipioComboBox.setPromptText("Selecciona municipio");

        municipioComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                cargarCiudadesPorMunicipio(newValue);
            } else {
                ciudadComboBox.getItems().clear();
            }
        });

        nivelEstudioComboBox.getItems().addAll(
                "Primaria", "Secundaria", "Bachillerato", "Técnico",
                "Licenciatura", "Maestría", "Doctorado"
        );

        discapacidadComboBox.getItems().addAll(
                "Ninguna", "Visual", "Auditiva", "Motriz", "Intelectual", "Psicosocial"
        );

        cantidadIdiomasComboBox.getItems().addAll("1", "2", "3", "4", "5");
        cantidadIdiomasComboBox.setPromptText("Cantidad de idiomas");

        cantidadIdiomasComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            generarComboBoxIdiomas(newValue);
        });

        configurarValidadoresSimples();
        agregarValidadorTelefono();
        agregarValidadorCodigoPostal();
        agregarValidadorRFC();
        agregarValidadorCURP();
        agregarValidadorAnosExperiencia();
    }

    private void configurarValidadoresSimples() {
        configurarCampoSinEspaciosInicio(nombreField);
        configurarCampoSinEspaciosInicio(apellidoPaternoField);
        configurarCampoSinEspaciosInicio(apellidoMaternoField);
        configurarCampoSinEspaciosInicio(calleField);
        configurarCampoSinEspaciosInicio(coloniaField);
        configurarCampoSinEspaciosInicio(herramientasField);
        configurarCampoSinEspaciosInicio(especialidadField);
        configurarCampoSinEspaciosInicio(experienciaField);
        configurarTextAreaSinEspaciosInicio(habilidadesArea);
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

    private void configurarDatePicker() {
        fechaNacimientoPicker.setEditable(false);
        fechaNacimientoPicker.setPromptText("Selecciona tu fecha de nacimiento");

        LocalDate fechaMaxima = LocalDate.now().minusYears(18);
        fechaNacimientoPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(fechaMaxima) > 0);
                if (date != null && !empty && date.compareTo(fechaMaxima) <= 0) {
                    setStyle("-fx-background-color: #e8f5e8;");
                }
            }
        });

        fechaNacimientoPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int edad = Period.between(newValue, LocalDate.now()).getYears();
                fechaSeleccionadaLabel.setText("Fecha: " + newValue.toString() + " (Edad: " + edad + " años)");
                fechaSeleccionadaLabel.setVisible(true);
                fechaSeleccionadaLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                if (edad < 18) {
                    fechaSeleccionadaLabel.setText("❌ Debes ser mayor de 18 años. Edad actual: " + edad + " años");
                    fechaSeleccionadaLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    fechaNacimientoPicker.setValue(null);
                }
            } else {
                fechaSeleccionadaLabel.setVisible(false);
            }
        });
        fechaSeleccionadaLabel.setVisible(false);
    }

    private void generarComboBoxIdiomas(String cantidad) {
        if (idiomasContainer == null) return;
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null && !"0".equals(cantidad)) {
            int numIdiomas = Integer.parseInt(cantidad);
            HBox hbox = new HBox(10);
            for (int i = 0; i < numIdiomas; i++) {
                ComboBox<String> comboBox = new ComboBox<>();
                try {
                    for (Idioma idioma : idiomaService.obtenerTodosIdiomas()) {
                        comboBox.getItems().add(idioma.getNombreIdioma());
                    }
                } catch (Exception e) {
                    comboBox.getItems().addAll(
                            "Español", "Inglés", "Francés", "Alemán", "Italiano",
                            "Portugués", "Chino Mandarín", "Japonés", "Coreano", "Ruso", "Árabe"
                    );
                }
                comboBox.setPrefHeight(25);
                comboBox.setPrefWidth(150);
                comboBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 11;");
                comboBox.setPromptText("Idioma " + (i + 1));
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
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

    private void agregarValidadorCURP() {
        if (curpField == null) return;
        curpField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String filteredValue = newValue.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                if (!filteredValue.equals(newValue)) {
                    curpField.setText(filteredValue);
                }
                if (filteredValue.length() > 18) {
                    curpField.setText(filteredValue.substring(0, 18));
                }
            }
        });
    }

    private void agregarValidadorAnosExperiencia() {
        if (anosExperienciaField == null) return;
        anosExperienciaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                anosExperienciaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 3) {
                anosExperienciaField.setText(newValue.substring(0, 3));
            }
        });
    }

    @FXML
    protected void onRegistrarClick() {
        System.out.println("=== INICIANDO REGISTRO DE TRABAJADOR ===");

        aplicarFormatoACampos();

        if (!validarCampos()) {
            System.out.println("❌ Validación de campos fallida");
            return;
        }
        System.out.println("✅ Validación de campos exitosa");

        if (!mostrarConfirmacionDatos()) {
            System.out.println("❌ Confirmación cancelada por el usuario");
            return;
        }
        System.out.println("✅ Confirmación aceptada por el usuario");

        try {
            String nombre = formatearPrimeraLetraMayuscula(nombreField.getText().trim());
            String apellidoPaterno = formatearPrimeraLetraMayuscula(apellidoPaternoField.getText().trim());
            String apellidoMaterno = formatearPrimeraLetraMayuscula(apellidoMaternoField.getText().trim());
            String email = emailField.getText();
            String password = passwordField.getText();

            System.out.println("📝 Datos preparados:");
            System.out.println("  Nombre: " + nombre);
            System.out.println("  Apellido Paterno: " + apellidoPaterno);
            System.out.println("  Apellido Materno: " + apellidoMaterno);
            System.out.println("  Email: " + email);

            System.out.println("🔍 Verificando si el email ya existe...");
            if (trabajadorService.existeEmail(email)) {
                mostrarAlertaError("Email ya registrado", "Este email ya está registrado. Por favor, inicia sesión.");
                System.out.println("❌ Email ya existe en la base de datos");
                return;
            }
            System.out.println("✅ Email disponible");

            System.out.println("🔄 Obteniendo objetos relacionados...");
            Genero genero = generoService.obtenerGeneroPorNombre(obtenerGenero());
            Nacionalidad nacionalidad = nacionalidadService.obtenerNacionalidadPorNombre(nacionalidadComboBox.getValue());
            EstadoCivil estadoCivil = estadoCivilService.obtenerEstadoCivilPorNombre(estadoCivilComboBox.getValue());
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(municipioComboBox.getValue());
            Ciudad ciudad = ciudadService.obtenerCiudadPorNombre(ciudadComboBox.getValue());

            System.out.println("🔄 Creando objeto Trabajador...");
            Trabajador trabajador = new Trabajador();
            trabajador.setNombre(nombre);
            trabajador.setApellidoPaterno(apellidoPaterno);
            trabajador.setApellidoMaterno(apellidoMaterno);
            trabajador.setCorreoElectronico(email);
            trabajador.setContrasena(password);
            trabajador.setFechaNacimiento(fechaNacimientoPicker.getValue());
            trabajador.setGenero(genero);
            trabajador.setNacionalidad(nacionalidad);
            trabajador.setEstadoCivil(estadoCivil);
            trabajador.setRfc(rfcField.getText().toUpperCase());
            trabajador.setCurp(curpField.getText().toUpperCase());
            trabajador.setCalle(calleField.getText());
            trabajador.setColonia(coloniaField.getText());
            trabajador.setMunicipio(municipio);
            trabajador.setCiudad(ciudad);
            trabajador.setCodigoPostal(codigoPostalField.getText());
            trabajador.setNumTelefono(telefonoField.getText());
            trabajador.setConocimientosHerramientas(herramientasField.getText());
            trabajador.setNivelEstudio(nivelEstudioComboBox.getValue());
            trabajador.setEspecialidad(especialidadField.getText());

            if (!anosExperienciaField.getText().isEmpty()) {
                try {
                    trabajador.setAnosExperiencia(Integer.parseInt(anosExperienciaField.getText()));
                } catch (NumberFormatException e) {
                    trabajador.setAnosExperiencia(0);
                }
            }

            trabajador.setDiscapacidad(discapacidadComboBox.getValue());
            trabajador.setExperienciaLaboral(experienciaField.getText());
            trabajador.setHabilidades(habilidadesArea != null ? habilidadesArea.getText() : "");

            System.out.println("💾 Guardando trabajador en la base de datos...");
            try {
                trabajadorService.guardarTrabajador(trabajador);
                System.out.println("✅ Trabajador guardado exitosamente en MySQL");

                Trabajador trabajadorGuardado = trabajadorService.obtenerTrabajadorPorEmail(email);
                if (trabajadorGuardado != null) {
                    System.out.println("✅ ID del trabajador generado: " + trabajadorGuardado.getIdTrabajador());

                    if (!idiomasComboBoxes.isEmpty()) {
                        System.out.println("🌐 Guardando idiomas...");
                        for (ComboBox<String> comboBox : idiomasComboBoxes) {
                            if (comboBox.getValue() != null) {
                                Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                                if (idioma != null) {
                                    try {
                                        trabajadorIdiomaService.agregarIdiomaATrabajador(trabajadorGuardado, idioma);
                                        System.out.println("  ✅ Idioma guardado: " + idioma.getNombreIdioma());
                                    } catch (Exception e) {
                                        System.err.println("  ⚠️ Error al guardar idioma " + idioma.getNombreIdioma() + ": " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                    System.out.println("🎉 Registro completo exitoso!");
                    mostrarConfirmacion("¡Registro exitoso! Bienvenido/a " + trabajadorGuardado.getNombreCompleto());

                    System.out.println("⏳ Navegando a iniciar sesión en 2 segundos...");
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("🚀 Navegando a pantalla de sesión...");
                                navegarASesion();
                            });
                        } catch (InterruptedException e) {
                            javafx.application.Platform.runLater(() -> {
                                navegarASesion();
                            });
                        }
                    }).start();

                } else {
                    System.err.println("❌ Error: No se pudo recuperar el trabajador después de guardar");
                    mostrarError("Error: No se pudo completar el registro. Intente nuevamente.");
                }

            } catch (Exception e) {
                System.err.println("❌ Error al guardar trabajador: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error al guardar los datos: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error crítico en el registro: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void aplicarFormatoACampos() {
        aplicarFormatoCampo(nombreField);
        aplicarFormatoCampo(apellidoPaternoField);
        aplicarFormatoCampo(apellidoMaternoField);
        aplicarFormatoCampo(calleField);
        aplicarFormatoCampo(coloniaField);
        aplicarFormatoCampo(herramientasField);
        aplicarFormatoCampo(especialidadField);
        aplicarFormatoCampo(experienciaField);
        aplicarFormatoTextArea(habilidadesArea);
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

    private boolean mostrarConfirmacionDatos() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("¿Estás seguro de que los siguientes datos son correctos?\n\n");
        resumen.append("INFORMACIÓN PERSONAL:\n");
        resumen.append("• Nombre: ").append(nombreField.getText()).append("\n");
        resumen.append("• Apellido Paterno: ").append(apellidoPaternoField.getText()).append("\n");
        resumen.append("• Apellido Materno: ").append(apellidoMaternoField.getText()).append("\n");
        resumen.append("• Email: ").append(emailField.getText()).append("\n");
        resumen.append("• Fecha de nacimiento: ").append(fechaNacimientoPicker.getValue() != null ?
                fechaNacimientoPicker.getValue().toString() : "No especificada").append("\n");

        if (fechaNacimientoPicker.getValue() != null) {
            int edad = Period.between(fechaNacimientoPicker.getValue(), LocalDate.now()).getYears();
            resumen.append("• Edad: ").append(edad).append(" años\n");
        }

        resumen.append("• Género: ").append(obtenerGenero() != null ? obtenerGenero() : "No especificado").append("\n");
        resumen.append("• Nacionalidad: ").append(nacionalidadComboBox.getValue() != null ?
                nacionalidadComboBox.getValue() : "No especificada").append("\n");
        resumen.append("• Estado civil: ").append(estadoCivilComboBox.getValue() != null ?
                estadoCivilComboBox.getValue() : "No especificado").append("\n");
        resumen.append("• RFC: ").append(rfcField.getText()).append("\n");
        resumen.append("• CURP: ").append(curpField.getText()).append("\n");
        resumen.append("\nINFORMACIÓN DE CONTACTO:\n");
        resumen.append("• Calle: ").append(calleField.getText()).append("\n");
        resumen.append("• Colonia: ").append(coloniaField.getText()).append("\n");
        resumen.append("• Municipio: ").append(municipioComboBox.getValue() != null ?
                municipioComboBox.getValue() : "No especificado").append("\n");
        resumen.append("• Ciudad: ").append(ciudadComboBox.getValue() != null ?
                ciudadComboBox.getValue() : "No especificada").append("\n");
        resumen.append("• Código Postal: ").append(codigoPostalField.getText()).append("\n");
        resumen.append("• Teléfono: ").append(telefonoField.getText()).append("\n");
        resumen.append("\nINFORMACIÓN PROFESIONAL:\n");
        resumen.append("• Herramientas: ").append(herramientasField.getText()).append("\n");
        resumen.append("• Idiomas: ").append(construirIdiomasString()).append("\n");
        resumen.append("• Nivel de estudio: ").append(nivelEstudioComboBox.getValue() != null ?
                nivelEstudioComboBox.getValue() : "No especificado").append("\n");
        resumen.append("• Especialidad: ").append(especialidadField.getText()).append("\n");
        resumen.append("• Años de experiencia: ").append(anosExperienciaField.getText()).append("\n");
        resumen.append("• Discapacidad: ").append(discapacidadComboBox.getValue() != null ?
                discapacidadComboBox.getValue() : "No especificada").append("\n");
        resumen.append("• Experiencia: ").append(experienciaField.getText()).append("\n");
        resumen.append("• Habilidades: ").append(habilidadesArea != null ? habilidadesArea.getText() : "").append("\n");

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

    private String construirIdiomasString() {
        StringBuilder idiomas = new StringBuilder();
        for (int i = 0; i < idiomasComboBoxes.size(); i++) {
            String idioma = idiomasComboBoxes.get(i).getValue();
            if (idioma != null && !idioma.isEmpty()) {
                if (idiomas.length() > 0) idiomas.append(", ");
                idiomas.append(idioma);
            }
        }
        return idiomas.length() > 0 ? idiomas.toString() : "No especificado";
    }

    private boolean validarCampos() {
        if (nombreField.getText().isEmpty() || apellidoPaternoField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            mostrarAlertaError("Campos obligatorios vacíos", "Por favor, completa los campos obligatorios:\n\n• Nombre\n• Apellido Paterno\n• Email\n• Contraseña");
            return false;
        }

        if (!emailField.getText().contains("@")) {
            mostrarAlertaError("Email inválido", "Por favor, ingresa un email válido");
            return false;
        }

        if (!validarDominioEmail(emailField.getText())) {
            mostrarAlertaError("Dominio de email no permitido",
                    "Solo se permiten correos con terminación:\n\n• @gmail.com\n• @hotmail.com\n\n" +
                            "Email ingresado: " + emailField.getText() + "\n\nPor favor, usa una de estas extensiones.");
            return false;
        }

        if (passwordField.getText().length() < 6) {
            mostrarAlertaError("Contraseña muy corta", "La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (fechaNacimientoPicker.getValue() == null) {
            mostrarAlertaError("Fecha de nacimiento requerida", "Por favor, selecciona tu fecha de nacimiento");
            return false;
        }

        if (!validarEdad()) {
            mostrarAlertaError("Edad insuficiente",
                    "Debes ser mayor de 18 años para registrarte.\n\n" +
                            "Fecha seleccionada: " + fechaNacimientoPicker.getValue().toString() + "\n" +
                            "Edad calculada: " + Period.between(fechaNacimientoPicker.getValue(), LocalDate.now()).getYears() + " años\n\n" +
                            "Por favor, selecciona una fecha de nacimiento válida.");
            return false;
        }

        if (obtenerGenero() == null) {
            mostrarAlertaError("Género no seleccionado", "Por favor, selecciona tu género");
            return false;
        }

        if (nacionalidadComboBox.getValue() == null) {
            mostrarAlertaError("Nacionalidad no seleccionada", "Por favor, selecciona tu nacionalidad");
            return false;
        }

        if (rfcField.getText().isEmpty()) {
            mostrarAlertaError("RFC requerido", "Por favor, ingresa tu RFC");
            return false;
        }

        if (!validarRFC(rfcField.getText())) {
            mostrarAlertaError("RFC inválido",
                    "El RFC debe contener solo caracteres alfanuméricos y tener entre 12 y 13 caracteres.\n\n" +
                            "RFC ingresado: " + rfcField.getText() + "\n\n" +
                            "Por favor, ingresa un RFC válido.");
            return false;
        }

        if (curpField.getText().isEmpty()) {
            mostrarAlertaError("CURP requerido", "Por favor, ingresa tu CURP");
            return false;
        }

        if (!validarCURP(curpField.getText())) {
            mostrarAlertaError("CURP inválido",
                    "La CURP debe contener solo caracteres alfanuméricos y tener exactamente 18 caracteres.\n\n" +
                            "CURP ingresado: " + curpField.getText() + " (" + curpField.getText().length() + " caracteres)\n\n" +
                            "Por favor, ingresa una CURP válida de 18 caracteres.");
            return false;
        }

        if (municipioComboBox.getValue() == null) {
            mostrarAlertaError("Municipio no seleccionado", "Por favor, selecciona tu municipio");
            return false;
        }

        if (ciudadComboBox.getValue() == null) {
            mostrarAlertaError("Ciudad no seleccionada", "Por favor, selecciona tu ciudad");
            return false;
        }

        if (codigoPostalField.getText().isEmpty()) {
            mostrarAlertaError("Código Postal requerido", "Por favor, ingresa tu código postal");
            return false;
        }

        if (!validarCodigoPostal(codigoPostalField.getText())) {
            mostrarAlertaError("Código Postal inválido",
                    "El código postal debe contener exactamente 5 dígitos numéricos.\n\n" +
                            "Código Postal ingresado: " + codigoPostalField.getText() + " (" + codigoPostalField.getText().length() + " dígitos)\n\n" +
                            "Por favor, ingresa un código postal válido de 5 dígitos.");
            return false;
        }

        if (cantidadIdiomasComboBox.getValue() != null && !"0".equals(cantidadIdiomasComboBox.getValue())) {
            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() == null) {
                    mostrarAlertaError("Idiomas incompletos", "Por favor, selecciona todos los idiomas que indicaste");
                    return false;
                }
            }
        }

        if (!telefonoField.getText().isEmpty() && !validarTelefono(telefonoField.getText())) {
            mostrarAlertaError("Teléfono inválido",
                    "El teléfono debe tener exactamente 10 dígitos numéricos.\n\n" +
                            "Teléfono ingresado: " + telefonoField.getText() + " (" + telefonoField.getText().length() + " dígitos)\n\n" +
                            "Por favor, ingresa un número de teléfono válido de 10 dígitos o deja el campo vacío.");
            return false;
        }

        return true;
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

    private boolean validarCURP(String curp) {
        if (curp == null || curp.isEmpty()) return false;
        return curp.matches("^[A-Z0-9]{18}$");
    }

    private boolean validarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isEmpty()) return false;
        return codigoPostal.matches("^\\d{5}$");
    }

    private boolean validarEdad() {
        if (fechaNacimientoPicker.getValue() == null) return false;
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        return edad >= 18;
    }

    private String obtenerGenero() {
        if (masculinoRadio.isSelected()) return "Masculino";
        else if (femeninoRadio.isSelected()) return "Femenino";
        return null;
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error en los datos");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
        }
        if (confirmacionLabel != null) confirmacionLabel.setVisible(false);
    }

    private void mostrarConfirmacion(String mensaje) {
        if (confirmacionLabel != null) {
            confirmacionLabel.setText(mensaje);
            confirmacionLabel.setVisible(true);
        }
        if (mensajeLabel != null) mensajeLabel.setVisible(false);
    }

    @FXML
    protected void onIniciarSesionClick() {
        navegarASesion();
    }

    @FXML
    protected void onRegresarClick() {
        navegarASesion();
    }

    @FXML
    protected void onMostrarPasswordChanged() {
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

    private void navegarASesion() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = fxmlLoader.load();

            SesionController controller = fxmlLoader.getController();
            controller.setTipoUsuario(false);

            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión - Trabajador");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al navegar a la pantalla de sesión");
        }
    }
}   