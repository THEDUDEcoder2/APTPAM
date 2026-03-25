package com.example.trabajos;

import com.example.trabajos.models.*;
import com.example.trabajos.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FormularioPrivadoController {

    @FXML private Label trabajadorInfoLabel;
    @FXML private Label trabajadorEmailLabel;
    @FXML private TextField herramientasField;
    @FXML private ComboBox<String> horarioEntradaComboBox;
    @FXML private ComboBox<String> horarioSalidaComboBox;
    @FXML private TextField calleField;
    @FXML private TextField coloniaField;
    @FXML private TextField codigoPostalField;
    @FXML private TextField gmailField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> tipoTrabajoComboBox;
    @FXML private TextField otroTrabajoField;
    @FXML private HBox otroTrabajoContainer;
    @FXML private TextField sueldoField;
    @FXML private ComboBox<String> tipoSalarioComboBox;
    @FXML private TextArea mensajePersonalArea;
    @FXML private Label mensajeLabel;

    private com.example.trabajos.models.Trabajador trabajadorDestino;
    private com.example.trabajos.models.Empresa empresaActual;

    private EmpresaService empresaService = new EmpresaService();
    private OfertaService ofertaService = new OfertaService();
    private SalarioService salarioService = new SalarioService();
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

    public void setTrabajadorDestino(com.example.trabajos.models.Trabajador trabajador) {
        this.trabajadorDestino = trabajador;
        actualizarInfoTrabajador();
    }

    private void actualizarInfoTrabajador() {
        if (trabajadorDestino != null) {
            String nombreCompleto = trabajadorDestino.getNombre();
            if (trabajadorDestino.getApellidoPaterno() != null && !trabajadorDestino.getApellidoPaterno().isEmpty()) {
                nombreCompleto += " " + trabajadorDestino.getApellidoPaterno();
            }
            if (trabajadorDestino.getApellidoMaterno() != null && !trabajadorDestino.getApellidoMaterno().isEmpty()) {
                nombreCompleto += " " + trabajadorDestino.getApellidoMaterno();
            }

            trabajadorInfoLabel.setText(nombreCompleto);
            trabajadorEmailLabel.setText(trabajadorDestino.getCorreoElectronico() != null ?
                    trabajadorDestino.getCorreoElectronico() : "No especificado");
        }
    }

    private void cargarEmpresaActual() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            empresaActual = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());

            if (empresaActual != null) {
                calleField.setText(empresaActual.getCalle() != null ? empresaActual.getCalle() : "");
                coloniaField.setText(empresaActual.getColonia() != null ? empresaActual.getColonia() : "");
                codigoPostalField.setText(empresaActual.getCodigoPostal() != null ? empresaActual.getCodigoPostal() : "");
                gmailField.setText(empresaActual.getCorreoElectronico() != null ? empresaActual.getCorreoElectronico() : "");
                telefonoField.setText(empresaActual.getNumTelefono() != null ? empresaActual.getNumTelefono() : "");
            }
        }
    }

    private void configurarCombos() {
        tipoTrabajoComboBox.getItems().addAll(
                "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante",
                "Otro"
        );

        tipoTrabajoComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if ("Otro".equals(newVal)) {
                otroTrabajoContainer.setVisible(true);
                otroTrabajoContainer.setManaged(true);
            } else {
                otroTrabajoContainer.setVisible(false);
                otroTrabajoContainer.setManaged(false);
                otroTrabajoField.clear();
            }
        });

        List<String> horas = new ArrayList<>();
        for (int i = 7; i < 21; i++) {
            for (int j = 0; j < 60; j += 30) {
                horas.add(String.format("%02d:%02d", i, j));
            }
        }
        horarioEntradaComboBox.getItems().addAll(horas);
        horarioSalidaComboBox.getItems().addAll(horas);

        tipoSalarioComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
    }

    private void configurarValidadores() {
        sueldoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                sueldoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        configurarCampoTexto(herramientasField);
        configurarCampoTexto(otroTrabajoField);
        configurarTextArea(mensajePersonalArea);
    }

    private void configurarCampoTexto(TextField field) {
        if (field == null) return;
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
        if (area == null) return;
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

    @FXML
    private void onEnviarClick() {
        if (!validarCampos()) return;

        try {
            String puestoFinal = "Otro".equals(tipoTrabajoComboBox.getValue())
                    ? otroTrabajoField.getText().trim()
                    : tipoTrabajoComboBox.getValue();

            String horarioCompleto = horarioEntradaComboBox.getValue() + " - " + horarioSalidaComboBox.getValue();

            Oferta nuevaOferta = new Oferta();
            nuevaOferta.setEmpresa(empresaActual);
            nuevaOferta.setTrabajadorDestino(trabajadorDestino);
            nuevaOferta.setPuesto_trabajo(puestoFinal);
            nuevaOferta.setDescripcion_trabajo(formatearPrimeraLetraMayuscula(mensajePersonalArea.getText()));
            nuevaOferta.setExperiencia("No especificada");
            nuevaOferta.setJornada_laboral(horarioCompleto);
            nuevaOferta.setNivel_estudio("No especificado");
            nuevaOferta.setCantidad(1);
            nuevaOferta.setFecha_publicacion(LocalDate.now());
            nuevaOferta.setMensajePersonal(formatearPrimeraLetraMayuscula(mensajePersonalArea.getText()));
            nuevaOferta.setTipoOferta("PRIVADA");

            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            nuevaOferta.setSalario(salario);

            Oferta ofertaPersistida = ofertaService.guardarOferta(nuevaOferta);

            com.example.trabajos.models.Postulacion postulacion = new com.example.trabajos.models.Postulacion();
            postulacion.setTrabajador(trabajadorDestino);
            postulacion.setOferta(ofertaPersistida);
            postulacion.setEmpresa(empresaActual);
            postulacion.setEstado("PENDIENTE");
            postulacion.setFechaPostulacion(LocalDateTime.now());
            postulacionService.guardarPostulacion(postulacion);

            String nombreTrabajador = trabajadorDestino.getNombre();
            if (trabajadorDestino.getApellidoPaterno() != null && !trabajadorDestino.getApellidoPaterno().isEmpty()) {
                nombreTrabajador += " " + trabajadorDestino.getApellidoPaterno();
            }

            mostrarMensajeExito("✅ Oferta PRIVADA enviada correctamente a " + nombreTrabajador);

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) mensajeLabel.getScene().getWindow();
                        // REGRESAR A LA MISMA VENTANA - REEMPLAZAR ESCENA
                        volverATrabajadoresDisponibles(stage);
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) mensajeLabel.getScene().getWindow();
                        volverATrabajadoresDisponibles(stage);
                    });
                }
            }).start();

        } catch (Exception e) {
            mostrarError("Error al enviar la oferta privada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void volverATrabajadoresDisponibles(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/TrabajadoresDisponibles.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Trabajadores Disponibles");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (herramientasField.getText().isEmpty() ||
                sueldoField.getText().isEmpty() ||
                tipoTrabajoComboBox.getValue() == null ||
                horarioEntradaComboBox.getValue() == null ||
                horarioSalidaComboBox.getValue() == null ||
                mensajePersonalArea.getText().isEmpty()) {

            mostrarError("Por favor, completa todos los campos obligatorios.");
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
        Stage stage = (Stage) mensajeLabel.getScene().getWindow();
        volverATrabajadoresDisponibles(stage);
    }
}