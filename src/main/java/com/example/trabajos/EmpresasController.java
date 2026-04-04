package com.example.trabajos;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Oferta;
import com.example.trabajos.models.Trabajador;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.MatchService;
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
    @FXML private Button editarPerfilButton;
    @FXML private TabPane tabPane;
    @FXML private Tab llenarFormularioTab;
    @FXML private Tab abrirFormularioTab;
    @FXML private Tab trabajadoresTab;
    @FXML private Tab matchTalentoTab;

    // Componentes del Tab 1 - Llenar Formulario
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

    // Componentes del Tab 2 - Abrir Formulario
    @FXML private TableView<Oferta> formulariosTable;
    @FXML private TableColumn<Oferta, String> tituloColumn;
    @FXML private TableColumn<Oferta, String> fechaColumn;
    @FXML private TableColumn<Oferta, Void> accionesColumn;
    @FXML private Label mensajeVacioLabel;
    @FXML private TableColumn<Oferta, String> tipoOfertaColumn;
    @FXML private TableColumn<Oferta, String> estadoPostulacionColumn;

    // Componentes del Tab 3 - Trabajadores Disponibles
    @FXML private TextField buscarField;
    @FXML private TableView<Trabajador> trabajadoresTable;
    @FXML private TableColumn<Trabajador, String> nombreColumn;
    @FXML private TableColumn<Trabajador, String> especialidadColumn;
    @FXML private TableColumn<Trabajador, String> nivelEstudioColumn;
    @FXML private TableColumn<Trabajador, String> experienciaColumn;
    @FXML private TableColumn<Trabajador, String> ubicacionColumn;
    @FXML private TableColumn<Trabajador, Void> accionesColumn2;
    @FXML private Label totalTrabajadoresLabel;

    // Componentes del Tab 4 - Match de Talento
    @FXML private TextField puestoField;
    @FXML private TextField herramientasMatchField;
    @FXML private ComboBox<String> cantidadIdiomasMatchComboBox;
    @FXML private VBox idiomasMatchContainer;
    @FXML private ComboBox<String> nivelEstudioMatchComboBox;
    @FXML private TextField anosExperienciaMatchField;
    @FXML private TextField edadMinField;
    @FXML private TextField edadMaxField;
    @FXML private ComboBox<String> generoMatchComboBox;
    @FXML private ComboBox<String> municipioMatchComboBox;
    @FXML private ComboBox<String> ciudadMatchComboBox;
    @FXML private Button buscarMatchButton;
    @FXML private Button limpiarMatchButton;
    @FXML private TableView<MatchService.MatchResult> resultadosMatchTable;
    @FXML private TableColumn<MatchService.MatchResult, String> nombreMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> puntajeMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> edadMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> especialidadMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> experienciaMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, String> ubicacionMatchColumn;
    @FXML private TableColumn<MatchService.MatchResult, Void> accionesMatchColumn;
    @FXML private Label mensajeMatchLabel;

    private Empresa empresaActual;
    private EmpresaService empresaService = new EmpresaService();

    // Controladores internos
    private FormularioController formularioController;
    private FormulariosTableController formulariosTableController;
    private TrabajadoresDisponiblesController trabajadoresDisponiblesController;
    private MatchTrabajadoresController matchTrabajadoresController;

    @FXML
    public void initialize() {
        cargarEmpresaActual();
        inicializarTabLlenarFormulario();
        inicializarTabAbrirFormulario();
        inicializarTabTrabajadoresDisponibles();
        inicializarTabMatchTalento();
    }

    private void cargarEmpresaActual() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            empresaActual = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());
            if (empresaActual != null) {
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

        if (aceptarButton1 != null) {
            aceptarButton1.setOnAction(e -> formularioController.onGuardarClick());
        }
        if (rechazarButton1 != null) {
            rechazarButton1.setOnAction(e -> formularioController.onCancelarClick());
        }

        formularioController.initialize();
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabAbrirFormulario() {
        formulariosTableController = new FormulariosTableController();
        formulariosTableController.setFormulariosTable(formulariosTable);
        formulariosTableController.setTituloColumn(tituloColumn);
        formulariosTableController.setFechaColumn(fechaColumn);
        formulariosTableController.setAccionesColumn(accionesColumn);
        formulariosTableController.setMensajeVacioLabel(mensajeVacioLabel);
        formulariosTableController.setTipoOfertaColumn(tipoOfertaColumn);
        formulariosTableController.setEstadoPostulacionColumn(estadoPostulacionColumn);

        formulariosTableController.initialize();
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabTrabajadoresDisponibles() {
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

        trabajadoresDisponiblesController.initialize();
    }

    @SuppressWarnings("unchecked")
    private void inicializarTabMatchTalento() {
        matchTrabajadoresController = new MatchTrabajadoresController();
        matchTrabajadoresController.setPuestoField(puestoField);
        matchTrabajadoresController.setHerramientasField(herramientasMatchField);
        matchTrabajadoresController.setCantidadIdiomasComboBox(cantidadIdiomasMatchComboBox);
        matchTrabajadoresController.setIdiomasContainer(idiomasMatchContainer);
        matchTrabajadoresController.setNivelEstudioComboBox(nivelEstudioMatchComboBox);
        matchTrabajadoresController.setAnosExperienciaField(anosExperienciaMatchField);
        matchTrabajadoresController.setEdadMinField(edadMinField);
        matchTrabajadoresController.setEdadMaxField(edadMaxField);
        matchTrabajadoresController.setGeneroComboBox(generoMatchComboBox);
        matchTrabajadoresController.setMunicipioComboBox(municipioMatchComboBox);
        matchTrabajadoresController.setCiudadComboBox(ciudadMatchComboBox);
        matchTrabajadoresController.setResultadosTable(resultadosMatchTable);
        matchTrabajadoresController.setNombreColumn(nombreMatchColumn);
        matchTrabajadoresController.setPuntajeColumn(puntajeMatchColumn);
        matchTrabajadoresController.setEdadColumn(edadMatchColumn);
        matchTrabajadoresController.setEspecialidadColumn(especialidadMatchColumn);
        matchTrabajadoresController.setExperienciaColumn(experienciaMatchColumn);
        matchTrabajadoresController.setUbicacionColumn(ubicacionMatchColumn);
        matchTrabajadoresController.setAccionesColumn(accionesMatchColumn);
        matchTrabajadoresController.setMensajeLabel(mensajeMatchLabel);
        matchTrabajadoresController.setBuscarButton(buscarMatchButton);
        matchTrabajadoresController.setLimpiarButton(limpiarMatchButton);

        matchTrabajadoresController.initialize();
    }

    @FXML
    protected void onEditarPerfilClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/EditarPerfilEmpresa.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Editar Perfil - Empresa");

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el editor de perfil");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onVolverClick() {
        System.out.println("=== CERRANDO SESIÓN ===");

        try {
            boolean eraEmpresa = SesionManager.getInstancia().esEmpresa();

            SesionManager.getInstancia().cerrarSesion();
            System.out.println("✅ Sesión cerrada en SesionManager");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Sesion.fxml"));
            Parent root = loader.load();

            SesionController sesionController = loader.getController();
            sesionController.setTipoUsuario(eraEmpresa);
            System.out.println("✅ Tipo de usuario configurado: " + (eraEmpresa ? "Empresa" : "Trabajador"));

            Stage stage = (Stage) volverButton.getScene().getWindow();
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