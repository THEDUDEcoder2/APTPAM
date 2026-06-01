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
import java.util.ArrayList;
import java.util.List;

public class EditarOfertaController {

    @FXML private TextField nombreEmpresaField;
    @FXML private TextField herramientaField;
    @FXML private ComboBox<String> cantidadIdiomasComboBox;
    @FXML private VBox idiomasContainer;
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
    @FXML private ComboBox<String> nivelEstudioComboBox;
    @FXML private TextField experienciaField;
    @FXML private TextArea descripcionArea;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;
    @FXML private Label mensajeLabel;
    @FXML private Label errorLabel;

    // NUEVO: Selector de fecha de expiración
    @FXML private DatePicker fechaExpiracionPicker;

    private Oferta ofertaOriginal;
    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();
    private Empresa empresaActual;

    private OfertaService ofertaService = new OfertaService();
    private EmpresaService empresaService = new EmpresaService();
    private SalarioService salarioService = new SalarioService();
    private IdiomaService idiomaService = new IdiomaService();
    private OfertaIdiomaService ofertaIdiomaService = new OfertaIdiomaService();

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        configurarCombos();
        cargarEmpresaActual();
        configurarFechaExpiracion();
    }

    /**
     * Configura el DatePicker para la fecha de expiración en modo edición
     */
    private void configurarFechaExpiracion() {
        if (fechaExpiracionPicker != null) {
            LocalDate fechaMinima = LocalDate.now().plusDays(7);
            LocalDate fechaMaxima = LocalDate.now().plusDays(365);

            fechaExpiracionPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isBefore(fechaMinima) || date.isAfter(fechaMaxima)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f0f0f0;");
                    }
                }
            });
        }
    }

    public void setOferta(Oferta oferta) {
        this.ofertaOriginal = oferta;
        cargarDatosOferta();
    }

    private void cargarEmpresaActual() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            empresaActual = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
            if (empresaActual != null) {
                nombreEmpresaField.setText(empresaActual.getNombreEmpresa());
                calleField.setText(empresaActual.getCalle());
                coloniaField.setText(empresaActual.getColonia());
                codigoPostalField.setText(empresaActual.getCodigoPostal());
                gmailField.setText(empresaActual.getCorreoElectronico());
                telefonoField.setText(empresaActual.getNumTelefono());
            }
        }
    }

    private void cargarDatosOferta() {
        if (ofertaOriginal == null) return;

        herramientaField.setText("No especificado");

        // Cargar fecha de expiración
        if (fechaExpiracionPicker != null) {
            if (ofertaOriginal.getFechaExpiracion() != null) {
                fechaExpiracionPicker.setValue(ofertaOriginal.getFechaExpiracion());
            } else {
                fechaExpiracionPicker.setValue(LocalDate.now().plusDays(30));
            }
        }

        // Cargar idiomas existentes
        try {
            List<Idioma> idiomas = ofertaIdiomaService.obtenerIdiomasDeOferta(ofertaOriginal);
            if (!idiomas.isEmpty()) {
                cantidadIdiomasComboBox.setValue(String.valueOf(idiomas.size()));
                javafx.application.Platform.runLater(() -> {
                    for (int i = 0; i < idiomas.size() && i < idiomasComboBoxes.size(); i++) {
                        idiomasComboBoxes.get(i).setValue(idiomas.get(i).getNombreIdioma());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error cargando idiomas: " + e.getMessage());
        }

        // Horario
        String horario = ofertaOriginal.getJornada_laboral();
        if (horario != null && horario.contains(" - ")) {
            String[] partes = horario.split(" - ");
            if (partes.length == 2) {
                horarioEntradaComboBox.setValue(partes[0]);
                horarioSalidaComboBox.setValue(partes[1]);
            }
        }

        // Puesto
        tipoTrabajoComboBox.setValue(ofertaOriginal.getPuesto_trabajo());
        if ("Otro".equals(ofertaOriginal.getPuesto_trabajo())) {
            otroTrabajoContainer.setVisible(true);
            otroTrabajoContainer.setManaged(true);
        }

        // Sueldo
        if (ofertaOriginal.getSueldo() != null) {
            sueldoField.setText(String.valueOf(ofertaOriginal.getSueldo()));
        }

        // Tipo de salario
        if (ofertaOriginal.getSalario() != null) {
            tipoSalarioComboBox.setValue(ofertaOriginal.getSalario().getTipoSalario());
        }

        // Nivel estudio
        nivelEstudioComboBox.setValue(ofertaOriginal.getNivel_estudio());

        // Experiencia
        experienciaField.setText(ofertaOriginal.getExperiencia());

        // Descripción
        descripcionArea.setText(ofertaOriginal.getDescripcion_trabajo());
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

        if (cantidadIdiomasComboBox != null) {
            cantidadIdiomasComboBox.getItems().addAll("0", "1", "2", "3", "4", "5");
            cantidadIdiomasComboBox.setValue("0");
            cantidadIdiomasComboBox.valueProperty().addListener((obs, old, newVal) -> {
                generarComboBoxIdiomas(newVal);
            });
        }
    }

    private void generarComboBoxIdiomas(String cantidad) {
        if (idiomasContainer == null) return;
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null && !"0".equals(cantidad)) {
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
                comboBox.setPrefWidth(150);
                idiomasComboBoxes.add(comboBox);
                hbox.getChildren().add(comboBox);
            }
            idiomasContainer.getChildren().add(hbox);
        }
    }

    @FXML
    private void onGuardarClick() {
        if (!validarCampos()) return;

        try {
            String puestoFinal = "Otro".equals(tipoTrabajoComboBox.getValue())
                    ? otroTrabajoField.getText().trim()
                    : tipoTrabajoComboBox.getValue();

            String horarioCompleto = horarioEntradaComboBox.getValue() + " - " + horarioSalidaComboBox.getValue();

            // Actualizar oferta
            ofertaOriginal.setPuesto_trabajo(puestoFinal);
            ofertaOriginal.setDescripcion_trabajo(formatearPrimeraLetraMayuscula(descripcionArea.getText()));
            ofertaOriginal.setExperiencia(experienciaField.getText());
            ofertaOriginal.setJornada_laboral(horarioCompleto);
            ofertaOriginal.setNivel_estudio(nivelEstudioComboBox.getValue());

            // Actualizar fecha de expiración
            if (fechaExpiracionPicker != null && fechaExpiracionPicker.getValue() != null) {
                // Validar que la nueva fecha sea válida
                if (fechaExpiracionPicker.getValue().isBefore(LocalDate.now().plusDays(7))) {
                    mostrarMensaje("⚠️ La fecha de expiración debe ser al menos dentro de 7 días.\n" +
                            "Fecha mínima permitida: " + LocalDate.now().plusDays(7).toString(), "error");
                    return;
                }
                ofertaOriginal.setFechaExpiracion(fechaExpiracionPicker.getValue());
            }

            // Actualizar sueldo
            String sueldoTexto = sueldoField.getText();
            if (sueldoTexto != null && !sueldoTexto.isEmpty()) {
                try {
                    ofertaOriginal.setSueldo(Double.parseDouble(sueldoTexto));
                } catch (NumberFormatException e) {
                    ofertaOriginal.setSueldo(null);
                }
            } else {
                ofertaOriginal.setSueldo(null);
            }

            // Actualizar salario
            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            ofertaOriginal.setSalario(salario);

            ofertaService.actualizarOferta(ofertaOriginal);

            // Actualizar idiomas
            ofertaIdiomaService.eliminarTodosIdiomasDeOferta(ofertaOriginal);
            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        ofertaIdiomaService.agregarIdiomaAOferta(ofertaOriginal, idioma);
                    }
                }
            }

            mostrarMensaje("✅ Oferta actualizada exitosamente. Válida hasta: " + ofertaOriginal.getFechaExpiracionFormateada(), "exito");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
                            Parent root = loader.load();
                            Stage stage = (Stage) guardarButton.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setMaximized(true);
                            stage.setTitle("Panel de Empresas");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            mostrarMensaje("❌ Error al guardar: " + e.getMessage(), "error");
        }
    }

    private boolean validarCampos() {
        if (tipoTrabajoComboBox.getValue() == null) {
            mostrarMensaje("Selecciona un puesto de trabajo", "error");
            return false;
        }

        if (descripcionArea.getText().isEmpty()) {
            mostrarMensaje("La descripción del puesto es obligatoria", "error");
            return false;
        }

        if (horarioEntradaComboBox.getValue() == null || horarioSalidaComboBox.getValue() == null) {
            mostrarMensaje("Selecciona el horario de trabajo", "error");
            return false;
        }

        if (tipoSalarioComboBox.getValue() == null) {
            mostrarMensaje("Selecciona el tipo de sueldo", "error");
            return false;
        }

        if (fechaExpiracionPicker != null && fechaExpiracionPicker.getValue() != null) {
            LocalDate fechaMinima = LocalDate.now().plusDays(7);
            if (fechaExpiracionPicker.getValue().isBefore(fechaMinima)) {
                mostrarMensaje("⚠️ La fecha de expiración debe ser al menos dentro de 7 días.\n" +
                        "Fecha mínima permitida: " + fechaMinima.toString(), "error");
                return false;
            }
        } else {
            mostrarMensaje("Selecciona una fecha de expiración para la oferta", "error");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}