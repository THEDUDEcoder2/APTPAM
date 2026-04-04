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
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class EditarPerfilTrabajadorController {

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
    @FXML private PasswordField nuevaPasswordField;
    @FXML private TextField nuevaPasswordVisibleField;
    @FXML private CheckBox mostrarPasswordCheckBox;
    @FXML private Label mensajeLabel;
    @FXML private Label errorLabel;
    @FXML private Label fechaSeleccionadaLabel;
    @FXML private ToggleGroup generoGroup;
    @FXML private Button guardarButton;
    @FXML private Button volverButton;

    private TrabajadorService trabajadorService = new TrabajadorService();
    private GeneroService generoService = new GeneroService();
    private NacionalidadService nacionalidadService = new NacionalidadService();
    private EstadoCivilService estadoCivilService = new EstadoCivilService();
    private IdiomaService idiomaService = new IdiomaService();
    private TrabajadorIdiomaService trabajadorIdiomaService = new TrabajadorIdiomaService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();

    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();
    private Trabajador trabajadorOriginal;

    @FXML
    public void initialize() {
        cargarDatosTrabajador();
        configurarCombos();
        configurarValidadores();
        configurarDatePicker();
    }

    private void cargarDatosTrabajador() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null && !usuario.isEsEmpresa()) {
            trabajadorOriginal = trabajadorService.obtenerTrabajadorPorEmail(usuario.getEmail());

            if (trabajadorOriginal != null) {
                // Cargar datos personales
                nombreField.setText(trabajadorOriginal.getNombre());
                apellidoPaternoField.setText(trabajadorOriginal.getApellidoPaterno());
                apellidoMaternoField.setText(trabajadorOriginal.getApellidoMaterno());
                emailField.setText(trabajadorOriginal.getCorreoElectronico());
                fechaNacimientoPicker.setValue(trabajadorOriginal.getFechaNacimiento());

                // Género
                if (trabajadorOriginal.getGenero() != null) {
                    if ("Masculino".equals(trabajadorOriginal.getGenero().getTipoGenero())) {
                        masculinoRadio.setSelected(true);
                    } else if ("Femenino".equals(trabajadorOriginal.getGenero().getTipoGenero())) {
                        femeninoRadio.setSelected(true);
                    }
                }

                // Documentos
                rfcField.setText(trabajadorOriginal.getRfc());
                curpField.setText(trabajadorOriginal.getCurp());

                // Dirección
                calleField.setText(trabajadorOriginal.getCalle());
                coloniaField.setText(trabajadorOriginal.getColonia());
                codigoPostalField.setText(trabajadorOriginal.getCodigoPostal());
                telefonoField.setText(trabajadorOriginal.getNumTelefono());

                // Datos profesionales
                herramientasField.setText(trabajadorOriginal.getConocimientosHerramientas());
                nivelEstudioComboBox.setValue(trabajadorOriginal.getNivelEstudio());
                especialidadField.setText(trabajadorOriginal.getEspecialidad());
                if (trabajadorOriginal.getAnosExperiencia() != null) {
                    anosExperienciaField.setText(String.valueOf(trabajadorOriginal.getAnosExperiencia()));
                }
                discapacidadComboBox.setValue(trabajadorOriginal.getDiscapacidad());
                experienciaField.setText(trabajadorOriginal.getExperienciaLaboral());
                habilidadesArea.setText(trabajadorOriginal.getHabilidades());

                // Nacionalidad
                if (trabajadorOriginal.getNacionalidad() != null) {
                    nacionalidadComboBox.setValue(trabajadorOriginal.getNacionalidad().getNombreNacionalidad());
                }

                // Estado Civil
                if (trabajadorOriginal.getEstadoCivil() != null) {
                    estadoCivilComboBox.setValue(trabajadorOriginal.getEstadoCivil().getEstadoCivil());
                }

                // Municipio y Ciudad
                if (trabajadorOriginal.getMunicipio() != null) {
                    municipioComboBox.setValue(trabajadorOriginal.getMunicipio().getNombreMunicipio());
                    cargarCiudadesPorMunicipio(trabajadorOriginal.getMunicipio().getNombreMunicipio());
                }
                if (trabajadorOriginal.getCiudad() != null) {
                    ciudadComboBox.setValue(trabajadorOriginal.getCiudad().getNombreCiudad());
                }
            }
        }
    }

    private void configurarCombos() {
        // Nacionalidad
        try {
            for (Nacionalidad n : nacionalidadService.obtenerTodasNacionalidades()) {
                nacionalidadComboBox.getItems().add(n.getNombreNacionalidad());
            }
        } catch (Exception e) {
            nacionalidadComboBox.getItems().addAll("Mexicana", "Estadounidense", "Canadiense", "Española");
        }

        // Estado Civil
        try {
            for (EstadoCivil ec : estadoCivilService.obtenerTodosEstadosCiviles()) {
                estadoCivilComboBox.getItems().add(ec.getEstadoCivil());
            }
        } catch (Exception e) {
            estadoCivilComboBox.getItems().addAll("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión libre");
        }

        // Municipio
        try {
            for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                municipioComboBox.getItems().add(m.getNombreMunicipio());
            }
        } catch (Exception e) {
            municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
        }

        municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                cargarCiudadesPorMunicipio(newVal);
            }
        });

        // Nivel estudio
        nivelEstudioComboBox.getItems().addAll("Primaria", "Secundaria", "Bachillerato", "Técnico", "Licenciatura", "Maestría", "Doctorado");

        // Discapacidad
        discapacidadComboBox.getItems().addAll("Ninguna", "Visual", "Auditiva", "Motriz", "Intelectual", "Psicosocial");

        // Idiomas
        cantidadIdiomasComboBox.getItems().addAll("1", "2", "3", "4", "5");
        cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
            generarComboBoxIdiomas(newVal);
        });

        // Cargar idiomas existentes del trabajador
        cargarIdiomasExistentes();
    }

    private void cargarIdiomasExistentes() {
        if (trabajadorOriginal != null) {
            List<Idioma> idiomas = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajadorOriginal)
                    .stream().map(ti -> ti.getIdioma()).collect(java.util.stream.Collectors.toList());

            if (!idiomas.isEmpty()) {
                cantidadIdiomasComboBox.setValue(String.valueOf(idiomas.size()));
                // Los idiomas se cargarán en generarComboBoxIdiomas
            }
        }
    }

    private void generarComboBoxIdiomas(String cantidad) {
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
                    comboBox.getItems().addAll("Español", "Inglés", "Francés", "Alemán", "Italiano", "Portugués");
                }
                comboBox.setPrefHeight(25);
                comboBox.setPrefWidth(150);
                comboBox.setPromptText("Idioma " + (i + 1));
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);

            // Cargar idiomas existentes
            if (trabajadorOriginal != null) {
                List<Idioma> idiomas = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajadorOriginal)
                        .stream().map(ti -> ti.getIdioma()).collect(java.util.stream.Collectors.toList());
                for (int i = 0; i < idiomas.size() && i < idiomasComboBoxes.size(); i++) {
                    idiomasComboBoxes.get(i).setValue(idiomas.get(i).getNombreIdioma());
                }
            }
        }
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
        ciudadComboBox.getItems().clear();
        if (nombreMunicipio == null) return;

        try {
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(nombreMunicipio);
            if (municipio != null) {
                for (Ciudad c : ciudadService.obtenerCiudadesPorMunicipio(municipio)) {
                    ciudadComboBox.getItems().add(c.getNombreCiudad());
                }
            }
        } catch (Exception e) {
            switch (nombreMunicipio) {
                case "Comondú": ciudadComboBox.getItems().addAll("Ciudad Constitución", "Puerto San Carlos"); break;
                case "La Paz": ciudadComboBox.getItems().addAll("La Paz", "El Centenario", "El Sargento"); break;
                case "Loreto": ciudadComboBox.getItems().addAll("Loreto", "Puerto Agua Verde"); break;
                case "Los Cabos": ciudadComboBox.getItems().addAll("Cabo San Lucas", "San José del Cabo"); break;
                case "Mulegé": ciudadComboBox.getItems().addAll("Santa Rosalía", "Mulegé", "Guerrero Negro"); break;
            }
        }
    }

    private void configurarDatePicker() {
        fechaNacimientoPicker.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                int edad = Period.between(newVal, LocalDate.now()).getYears();
                fechaSeleccionadaLabel.setText("Fecha: " + newVal + " (Edad: " + edad + " años)");
                fechaSeleccionadaLabel.setVisible(true);
                if (edad < 18) {
                    fechaSeleccionadaLabel.setText("❌ Debes ser mayor de 18 años");
                    fechaSeleccionadaLabel.setStyle("-fx-text-fill: #e74c3c;");
                } else {
                    fechaSeleccionadaLabel.setStyle("-fx-text-fill: #27ae60;");
                }
            }
        });
    }

    private void configurarValidadores() {
        // Validar teléfono (solo números, 10 dígitos)
        telefonoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telefonoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 10) {
                telefonoField.setText(newVal.substring(0, 10));
            }
        });

        // Validar código postal
        codigoPostalField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                codigoPostalField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 5) {
                codigoPostalField.setText(newVal.substring(0, 5));
            }
        });

        // Validar años experiencia
        anosExperienciaField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                anosExperienciaField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
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
    private void onGuardarClick() {
        if (!validarCampos()) return;

        try {
            // Actualizar datos del trabajador
            trabajadorOriginal.setNombre(nombreField.getText().trim());
            trabajadorOriginal.setApellidoPaterno(apellidoPaternoField.getText().trim());
            trabajadorOriginal.setApellidoMaterno(apellidoMaternoField.getText().trim());
            trabajadorOriginal.setFechaNacimiento(fechaNacimientoPicker.getValue());

            // Género
            if (masculinoRadio.isSelected()) {
                trabajadorOriginal.setGenero(generoService.obtenerGeneroPorNombre("Masculino"));
            } else if (femeninoRadio.isSelected()) {
                trabajadorOriginal.setGenero(generoService.obtenerGeneroPorNombre("Femenino"));
            }

            // Contraseña (solo si se ingresó una nueva)
            String nuevaPassword = nuevaPasswordField.getText();
            if (!nuevaPassword.isEmpty()) {
                if (nuevaPassword.length() >= 6) {
                    trabajadorOriginal.setContrasena(nuevaPassword);
                }
            }

            // Nacionalidad
            if (nacionalidadComboBox.getValue() != null) {
                trabajadorOriginal.setNacionalidad(nacionalidadService.obtenerNacionalidadPorNombre(nacionalidadComboBox.getValue()));
            }

            // Estado Civil
            if (estadoCivilComboBox.getValue() != null) {
                trabajadorOriginal.setEstadoCivil(estadoCivilService.obtenerEstadoCivilPorNombre(estadoCivilComboBox.getValue()));
            }

            // Documentos
            trabajadorOriginal.setRfc(rfcField.getText().toUpperCase());
            trabajadorOriginal.setCurp(curpField.getText().toUpperCase());

            // Dirección
            trabajadorOriginal.setCalle(calleField.getText());
            trabajadorOriginal.setColonia(coloniaField.getText());
            if (municipioComboBox.getValue() != null) {
                trabajadorOriginal.setMunicipio(municipioService.obtenerMunicipioPorNombre(municipioComboBox.getValue()));
            }
            if (ciudadComboBox.getValue() != null) {
                trabajadorOriginal.setCiudad(ciudadService.obtenerCiudadPorNombre(ciudadComboBox.getValue()));
            }
            trabajadorOriginal.setCodigoPostal(codigoPostalField.getText());
            trabajadorOriginal.setNumTelefono(telefonoField.getText());

            // Datos profesionales
            trabajadorOriginal.setConocimientosHerramientas(herramientasField.getText());
            trabajadorOriginal.setNivelEstudio(nivelEstudioComboBox.getValue());
            trabajadorOriginal.setEspecialidad(especialidadField.getText());
            if (!anosExperienciaField.getText().isEmpty()) {
                trabajadorOriginal.setAnosExperiencia(Integer.parseInt(anosExperienciaField.getText()));
            }
            trabajadorOriginal.setDiscapacidad(discapacidadComboBox.getValue());
            trabajadorOriginal.setExperienciaLaboral(experienciaField.getText());
            trabajadorOriginal.setHabilidades(habilidadesArea.getText());

            // Guardar en BD
            trabajadorService.actualizarTrabajador(trabajadorOriginal);

            // Actualizar idiomas
            // Primero eliminar idiomas existentes
            List<com.example.trabajos.models.TrabajadorIdioma> idiomasActuales = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajadorOriginal);
            for (com.example.trabajos.models.TrabajadorIdioma ti : idiomasActuales) {
                // Eliminar relación (esto requiere un método en el servicio)
            }

            // Agregar nuevos idiomas
            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        trabajadorIdiomaService.agregarIdiomaATrabajador(trabajadorOriginal, idioma);
                    }
                }
            }

            mostrarMensaje("✅ Datos actualizados correctamente", "exito");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::regresarATrabajos);
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(this::regresarATrabajos);
                }
            }).start();

        } catch (Exception e) {
            mostrarMensaje("❌ Error al guardar: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().isEmpty() || apellidoPaternoField.getText().isEmpty()) {
            mostrarMensaje("Nombre y Apellido Paterno son obligatorios", "error");
            return false;
        }

        if (fechaNacimientoPicker.getValue() == null) {
            mostrarMensaje("Selecciona tu fecha de nacimiento", "error");
            return false;
        }

        int edad = Period.between(fechaNacimientoPicker.getValue(), LocalDate.now()).getYears();
        if (edad < 18) {
            mostrarMensaje("Debes ser mayor de 18 años", "error");
            return false;
        }

        if (rfcField.getText().isEmpty()) {
            mostrarMensaje("RFC es obligatorio", "error");
            return false;
        }

        if (curpField.getText().isEmpty()) {
            mostrarMensaje("CURP es obligatorio", "error");
            return false;
        }

        if (codigoPostalField.getText().isEmpty() || codigoPostalField.getText().length() != 5) {
            mostrarMensaje("Código Postal debe tener 5 dígitos", "error");
            return false;
        }

        String nuevaPassword = nuevaPasswordField.getText();
        if (!nuevaPassword.isEmpty() && nuevaPassword.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres", "error");
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        if ("error".equals(tipo)) {
            errorLabel.setText(mensaje);
            errorLabel.setVisible(true);
            mensajeLabel.setVisible(false);
        } else {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
            errorLabel.setVisible(false);
        }

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    mensajeLabel.setVisible(false);
                    errorLabel.setVisible(false);
                });
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void onRegresarClick() {
        regresarATrabajos();
    }

    private void regresarATrabajos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Trabajos.fxml"));
            Parent root = loader.load();

            TrabajosController controller = loader.getController();
            controller.refrescarTabla();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Buscar Trabajos");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelarClick() {
        regresarATrabajos();
    }
}