package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.example.trabajos.models.Trabajador;
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
    @FXML private Button cancelarButton;
    @FXML private Button volverButton;

    @FXML private ImageView imageViewFoto;
    @FXML private Button btnTomarFoto;
    @FXML private Button btnSubirFoto;
    private byte[] fotoPerfilBytes;

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

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        // LIMITAR NUEVA CONTRASEÑA (máximo 10 caracteres)
        nuevaPasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 10) {
                nuevaPasswordField.setText(oldValue);
                nuevaPasswordVisibleField.setText(oldValue);
            }
        });

        nuevaPasswordVisibleField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 10) {
                nuevaPasswordVisibleField.setText(oldValue);
                nuevaPasswordField.setText(oldValue);
            }
        });

        configurarCombos();
        cargarDatosTrabajador();
        configurarValidadores();
        configurarDatePicker();
    }

    private void configurarCombos() {
        if (nacionalidadComboBox != null) {
            try {
                for (Nacionalidad n : nacionalidadService.obtenerTodasNacionalidades()) {
                    nacionalidadComboBox.getItems().add(n.getNombreNacionalidad());
                }
            } catch (Exception e) {
                nacionalidadComboBox.getItems().addAll("Mexicana", "Estadounidense", "Canadiense", "Española");
            }
        }

        if (estadoCivilComboBox != null) {
            try {
                for (EstadoCivil ec : estadoCivilService.obtenerTodosEstadosCiviles()) {
                    estadoCivilComboBox.getItems().add(ec.getEstadoCivil());
                }
            } catch (Exception e) {
                estadoCivilComboBox.getItems().addAll("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión libre");
            }
        }

        if (nivelEstudioComboBox != null) {
            nivelEstudioComboBox.getItems().addAll(
                    "Primaria", "Secundaria", "Bachillerato", "Técnico",
                    "Licenciatura", "Maestría", "Doctorado"
            );
        }

        if (discapacidadComboBox != null) {
            discapacidadComboBox.getItems().addAll(
                    "Ninguna", "Visual", "Auditiva", "Motriz", "Intelectual", "Psicosocial"
            );
        }

        if (municipioComboBox != null) {
            try {
                for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                    municipioComboBox.getItems().add(m.getNombreMunicipio());
                }
            } catch (Exception e) {
                municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
            }
        }

        if (ciudadComboBox != null) {
            ciudadComboBox.getItems().clear();
        }

        if (municipioComboBox != null) {
            municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    cargarCiudadesPorMunicipio(newVal);
                } else {
                    ciudadComboBox.getItems().clear();
                }
            });
        }

        if (cantidadIdiomasComboBox != null) {
            cantidadIdiomasComboBox.getItems().addAll("0", "1", "2", "3", "4", "5");
            cantidadIdiomasComboBox.setValue("0");
            cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
                generarComboBoxIdiomas(newVal);
            });
        }
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
        if (ciudadComboBox == null) return;

        ciudadComboBox.getItems().clear();

        try {
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(nombreMunicipio);
            if (municipio != null) {
                for (Ciudad c : ciudadService.obtenerCiudadesPorMunicipio(municipio)) {
                    ciudadComboBox.getItems().add(c.getNombreCiudad());
                }
            } else {
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
        }
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
                comboBox.setPromptText("Idioma " + (i + 1));
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
        }
    }

    private void cargarDatosTrabajador() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null && !usuario.isEsEmpresa()) {
            trabajadorOriginal = trabajadorService.obtenerTrabajadorPorEmail(usuario.getEmail());

            if (trabajadorOriginal != null) {
                nombreField.setText(trabajadorOriginal.getNombre());
                apellidoPaternoField.setText(trabajadorOriginal.getApellidoPaterno());
                apellidoMaternoField.setText(trabajadorOriginal.getApellidoMaterno());
                emailField.setText(trabajadorOriginal.getCorreoElectronico());
                fechaNacimientoPicker.setValue(trabajadorOriginal.getFechaNacimiento());

                if (trabajadorOriginal.getGenero() != null) {
                    if ("Masculino".equals(trabajadorOriginal.getGenero().getTipoGenero())) {
                        masculinoRadio.setSelected(true);
                    } else if ("Femenino".equals(trabajadorOriginal.getGenero().getTipoGenero())) {
                        femeninoRadio.setSelected(true);
                    }
                }

                rfcField.setText(trabajadorOriginal.getRfc());
                curpField.setText(trabajadorOriginal.getCurp());
                calleField.setText(trabajadorOriginal.getCalle());
                coloniaField.setText(trabajadorOriginal.getColonia());
                codigoPostalField.setText(trabajadorOriginal.getCodigoPostal());
                telefonoField.setText(trabajadorOriginal.getNumTelefono());
                herramientasField.setText(trabajadorOriginal.getConocimientosHerramientas());
                nivelEstudioComboBox.setValue(trabajadorOriginal.getNivelEstudio());
                especialidadField.setText(trabajadorOriginal.getEspecialidad());
                if (trabajadorOriginal.getAnosExperiencia() != null) {
                    anosExperienciaField.setText(String.valueOf(trabajadorOriginal.getAnosExperiencia()));
                }
                discapacidadComboBox.setValue(trabajadorOriginal.getDiscapacidad());
                experienciaField.setText(trabajadorOriginal.getExperienciaLaboral());
                habilidadesArea.setText(trabajadorOriginal.getHabilidades());

                if (trabajadorOriginal.getNacionalidad() != null) {
                    nacionalidadComboBox.setValue(trabajadorOriginal.getNacionalidad().getNombreNacionalidad());
                }

                if (trabajadorOriginal.getEstadoCivil() != null) {
                    estadoCivilComboBox.setValue(trabajadorOriginal.getEstadoCivil().getEstadoCivil());
                }

                if (trabajadorOriginal.getMunicipio() != null) {
                    municipioComboBox.setValue(trabajadorOriginal.getMunicipio().getNombreMunicipio());
                    cargarCiudadesPorMunicipio(trabajadorOriginal.getMunicipio().getNombreMunicipio());
                }
                if (trabajadorOriginal.getCiudad() != null) {
                    ciudadComboBox.setValue(trabajadorOriginal.getCiudad().getNombreCiudad());
                }

                byte[] fotoBytes = trabajadorOriginal.getFotoPerfil();
                if (fotoBytes != null && fotoBytes.length > 0) {
                    Image foto = byteArrayAImagen(fotoBytes);
                    imageViewFoto.setImage(foto);
                    fotoPerfilBytes = fotoBytes;
                }

                cargarIdiomasExistentes();
            }
        }
    }

    private void cargarIdiomasExistentes() {
        if (trabajadorOriginal != null) {
            List<Idioma> idiomas = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajadorOriginal)
                    .stream().map(ti -> ti.getIdioma()).collect(java.util.stream.Collectors.toList());

            if (!idiomas.isEmpty()) {
                cantidadIdiomasComboBox.setValue(String.valueOf(idiomas.size()));
                javafx.application.Platform.runLater(() -> {
                    for (int i = 0; i < idiomas.size() && i < idiomasComboBoxes.size(); i++) {
                        idiomasComboBoxes.get(i).setValue(idiomas.get(i).getNombreIdioma());
                    }
                });
            }
        }
    }

    private void configurarDatePicker() {
        fechaNacimientoPicker.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                int edad = Period.between(newVal, LocalDate.now()).getYears();
                fechaSeleccionadaLabel.setText("Fecha: " + newVal.toString() + " (Edad: " + edad + " años)");
                fechaSeleccionadaLabel.setVisible(true);
                if (edad < 18) {
                    fechaSeleccionadaLabel.setText("❌ Debes ser mayor de 18 años. Edad actual: " + edad + " años");
                    fechaSeleccionadaLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                } else {
                    fechaSeleccionadaLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            } else {
                fechaSeleccionadaLabel.setVisible(false);
            }
        });
    }

    private void configurarValidadores() {
        telefonoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telefonoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 10) {
                telefonoField.setText(newVal.substring(0, 10));
            }
        });

        codigoPostalField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                codigoPostalField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 5) {
                codigoPostalField.setText(newVal.substring(0, 5));
            }
        });

        rfcField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String filteredValue = newVal.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                if (!filteredValue.equals(newVal)) {
                    rfcField.setText(filteredValue);
                }
                if (filteredValue.length() > 13) {
                    rfcField.setText(filteredValue.substring(0, 13));
                }
            }
        });

        curpField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String filteredValue = newVal.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                if (!filteredValue.equals(newVal)) {
                    curpField.setText(filteredValue);
                }
                if (filteredValue.length() > 18) {
                    curpField.setText(filteredValue.substring(0, 18));
                }
            }
        });

        anosExperienciaField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                anosExperienciaField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 3) {
                anosExperienciaField.setText(newVal.substring(0, 3));
            }
        });

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
    private void onTomarFotoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/TomarFoto.fxml"));
            Parent root = loader.load();

            TomarFotoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Tomar Foto de Perfil");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnTomarFoto.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            controller.setCallback(foto -> {
                if (foto != null) {
                    imageViewFoto.setImage(foto);
                    fotoPerfilBytes = imagenAByteArray(foto);
                    mostrarMensaje("✅ Foto de perfil actualizada", "exito");
                }
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("No se pudo abrir la cámara: " + e.getMessage(), "error");
        }
    }

    @FXML
    private void onSubirFotoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(btnSubirFoto.getScene().getWindow());

        if (selectedFile != null) {
            try {
                Image imagen = new Image(selectedFile.toURI().toString());

                double anchoMax = 300;
                double altoMax = 300;
                double ancho = imagen.getWidth();
                double alto = imagen.getHeight();

                if (ancho > anchoMax || alto > altoMax) {
                    double escala = Math.min(anchoMax / ancho, altoMax / alto);
                    ancho = ancho * escala;
                    alto = alto * escala;
                }

                WritableImage imagenRedimensionada = new WritableImage((int)ancho, (int)alto);
                javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(ancho, alto);
                canvas.getGraphicsContext2D().drawImage(imagen, 0, 0, ancho, alto);
                canvas.snapshot(null, imagenRedimensionada);

                imageViewFoto.setImage(imagenRedimensionada);
                fotoPerfilBytes = imagenAByteArray(imagenRedimensionada);
                mostrarMensaje("✅ Foto de perfil actualizada", "exito");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error al cargar la imagen: " + e.getMessage(), "error");
            }
        }
    }

    private byte[] imagenAByteArray(Image image) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Image byteArrayAImagen(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return new Image(bis);
    }

    @FXML
    private void onGuardarClick() {
        if (!validarCampos()) return;

        try {
            trabajadorOriginal.setNombre(formatearPrimeraLetraMayuscula(nombreField.getText().trim()));
            trabajadorOriginal.setApellidoPaterno(formatearPrimeraLetraMayuscula(apellidoPaternoField.getText().trim()));
            trabajadorOriginal.setApellidoMaterno(formatearPrimeraLetraMayuscula(apellidoMaternoField.getText().trim()));
            trabajadorOriginal.setFechaNacimiento(fechaNacimientoPicker.getValue());

            if (masculinoRadio.isSelected()) {
                trabajadorOriginal.setGenero(generoService.obtenerGeneroPorNombre("Masculino"));
            } else if (femeninoRadio.isSelected()) {
                trabajadorOriginal.setGenero(generoService.obtenerGeneroPorNombre("Femenino"));
            }

            String nuevaPassword = nuevaPasswordField.getText();
            if (!nuevaPassword.isEmpty()) {
                if (nuevaPassword.length() >= 6 && nuevaPassword.length() <= 10) {
                    trabajadorOriginal.setContrasena(nuevaPassword);
                } else if (nuevaPassword.length() < 6) {
                    mostrarMensaje("La contraseña debe tener al menos 6 caracteres", "error");
                    return;
                } else if (nuevaPassword.length() > 10) {
                    mostrarMensaje("La contraseña no puede tener más de 10 caracteres", "error");
                    return;
                }
            }

            if (nacionalidadComboBox.getValue() != null) {
                trabajadorOriginal.setNacionalidad(nacionalidadService.obtenerNacionalidadPorNombre(nacionalidadComboBox.getValue()));
            }

            if (estadoCivilComboBox.getValue() != null) {
                trabajadorOriginal.setEstadoCivil(estadoCivilService.obtenerEstadoCivilPorNombre(estadoCivilComboBox.getValue()));
            }

            trabajadorOriginal.setRfc(rfcField.getText().toUpperCase());
            trabajadorOriginal.setCurp(curpField.getText().toUpperCase());
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
            trabajadorOriginal.setConocimientosHerramientas(herramientasField.getText());
            trabajadorOriginal.setNivelEstudio(nivelEstudioComboBox.getValue());
            trabajadorOriginal.setEspecialidad(especialidadField.getText());
            if (!anosExperienciaField.getText().isEmpty()) {
                trabajadorOriginal.setAnosExperiencia(Integer.parseInt(anosExperienciaField.getText()));
            }
            trabajadorOriginal.setDiscapacidad(discapacidadComboBox.getValue());
            trabajadorOriginal.setExperienciaLaboral(experienciaField.getText());
            trabajadorOriginal.setHabilidades(habilidadesArea.getText());

            if (fotoPerfilBytes != null) {
                trabajadorOriginal.setFotoPerfil(fotoPerfilBytes);
            }

            trabajadorService.actualizarTrabajador(trabajadorOriginal);

            List<com.example.trabajos.models.TrabajadorIdioma> idiomasActuales = trabajadorIdiomaService.obtenerIdiomasPorTrabajador(trabajadorOriginal);
            for (com.example.trabajos.models.TrabajadorIdioma ti : idiomasActuales) {
                trabajadorIdiomaService.eliminarIdiomaDeTrabajador(trabajadorOriginal, ti.getIdioma());
            }

            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
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

        if (telefonoField.getText().isEmpty()) {
            mostrarMensaje("Teléfono es obligatorio", "error");
            return false;
        }

        if (!telefonoField.getText().matches("\\d{10}")) {
            mostrarMensaje("El teléfono debe tener 10 dígitos", "error");
            return false;
        }

        String nuevaPassword = nuevaPasswordField.getText();
        if (!nuevaPassword.isEmpty() && (nuevaPassword.length() < 6 || nuevaPassword.length() > 10)) {
            mostrarMensaje("La contraseña debe tener entre 6 y 10 caracteres", "error");
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
    private void onCancelarClick() {
        regresarATrabajos();
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
}