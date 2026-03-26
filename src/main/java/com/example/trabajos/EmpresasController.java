package com.example.trabajos;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.EmpresaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EmpresasController {

    // Componentes del FXML
    @FXML private Button volverButton;
    @FXML private TabPane tabPane;
    @FXML private Tab llenarFormularioTab;
    @FXML private Tab abrirFormularioTab;
    @FXML private Tab trabajadoresTab;

    // Componentes del Tab 1 - Llenar Formulario (usando FormularioController)
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
    @FXML private Button aceptarButton1;
    @FXML private Button rechazarButton1;

    // Componentes del Tab 2 - Abrir Formulario (usando FormulariosTableController)
    @FXML private TableView<Oferta> formulariosTable;
    @FXML private TableColumn<Oferta, String> tituloColumn;
    @FXML private TableColumn<Oferta, String> fechaColumn;
    @FXML private TableColumn<Oferta, Void> accionesColumn;
    @FXML private Label mensajeVacioLabel;

    // Componentes del Tab 3 - Trabajadores Disponibles (usando TrabajadoresDisponiblesController)
    @FXML private TextField buscarField;
    @FXML private TableView<Trabajador> trabajadoresTable;
    @FXML private TableColumn<Trabajador, String> nombreColumn;
    @FXML private TableColumn<Trabajador, String> especialidadColumn;
    @FXML private TableColumn<Trabajador, String> nivelEstudioColumn;
    @FXML private TableColumn<Trabajador, String> experienciaColumn;
    @FXML private TableColumn<Trabajador, String> ubicacionColumn;
    @FXML private TableColumn<Trabajador, Void> accionesColumn2;
    @FXML private Label totalTrabajadoresLabel;

    private Empresa empresaActual;
    private EmpresaService empresaService = new EmpresaService();

    // Controladores internos
    private FormularioController formularioController;
    private FormulariosTableController formulariosTableController;
    private TrabajadoresDisponiblesController trabajadoresDisponiblesController;

    @FXML
    public void initialize() {
        cargarEmpresaActual();
        inicializarTabLlenarFormulario();
        inicializarTabAbrirFormulario();
        inicializarTabTrabajadoresDisponibles();
    }

    private void cargarEmpresaActual() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            empresaActual = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
            if (empresaActual != null) {
                // Cargar datos de la empresa en el formulario
                if (nombreEmpresaField != null) nombreEmpresaField.setText(empresaActual.getNombreEmpresa());
                if (calleField != null) calleField.setText(empresaActual.getCalle());
                if (coloniaField != null) coloniaField.setText(empresaActual.getColonia());
                if (codigoPostalField != null) codigoPostalField.setText(empresaActual.getCodigoPostal());
                if (gmailField != null) gmailField.setText(empresaActual.getCorreoElectronico());
                if (telefonoField != null) telefonoField.setText(empresaActual.getNumTelefono());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabLlenarFormulario() {
        // Crear instancia de FormularioController y pasarle los componentes
        formularioController = new FormularioController();
        formularioController.setEmpresaActual(empresaActual);
        formularioController.setNombreEmpresaField(nombreEmpresaField);
        formularioController.setHerramientaField(herramientaField);
        formularioController.setCantidadIdiomasComboBox(cantidadIdiomasComboBox);
        formularioController.setIdiomasContainer(idiomasContainer);
        formularioController.setCalleField(calleField);
        formularioController.setColoniaField(coloniaField);
        formularioController.setCodigoPostalField(codigoPostalField);
        formularioController.setGmailField(gmailField);
        formularioController.setTelefonoField(telefonoField);
        formularioController.setTipoTrabajoComboBox(tipoTrabajoComboBox);
        formularioController.setOtroTrabajoField(otroTrabajoField);
        formularioController.setOtroTrabajoContainer(otroTrabajoContainer);
        formularioController.setHorarioEntradaComboBox(horarioEntradaComboBox);
        formularioController.setHorarioSalidaComboBox(horarioSalidaComboBox);
        formularioController.setSueldoField(sueldoField);
        formularioController.setTipoSalarioComboBox(tipoSalarioComboBox);
        formularioController.setNivelEstudioComboBox(nivelEstudioComboBox);
        formularioController.setExperienciaField(experienciaField);
        formularioController.setDescripcionArea(descripcionArea);

        // Configurar los botones
        if (aceptarButton1 != null) {
            aceptarButton1.setOnAction(e -> formularioController.onGuardarClick());
        }
        if (rechazarButton1 != null) {
            rechazarButton1.setOnAction(e -> formularioController.onCancelarClick());
        }

        // Inicializar el controlador
        formularioController.initialize();
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabAbrirFormulario() {
        // Crear instancia de FormulariosTableController
        formulariosTableController = new FormulariosTableController();
        formulariosTableController.setFormulariosTable(formulariosTable);
        formulariosTableController.setTituloColumn(tituloColumn);
        formulariosTableController.setFechaColumn(fechaColumn);
        formulariosTableController.setAccionesColumn(accionesColumn);
        formulariosTableController.setMensajeVacioLabel(mensajeVacioLabel);

        // Inicializar y cargar datos
        formulariosTableController.initialize();
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabTrabajadoresDisponibles() {
        // Crear instancia de TrabajadoresDisponiblesController
        trabajadoresDisponiblesController = new TrabajadoresDisponiblesController();
        trabajadoresDisponiblesController.setTrabajadoresTable(trabajadoresTable);
        trabajadoresDisponiblesController.setNombreColumn(nombreColumn);
        trabajadoresDisponiblesController.setEspecialidadColumn(especialidadColumn);
        trabajadoresDisponiblesController.setNivelEstudioColumn(nivelEstudioColumn);
        trabajadoresDisponiblesController.setExperienciaColumn(experienciaColumn);
        trabajadoresDisponiblesController.setUbicacionColumn(ubicacionColumn);
        trabajadoresDisponiblesController.setAccionesColumn(accionesColumn2);
        trabajadoresDisponiblesController.setBuscarField(buscarField);
        trabajadoresDisponiblesController.setTotalTrabajadoresLabel(totalTrabajadoresLabel);

        // Inicializar y cargar datos
        trabajadoresDisponiblesController.initialize();
    }

    @FXML
    private void onVolverClick() {
        System.out.println("=== CERRANDO SESIÓN ===");

        try {
            // Obtener el tipo de usuario antes de cerrar sesión
            boolean eraEmpresa = SesionManager.getInstancia().esEmpresa();

            // Cerrar sesión
            SesionManager.getInstancia().cerrarSesion();
            System.out.println("✅ Sesión cerrada en SesionManager");

            // Cargar la pantalla de inicio de sesión
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = loader.load();

            // Configurar el tipo de usuario en el controlador de sesión
            SesionController sesionController = loader.getController();
            sesionController.setTipoUsuario(eraEmpresa);
            System.out.println("✅ Tipo de usuario configurado: " + (eraEmpresa ? "Empresa" : "Trabajador"));

            // Obtener el Stage actual
            Stage stage = (Stage) volverButton.getScene().getWindow();

            // Cambiar escena
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión");

            System.out.println("✅ Sesión cerrada correctamente, regresando a inicio de sesión");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar Sesion.fxml: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cerrar la sesión");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}