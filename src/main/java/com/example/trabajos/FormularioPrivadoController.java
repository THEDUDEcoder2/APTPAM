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
    @FXML private Label nombreEmpresaLabel;
    @FXML private TextField herramientasField;
    @FXML private TextField calleField;
    @FXML private TextField coloniaField;
    @FXML private TextField codigoPostalField;
    @FXML private TextField gmailField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> tipoTrabajoComboBox;
    @FXML private TextField otroTrabajoField;
    @FXML private HBox otroTrabajoContainer;
    @FXML private ComboBox<String> horarioEntradaComboBox;
    @FXML private ComboBox<String> horarioSalidaComboBox;
    @FXML private TextField sueldoField;
    @FXML private ComboBox<String> tipoSalarioComboBox;
    @FXML private TextArea mensajePersonalArea;
    @FXML private Label mensajeLabel;
    @FXML private Button enviarButton;
    @FXML private Button cancelarButton;

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
                nombreEmpresaLabel.setText(empresaActual.getNombreEmpresa());
                calleField.setText(empresaActual.getCalle());
                coloniaField.setText(empresaActual.getColonia());
                codigoPostalField.setText(empresaActual.getCodigoPostal());
                gmailField.setText(empresaActual.getCorreoElectronico());
                telefonoField.setText(empresaActual.getNumTelefono());
            }
        }
    }

    private void configurarCombos() {
        // Configurar tipos de trabajo
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

        // Horarios
        List<String> horas = new ArrayList<>();
        for (int i = 7; i < 21; i++) {
            for (int j = 0; j < 60; j += 30) {
                horas.add(String.format("%02d:%02d", i, j));
            }
        }
        horarioEntradaComboBox.getItems().addAll(horas);
        horarioSalidaComboBox.getItems().addAll(horas);

        // Tipo salario
        tipoSalarioComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
    }

    private void configurarValidadores() {
        sueldoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                sueldoField.setText(newVal.replaceAll("[^\\d]", ""));
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

        configurarCampoTexto(herramientasField);
        configurarTextArea(mensajePersonalArea);

        // NOTA: No configuramos otroTrabajoField aquí porque su contenedor está invisible al inicio
        // El validador se aplicará cuando se haga visible
    }

    private void configurarCampoTexto(TextField field) {
        if (field == null) return; // Evitar NullPointerException

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
        if (area == null) return; // Evitar NullPointerException

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

            // Crear la oferta PRIVADA
            Oferta nuevaOferta = new Oferta();
            nuevaOferta.setEmpresa(empresaActual);
            nuevaOferta.setTrabajadorDestino(trabajadorDestino);
            nuevaOferta.setPuesto_trabajo(puestoFinal);
            nuevaOferta.setDescripcion_trabajo("");
            nuevaOferta.setExperiencia("");
            nuevaOferta.setJornada_laboral(horarioCompleto);
            nuevaOferta.setNivel_estudio("");
            nuevaOferta.setCantidad(1);
            nuevaOferta.setFecha_publicacion(LocalDate.now());
            nuevaOferta.setMensajePersonal(formatearPrimeraLetraMayuscula(mensajePersonalArea.getText()));
            nuevaOferta.setTipoOferta("PRIVADA");

            // Salario
            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            nuevaOferta.setSalario(salario);

            System.out.println("📝 Creando oferta privada:");
            System.out.println("   - Empresa: " + empresaActual.getNombreEmpresa());
            System.out.println("   - Trabajador destino: " + trabajadorDestino.getNombre() + " " +
                    (trabajadorDestino.getApellidoPaterno() != null ? trabajadorDestino.getApellidoPaterno() : ""));
            System.out.println("   - Puesto: " + puestoFinal);
            System.out.println("   - Tipo: " + nuevaOferta.getTipoOferta());

            // Guardar oferta
            Oferta ofertaPersistida = ofertaService.guardarOferta(nuevaOferta);

            // Crear postulación automática
            com.example.trabajos.models.Postulacion postulacion = new com.example.trabajos.models.Postulacion();
            postulacion.setTrabajador(trabajadorDestino);
            postulacion.setOferta(ofertaPersistida);
            postulacion.setEmpresa(empresaActual);
            postulacion.setEstado("PENDIENTE");
            postulacion.setFechaPostulacion(LocalDateTime.now());
            postulacionService.guardarPostulacion(postulacion);

            System.out.println("✅ OFERTA PRIVADA CREADA EXITOSAMENTE:");
            System.out.println("   - ID Oferta: " + ofertaPersistida.getIdOferta());

            String nombreTrabajador = trabajadorDestino.getNombre();
            if (trabajadorDestino.getApellidoPaterno() != null && !trabajadorDestino.getApellidoPaterno().isEmpty()) {
                nombreTrabajador += " " + trabajadorDestino.getApellidoPaterno();
            }

            mostrarMensajeExito("✅ Oferta privada enviada correctamente a " + nombreTrabajador);

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        volverATrabajadoresDisponibles();
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(this::volverATrabajadoresDisponibles);
                }
            }).start();

        } catch (Exception e) {
            mostrarError("Error al enviar la oferta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void volverATrabajadoresDisponibles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/TrabajadoresDisponibles.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mensajeLabel.getScene().getWindow();
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
                tipoSalarioComboBox.getValue() == null) {

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
        volverATrabajadoresDisponibles();
    }
}