package com.example.trabajos;

import com.example.trabajos.models.*;
import com.example.trabajos.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnviarOfertaDirectaController {

    @FXML private Label trabajadorInfoLabel;
    @FXML private Label trabajadorEmailLabel;
    @FXML private TextField puestoField;
    @FXML private TextArea descripcionArea;
    @FXML private TextField experienciaField;
    @FXML private ComboBox<String> horarioEntradaComboBox;
    @FXML private ComboBox<String> horarioSalidaComboBox;
    @FXML private ComboBox<String> nivelEstudioComboBox;
    @FXML private TextField sueldoField;
    @FXML private ComboBox<String> tipoSalarioComboBox;
    @FXML private TextField herramientasField;
    @FXML private ComboBox<String> cantidadIdiomasComboBox;
    @FXML private VBox idiomasContainer;
    @FXML private TextArea mensajePersonalArea;
    @FXML private Label mensajeLabel;

    private com.example.trabajos.models.Trabajador trabajador;
    private com.example.trabajos.models.Empresa empresaActual;
    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();

    private EmpresaService empresaService = new EmpresaService();
    private OfertaService ofertaService = new OfertaService();
    private SalarioService salarioService = new SalarioService();
    private IdiomaService idiomaService = new IdiomaService();
    private OfertaIdiomaService ofertaIdiomaService = new OfertaIdiomaService();
    private PostulacionService postulacionService = new PostulacionService();

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        configurarCombos();
        configurarValidadores();
        cargarEmpresaActual();
    }

    public void setTrabajador(com.example.trabajos.models.Trabajador trabajador) {
        this.trabajador = trabajador;
        actualizarInfoTrabajador();
    }

    private void actualizarInfoTrabajador() {
        if (trabajador != null) {
            String nombreCompleto = trabajador.getNombre();
            if (trabajador.getApellidoPaterno() != null && !trabajador.getApellidoPaterno().isEmpty()) {
                nombreCompleto += " " + trabajador.getApellidoPaterno();
            }
            if (trabajador.getApellidoMaterno() != null && !trabajador.getApellidoMaterno().isEmpty()) {
                nombreCompleto += " " + trabajador.getApellidoMaterno();
            }

            trabajadorInfoLabel.setText(nombreCompleto);
            trabajadorEmailLabel.setText(trabajador.getCorreoElectronico() != null ?
                    trabajador.getCorreoElectronico() : "No especificado");
        }
    }

    private void cargarEmpresaActual() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            empresaActual = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
        }
    }

    private void configurarCombos() {
        List<String> horas = new ArrayList<>();
        for (int i = 7; i < 21; i++) {
            for (int j = 0; j < 60; j += 30) {
                horas.add(String.format("%02d:%02d", i, j));
            }
        }
        horarioEntradaComboBox.getItems().addAll(horas);
        horarioSalidaComboBox.getItems().addAll(horas);

        nivelEstudioComboBox.getItems().addAll(
                "Primaria", "Secundaria", "Bachillerato", "Técnico",
                "Licenciatura", "Maestría", "Doctorado"
        );

        tipoSalarioComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");

        cantidadIdiomasComboBox.getItems().addAll("1", "2", "3", "4", "5");
        cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
            generarComboBoxIdiomas(newVal);
        });
    }

    private void configurarValidadores() {
        sueldoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                sueldoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        configurarCampoTexto(puestoField);
        configurarCampoTexto(experienciaField);
        configurarCampoTexto(herramientasField);
        configurarTextArea(descripcionArea);
        configurarTextArea(mensajePersonalArea);
    }

    private void configurarCampoTexto(TextField field) {
        field.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (newVal.startsWith(" ")) {
                    field.setText(newVal.trim());
                    return;
                }
                if (old == null || old.isEmpty()) {
                    String formateado = formatearPrimeraLetraMayuscula(newVal);
                    if (!formateado.equals(newVal)) {
                        field.setText(formateado);
                    }
                }
            }
        });
    }

    private void configurarTextArea(TextArea area) {
        area.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (newVal.startsWith(" ")) {
                    area.setText(newVal.trim());
                    return;
                }
                if (old == null || old.isEmpty()) {
                    String formateado = formatearPrimeraLetraMayuscula(newVal);
                    if (!formateado.equals(newVal)) {
                        area.setText(formateado);
                    }
                }
            }
        });
    }

    private void generarComboBoxIdiomas(String cantidad) {
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null) {
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
                            "Portugués", "Chino Mandarín", "Japonés", "Coreano"
                    );
                }
                comboBox.setPrefHeight(30);
                comboBox.setPrefWidth(130);
                comboBox.setPromptText("Idioma " + (i + 1));
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
        }
    }

    @FXML
    private void onEnviarClick() {
        if (!validarCampos()) return;

        try {
            String puestoFinal = formatearPrimeraLetraMayuscula(puestoField.getText().trim());
            String horarioCompleto = horarioEntradaComboBox.getValue() + " - " + horarioSalidaComboBox.getValue();

            // Crear la oferta privada
            Oferta nuevaOferta = new Oferta();
            nuevaOferta.setEmpresa(empresaActual);
            nuevaOferta.setTrabajadorDestino(trabajador); // ¡Esto es crucial!
            nuevaOferta.setPuesto_trabajo(puestoFinal);
            nuevaOferta.setDescripcion_trabajo(formatearPrimeraLetraMayuscula(descripcionArea.getText()));
            nuevaOferta.setExperiencia(formatearPrimeraLetraMayuscula(experienciaField.getText()));
            nuevaOferta.setJornada_laboral(horarioCompleto);
            nuevaOferta.setNivel_estudio(nivelEstudioComboBox.getValue());
            nuevaOferta.setCantidad(1);
            nuevaOferta.setFecha_publicacion(LocalDate.now());
            nuevaOferta.setMensajePersonal(formatearPrimeraLetraMayuscula(mensajePersonalArea.getText()));
            nuevaOferta.setTipoOferta("PRIVADA"); // Explícitamente PRIVADA

            System.out.println("🔍 DATOS DE LA OFERTA PRIVADA:");
            System.out.println("   - Tipo Oferta: " + nuevaOferta.getTipoOferta());
            System.out.println("   - Trabajador Destino ID: " + (trabajador != null ? trabajador.getIdTrabajador() : "null"));
            System.out.println("   - Trabajador Destino Nombre: " + (trabajador != null ? trabajador.getNombre() : "null"));

            // Salario
            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            nuevaOferta.setSalario(salario);

            // Guardar oferta
            Oferta ofertaPersistida = ofertaService.guardarOferta(nuevaOferta);

            System.out.println("✅ OFERTA GUARDADA EN BD:");
            System.out.println("   - ID Oferta: " + ofertaPersistida.getIdOferta());
            System.out.println("   - Tipo en BD: " + ofertaPersistida.getTipoOferta());
            System.out.println("   - Trabajador Destino ID en BD: " +
                    (ofertaPersistida.getTrabajadorDestino() != null ? ofertaPersistida.getTrabajadorDestino().getIdTrabajador() : "null"));

            // Guardar idiomas si los hay
            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        ofertaIdiomaService.agregarIdiomaAOferta(ofertaPersistida, idioma);
                    }
                }
            }

            // Crear postulación automática para el trabajador
            com.example.trabajos.models.Postulacion postulacion = new com.example.trabajos.models.Postulacion();
            postulacion.setTrabajador(trabajador);
            postulacion.setOferta(ofertaPersistida);
            postulacion.setEmpresa(empresaActual);
            postulacion.setEstado("PENDIENTE");
            postulacion.setFechaPostulacion(LocalDateTime.now());

            postulacionService.guardarPostulacion(postulacion);

            mostrarMensajeExito("✅ Oferta privada enviada correctamente a " +
                    trabajador.getNombre() + " " +
                    (trabajador.getApellidoPaterno() != null ? trabajador.getApellidoPaterno() : ""));

            // Cerrar ventana después de 2 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) mensajeLabel.getScene().getWindow();
                        stage.close();
                        abrirTrabajadoresDisponibles();
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(this::cerrarVentana);
                }
            }).start();

        } catch (Exception e) {
            mostrarError("Error al enviar la oferta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirTrabajadoresDisponibles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/TrabajadoresDisponibles.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (puestoField.getText().isEmpty() || descripcionArea.getText().isEmpty() ||
                sueldoField.getText().isEmpty() || tipoSalarioComboBox.getValue() == null ||
                horarioEntradaComboBox.getValue() == null || horarioSalidaComboBox.getValue() == null) {

            mostrarError("Por favor, completa todos los campos obligatorios");
            return false;
        }
        return true;
    }

    private void mostrarMensajeExito(String mensaje) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
        mensajeLabel.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
        mensajeLabel.setVisible(true);
    }

    @FXML
    private void onCancelarClick() {
        cerrarVentana();
        abrirTrabajadoresDisponibles();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) mensajeLabel.getScene().getWindow();
        stage.close();
    }
}