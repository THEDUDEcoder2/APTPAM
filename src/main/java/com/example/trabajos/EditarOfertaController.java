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
        System.out.println("=== INICIALIZANDO EditarOfertaController ===");
        configurarCombos();
        cargarEmpresaActual();
    }

    public void setOferta(Oferta oferta) {
        this.ofertaOriginal = oferta;
        System.out.println("=== OFERTA RECIBIDA PARA EDITAR ===");
        System.out.println("ID: " + oferta.getIdOferta());
        System.out.println("Puesto: " + oferta.getPuesto_trabajo());
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
        if (ofertaOriginal == null) {
            System.out.println("❌ ofertaOriginal es NULL");
            return;
        }

        herramientaField.setText("No especificado");

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

        // Sueldo - NOTA: El modelo Oferta no tiene campo de monto, solo tipo de salario
        // Por ahora dejamos el campo de sueldo vacío o con un valor por defecto
        // Si tu modelo tiene un campo para el monto, descomenta la línea correspondiente
        sueldoField.setText(""); // Oferta no tiene monto de sueldo en el modelo
        sueldoField.setPromptText("Ej: 8500 (opcional)");

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

        System.out.println("=== DATOS CARGADOS CORRECTAMENTE ===");
        System.out.println("Tipo salario: " + (ofertaOriginal.getSalario() != null ? ofertaOriginal.getSalario().getTipoSalario() : "null"));
        System.out.println("Nivel estudio: " + ofertaOriginal.getNivel_estudio());
    }

    private void configurarCombos() {
        // Nivel estudio
        if (nivelEstudioComboBox != null) {
            nivelEstudioComboBox.getItems().addAll(
                    "Primaria", "Secundaria", "Bachillerato", "Técnico",
                    "Licenciatura", "Maestría", "Doctorado"
            );
        }

        // Tipo salario
        if (tipoSalarioComboBox != null) {
            tipoSalarioComboBox.getItems().addAll("Semanal", "Quincenal", "Mensual");
        }

        // Tipo trabajo
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

        // Horarios
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

        // Cantidad idiomas
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
        System.out.println("=== BOTÓN GUARDAR CLICKEADO ===");

        if (!validarCampos()) {
            System.out.println("❌ Validación fallida");
            return;
        }

        System.out.println("✅ Validación exitosa");

        try {
            String puestoFinal = "Otro".equals(tipoTrabajoComboBox.getValue())
                    ? otroTrabajoField.getText().trim()
                    : tipoTrabajoComboBox.getValue();

            String horarioCompleto = horarioEntradaComboBox.getValue() + " - " + horarioSalidaComboBox.getValue();

            System.out.println("Guardando - Puesto: " + puestoFinal);
            System.out.println("Guardando - Horario: " + horarioCompleto);
            System.out.println("Guardando - Tipo salario: " + tipoSalarioComboBox.getValue());

            // Actualizar oferta
            ofertaOriginal.setPuesto_trabajo(puestoFinal);
            ofertaOriginal.setDescripcion_trabajo(formatearPrimeraLetraMayuscula(descripcionArea.getText()));
            ofertaOriginal.setExperiencia(experienciaField.getText());
            ofertaOriginal.setJornada_laboral(horarioCompleto);
            ofertaOriginal.setNivel_estudio(nivelEstudioComboBox.getValue());

            // Actualizar salario
            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            ofertaOriginal.setSalario(salario);

            // NOTA: El monto del sueldo no se guarda porque el modelo Oferta no tiene ese campo
            // Si deseas guardar el monto, necesitas agregar un campo en la tabla ofertas

            System.out.println("💾 Llamando a ofertaService.actualizarOferta...");
            ofertaService.actualizarOferta(ofertaOriginal);
            System.out.println("✅ Oferta actualizada en BD");

            // Actualizar idiomas
            System.out.println("🔄 Actualizando idiomas...");
            ofertaIdiomaService.eliminarTodosIdiomasDeOferta(ofertaOriginal);
            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        ofertaIdiomaService.agregarIdiomaAOferta(ofertaOriginal, idioma);
                        System.out.println("  ✅ Idioma agregado: " + idioma.getNombreIdioma());
                    }
                }
            }

            mostrarMensaje("✅ Oferta actualizada exitosamente", "exito");
            System.out.println("=== OFERTA GUARDADA EXITOSAMENTE ===");

            // Volver a empresas después de 1.5 segundos
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
            System.err.println("❌ Error al guardar: " + e.getMessage());
            e.printStackTrace();
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

        return true;
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        System.out.println("Mensaje: " + mensaje + " (Tipo: " + tipo + ")");

        if ("error".equals(tipo)) {
            if (errorLabel != null) {
                errorLabel.setText(mensaje);
                errorLabel.setVisible(true);
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
            if (mensajeLabel != null) {
                mensajeLabel.setVisible(false);
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } else if ("exito".equals(tipo)) {
            if (mensajeLabel != null) {
                mensajeLabel.setText(mensaje);
                mensajeLabel.setVisible(true);
                mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
            }
            if (errorLabel != null) {
                errorLabel.setVisible(false);
            }
        } else {
            if (mensajeLabel != null) {
                mensajeLabel.setText(mensaje);
                mensajeLabel.setVisible(true);
                mensajeLabel.setStyle("-fx-text-fill: #3498db;");
            }
        }

        if (!"error".equals(tipo)) {
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        if (mensajeLabel != null) mensajeLabel.setVisible(false);
                        if (errorLabel != null) errorLabel.setVisible(false);
                    });
                } catch (InterruptedException e) {}
            }).start();
        }
    }

    @FXML
    private void onCancelarClick() {
        System.out.println("=== BOTÓN CANCELAR CLICKEADO ===");
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