package com.example.trabajos;

import com.example.trabajos.models.*;
import com.example.trabajos.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FormularioController {

    private TextField nombreEmpresaField;
    private TextField herramientaField;
    private ComboBox<String> cantidadIdiomasComboBox;
    private VBox idiomasContainer;
    private TextField calleField;
    private TextField coloniaField;
    private TextField codigoPostalField;
    private TextField gmailField;
    private TextField telefonoField;
    private ComboBox<String> tipoTrabajoComboBox;
    private TextField otroTrabajoField;
    private HBox otroTrabajoContainer;
    private ComboBox<String> horarioEntradaComboBox;
    private ComboBox<String> horarioSalidaComboBox;
    private TextField sueldoField;
    private ComboBox<String> tipoSalarioComboBox;
    private ComboBox<String> nivelEstudioComboBox;
    private TextField experienciaField;
    private TextArea descripcionArea;
    private Label mensajeLabel;

    private Empresa empresaActual;
    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();

    private EmpresaService empresaService = new EmpresaService();
    private OfertaService ofertaService = new OfertaService();
    private SalarioService salarioService = new SalarioService();
    private EstadoContratacionService estadoContratacionService = new EstadoContratacionService();
    private IdiomaService idiomaService = new IdiomaService();
    private OfertaIdiomaService ofertaIdiomaService = new OfertaIdiomaService();

    // Setters para inyección desde EmpresasController
    public void setEmpresaActual(Empresa empresa) { this.empresaActual = empresa; }
    public void setNombreEmpresaField(TextField field) { this.nombreEmpresaField = field; }
    public void setHerramientaField(TextField field) { this.herramientaField = field; }
    public void setCantidadIdiomasComboBox(ComboBox<String> cb) { this.cantidadIdiomasComboBox = cb; }
    public void setIdiomasContainer(VBox container) { this.idiomasContainer = container; }
    public void setCalleField(TextField field) { this.calleField = field; }
    public void setColoniaField(TextField field) { this.coloniaField = field; }
    public void setCodigoPostalField(TextField field) { this.codigoPostalField = field; }
    public void setGmailField(TextField field) { this.gmailField = field; }
    public void setTelefonoField(TextField field) { this.telefonoField = field; }
    public void setTipoTrabajoComboBox(ComboBox<String> cb) { this.tipoTrabajoComboBox = cb; }
    public void setOtroTrabajoField(TextField field) { this.otroTrabajoField = field; }
    public void setOtroTrabajoContainer(HBox container) { this.otroTrabajoContainer = container; }
    public void setHorarioEntradaComboBox(ComboBox<String> cb) { this.horarioEntradaComboBox = cb; }
    public void setHorarioSalidaComboBox(ComboBox<String> cb) { this.horarioSalidaComboBox = cb; }
    public void setSueldoField(TextField field) { this.sueldoField = field; }
    public void setTipoSalarioComboBox(ComboBox<String> cb) { this.tipoSalarioComboBox = cb; }
    public void setNivelEstudioComboBox(ComboBox<String> cb) { this.nivelEstudioComboBox = cb; }
    public void setExperienciaField(TextField field) { this.experienciaField = field; }
    public void setDescripcionArea(TextArea area) { this.descripcionArea = area; }

    public void initialize() {
        configurarCombos();
        configurarValidadores();
    }

    private void configurarCombos() {
        if (nivelEstudioComboBox != null) {
            nivelEstudioComboBox.getItems().addAll(
                    "Primaria", "Secundaria", "Bachillerato", "Técnico",
                    "Licenciatura", "Maestría", "Doctorado"
            );
        }

        if (tipoSalarioComboBox != null) {
            tipoSalarioComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
        }

        if (tipoTrabajoComboBox != null) {
            tipoTrabajoComboBox.getItems().addAll(
                    "Asesor/Consultor", "Atención al cliente", "Vigilancia/Recepcionista",
                    "Tutor/Enseñanza", "Artesanías", "Jardinería", "Limpieza",
                    "Repartidor", "Cuidado de personas", "Trabajo administrativo",
                    "Telemercadeo", "Guardia de seguridad", "Conductor", "Cocina ayudante",
                    "Otro"
            );

            tipoTrabajoComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (otroTrabajoContainer != null) {
                    if ("Otro".equals(newValue)) {
                        otroTrabajoContainer.setVisible(true);
                        otroTrabajoContainer.setManaged(true);
                    } else {
                        otroTrabajoContainer.setVisible(false);
                        otroTrabajoContainer.setManaged(false);
                        if (otroTrabajoField != null) otroTrabajoField.clear();
                    }
                }
            });
        }

        if (cantidadIdiomasComboBox != null) {
            cantidadIdiomasComboBox.getItems().addAll("1", "2", "3", "4", "5");
            cantidadIdiomasComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                generarComboBoxIdiomas(newValue);
            });
        }

        if (horarioEntradaComboBox != null && horarioSalidaComboBox != null) {
            List<String> horas = new ArrayList<>();
            for (int i = 7; i < 21; i++) {
                for (int j = 0; j < 60; j += 30) {
                    horas.add(String.format("%02d:%02d", i, j));
                }
            }
            horarioEntradaComboBox.getItems().addAll(horas);
            horarioSalidaComboBox.getItems().addAll(horas);
        }
    }

    private void configurarValidadores() {
        if (sueldoField != null) {
            sueldoField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    sueldoField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }

        if (codigoPostalField != null) {
            codigoPostalField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    codigoPostalField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue.length() > 5) {
                    codigoPostalField.setText(newValue.substring(0, 5));
                }
            });
        }

        configurarCampoSinEspaciosInicio(herramientaField);
        configurarCampoSinEspaciosInicio(experienciaField);
        configurarCampoSinEspaciosInicio(otroTrabajoField);
        configurarTextAreaSinEspaciosInicio(descripcionArea);
    }

    private void configurarCampoSinEspaciosInicio(TextField textField) {
        if (textField == null) return;
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && newValue.startsWith(" ")) {
                textField.setText(newValue.trim());
            }
        });
    }

    private void configurarTextAreaSinEspaciosInicio(TextArea textArea) {
        if (textArea == null) return;
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && newValue.startsWith(" ")) {
                textArea.setText(newValue.trim());
            }
        });
    }

    private void generarComboBoxIdiomas(String cantidad) {
        if (idiomasContainer == null) return;
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null) {
            int numIdiomas = Integer.parseInt(cantidad);
            HBox hbox = new HBox(8);
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
                comboBox.setPromptText("Idioma " + (i + 1));
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
        }
    }

    public void onGuardarClick() {
        if (!validarCampos()) return;
        guardarOferta();
    }

    private void guardarOferta() {
        try {
            String puestoFinal = "Otro".equals(tipoTrabajoComboBox.getValue())
                    ? otroTrabajoField.getText().trim()
                    : tipoTrabajoComboBox.getValue();

            String horarioCompleto = horarioEntradaComboBox.getValue() + " - " + horarioSalidaComboBox.getValue();

            Oferta nuevaOferta = new Oferta();
            nuevaOferta.setEmpresa(empresaActual);
            nuevaOferta.setPuesto_trabajo(puestoFinal);
            nuevaOferta.setDescripcion_trabajo(descripcionArea.getText());
            nuevaOferta.setExperiencia(experienciaField.getText());
            nuevaOferta.setJornada_laboral(horarioCompleto);
            nuevaOferta.setNivel_estudio(nivelEstudioComboBox.getValue());
            nuevaOferta.setCantidad(1);
            nuevaOferta.setFecha_publicacion(LocalDate.now());
            nuevaOferta.setTipoOferta("PUBLICA");

            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) salario = new Salario(tipoSalarioComboBox.getValue());
            nuevaOferta.setSalario(salario);

            Oferta ofertaPersistida = ofertaService.guardarOferta(nuevaOferta);

            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        ofertaIdiomaService.agregarIdiomaAOferta(ofertaPersistida, idioma);
                    }
                }
            }

            mostrarMensaje("Oferta guardada exitosamente");
            limpiarCampos();

        } catch (Exception e) {
            mostrarMensaje("Error al guardar la oferta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onCancelarClick() {
        limpiarCampos();
    }

    private boolean validarCampos() {
        if (herramientaField.getText().isEmpty() ||
                sueldoField.getText().isEmpty() ||
                tipoTrabajoComboBox.getValue() == null ||
                descripcionArea.getText().isEmpty()) {
            mostrarMensaje("Completa todos los campos obligatorios.");
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        if (herramientaField != null) herramientaField.clear();
        if (descripcionArea != null) descripcionArea.clear();
        if (sueldoField != null) sueldoField.clear();
        if (experienciaField != null) experienciaField.clear();
        if (otroTrabajoField != null) otroTrabajoField.clear();
        if (idiomasContainer != null) idiomasContainer.getChildren().clear();
        if (cantidadIdiomasComboBox != null) cantidadIdiomasComboBox.getSelectionModel().clearSelection();
        if (tipoTrabajoComboBox != null) tipoTrabajoComboBox.getSelectionModel().clearSelection();
        if (horarioEntradaComboBox != null) horarioEntradaComboBox.getSelectionModel().clearSelection();
        if (horarioSalidaComboBox != null) horarioSalidaComboBox.getSelectionModel().clearSelection();
        if (tipoSalarioComboBox != null) tipoSalarioComboBox.getSelectionModel().clearSelection();
        if (nivelEstudioComboBox != null) nivelEstudioComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje) {
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }
}