package com.example.trabajos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Salario;
import com.example.trabajos.models.EstadoContratacion;
import com.example.trabajos.models.Idioma;
import com.example.trabajos.models.OfertaIdioma;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.OfertaService;
import com.example.trabajos.services.SalarioService;
import com.example.trabajos.services.EstadoContratacionService;
import com.example.trabajos.services.IdiomaService;
import com.example.trabajos.services.OfertaIdiomaService;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FormularioController {

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
    @FXML private Label mensajeLabel;

    private Empresa empresaActual;
    private List<ComboBox<String>> idiomasComboBoxes = new ArrayList<>();

    private EmpresaService empresaService = new EmpresaService();
    private OfertaService ofertaService = new OfertaService();
    private SalarioService salarioService = new SalarioService();
    private EstadoContratacionService estadoContratacionService = new EstadoContratacionService();
    private IdiomaService idiomaService = new IdiomaService();
    private OfertaIdiomaService ofertaIdiomaService = new OfertaIdiomaService();

    private String formatearPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML
    public void initialize() {
        nivelEstudioComboBox.getItems().addAll(
                "Primaria",
                "Secundaria",
                "Bachillerato",
                "Técnico",
                "Licenciatura",
                "Maestría",
                "Doctorado"
        );

        tipoSalarioComboBox.getItems().addAll(
                "Semanal",
                "Quincenal",
                "Mensual"
        );

        tipoTrabajoComboBox.getItems().addAll(
                "Asesor/Consultor",
                "Atención al cliente",
                "Vigilancia/Recepcionista",
                "Tutor/Enseñanza",
                "Artesanías",
                "Jardinería",
                "Limpieza",
                "Repartidor",
                "Cuidado de personas",
                "Trabajo administrativo",
                "Telemercadeo",
                "Guardia de seguridad",
                "Conductor",
                "Cocina ayudante",
                "Otro"
        );

        // Asegurar que el texto se muestre completo
        tipoTrabajoComboBox.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
        tipoTrabajoComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        tipoTrabajoComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Otro".equals(newValue)) {
                otroTrabajoContainer.setVisible(true);
                otroTrabajoContainer.setManaged(true);
            } else {
                otroTrabajoContainer.setVisible(false);
                otroTrabajoContainer.setManaged(false);
                otroTrabajoField.clear();
            }
        });

        cantidadIdiomasComboBox.getItems().addAll(
                "1", "2", "3", "4", "5"
        );

        cantidadIdiomasComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            generarComboBoxIdiomas(newValue);
        });

        List<String> horas = new ArrayList<>();
        for (int i = 7; i < 21; i++) {
            for (int j = 0; j < 60; j += 30) {
                String hora = String.format("%02d:%02d", i, j);
                horas.add(hora);
            }
        }

        horarioEntradaComboBox.getItems().addAll(horas);
        horarioSalidaComboBox.getItems().addAll(horas);

        sueldoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                sueldoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        codigoPostalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                codigoPostalField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (newValue.length() > 5) {
                codigoPostalField.setText(newValue.substring(0, 5));
            }
        });

        configurarCampoSinEspaciosInicio(herramientaField);
        configurarCampoSinEspaciosInicio(experienciaField);
        configurarCampoSinEspaciosInicio(otroTrabajoField);
        configurarTextAreaSinEspaciosInicio(descripcionArea);

        otroTrabajoContainer.setVisible(false);
        otroTrabajoContainer.setManaged(false);

        cargarDatosEmpresa();
    }

    private void configurarCampoSinEspaciosInicio(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.startsWith(" ")) {
                    textField.setText(newValue.trim());
                    return;
                }

                if (oldValue != null && newValue.equals(oldValue + " ")) {
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
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.startsWith(" ")) {
                    textArea.setText(newValue.trim());
                    return;
                }

                if (oldValue != null && newValue.equals(oldValue + " ")) {
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

    private void generarComboBoxIdiomas(String cantidad) {
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();

        if (cantidad != null) {
            int numIdiomas = Integer.parseInt(cantidad);

            HBox horizontalBox = new HBox();
            horizontalBox.setSpacing(8);

            Label labelIdiomas = new Label("Idiomas:");
            labelIdiomas.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 11; -fx-min-width: 60;");
            horizontalBox.getChildren().add(labelIdiomas);

            for (int i = 0; i < numIdiomas; i++) {
                ComboBox<String> comboBox = new ComboBox<>();
                try {
                    for (Idioma idioma : idiomaService.obtenerTodosIdiomas()) {
                        comboBox.getItems().add(idioma.getNombreIdioma());
                    }
                } catch (Exception e) {
                    comboBox.getItems().addAll(
                            "Español",
                            "Inglés",
                            "Francés",
                            "Alemán",
                            "Italiano",
                            "Portugués",
                            "Chino Mandarín",
                            "Japonés",
                            "Coreano",
                            "Ruso",
                            "Árabe"
                    );
                }

                comboBox.setPrefHeight(30);
                comboBox.setPrefWidth(130);
                comboBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-radius: 3;");
                comboBox.setPromptText("Idioma " + (i + 1));

                idiomasComboBoxes.add(comboBox);
                horizontalBox.getChildren().add(comboBox);
            }

            idiomasContainer.getChildren().add(horizontalBox);
        }
    }

    private void cargarDatosEmpresa() {
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
                nombreEmpresaField.setEditable(false);
                calleField.setEditable(false);
                coloniaField.setEditable(false);
                codigoPostalField.setEditable(false);
                gmailField.setEditable(false);
                telefonoField.setEditable(false);
                nombreEmpresaField.setStyle("-fx-background-color: #f0f0f0;");
                calleField.setStyle("-fx-background-color: #f0f0f0;");
                coloniaField.setStyle("-fx-background-color: #f0f0f0;");
                codigoPostalField.setStyle("-fx-background-color: #f0f0f0;");
                gmailField.setStyle("-fx-background-color: #f0f0f0;");
                telefonoField.setStyle("-fx-background-color: #f0f0f0;");
            }
        }
    }

    @FXML
    protected void onGuardarClick(javafx.event.ActionEvent event) {
        if (!validarCampos()) return;
        if (!mostrarConfirmacionDatos()) return;
        guardarOferta(event);
    }

    private void guardarOferta(javafx.event.ActionEvent event) {
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
            nuevaOferta.setTrabajadorDestino(null);

            Salario salario = salarioService.obtenerSalarioPorTipo(tipoSalarioComboBox.getValue());
            if (salario == null) {
                salario = new Salario(tipoSalarioComboBox.getValue());
            }
            nuevaOferta.setSalario(salario);

            EstadoContratacion estado = estadoContratacionService.obtenerEstadoContratacionPorNombre("En espera");
            nuevaOferta.setEstadoContratacion(estado);

            Oferta ofertaPersistida = ofertaService.guardarOferta(nuevaOferta);

            for (ComboBox<String> comboBox : idiomasComboBoxes) {
                if (comboBox.getValue() != null) {
                    Idioma idioma = idiomaService.obtenerIdiomaPorNombre(comboBox.getValue());
                    if (idioma != null) {
                        ofertaIdiomaService.agregarIdiomaAOferta(ofertaPersistida, idioma);
                    }
                }
            }

            mostrarMensaje("Oferta pública guardada exitosamente");
            limpiarCampos();

            // Regresar al panel de empresas después de guardar
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        regresarAEmpresas(event);
                    });
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(() -> {
                        regresarAEmpresas(event);
                    });
                }
            }).start();

        } catch (Exception e) {
            mostrarMensaje("Error al guardar la oferta: " + e.getMessage());
            e.printStackTrace();
        }
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

    private boolean mostrarConfirmacionDatos() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar datos");
        alert.setHeaderText("¿Deseas guardar la oferta?");
        alert.setContentText("Revisa que los datos sean correctos.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void limpiarCampos() {
        herramientaField.clear();
        descripcionArea.clear();
        sueldoField.clear();
        experienciaField.clear();
        otroTrabajoField.clear();
        idiomasContainer.getChildren().clear();
        idiomasComboBoxes.clear();
        cantidadIdiomasComboBox.getSelectionModel().clearSelection();
    }

    private void regresarAEmpresas(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setVisible(true);
    }

    @FXML
    private void onCancelarClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}