package com.example.trabajos;

import com.example.trabajos.models.Empresa;
import com.example.trabajos.models.Ciudad;
import com.example.trabajos.models.Municipio;
import com.example.trabajos.models.SectorActividad;
import com.example.trabajos.services.EmpresaService;
import com.example.trabajos.services.CiudadService;
import com.example.trabajos.services.MunicipioService;
import com.example.trabajos.services.SectorActividadService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class EditarPerfilEmpresaController {

    @FXML private TextField nombreEmpresaField;
    @FXML private TextField razonSocialField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private TextField rfcField;
    @FXML private ComboBox<String> tipoEmpresaComboBox;
    @FXML private ComboBox<String> sectorActividadComboBox;
    @FXML private TextArea actividadEconomicaArea;
    @FXML private TextField calleField;
    @FXML private TextField coloniaField;
    @FXML private ComboBox<String> municipioComboBox;
    @FXML private ComboBox<String> ciudadComboBox;
    @FXML private TextField codigoPostalField;
    @FXML private PasswordField nuevaPasswordField;
    @FXML private TextField nuevaPasswordVisibleField;
    @FXML private CheckBox mostrarPasswordCheckBox;
    @FXML private Label mensajeLabel;
    @FXML private Label errorLabel;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;
    @FXML private Button volverButton;

    private EmpresaService empresaService = new EmpresaService();
    private MunicipioService municipioService = new MunicipioService();
    private CiudadService ciudadService = new CiudadService();
    private SectorActividadService sectorActividadService = new SectorActividadService();

    private Empresa empresaOriginal;

    @FXML
    public void initialize() {
        configurarCombos();
        cargarDatosEmpresa();
        configurarValidadores();
    }

    private void configurarCombos() {
        // Tipo de empresa
        tipoEmpresaComboBox.getItems().addAll(
                "Persona Física", "Persona Moral", "Sociedad Anónima",
                "Sociedad de Responsabilidad Limitada", "Empresa Individual"
        );

        // Sector de actividad
        try {
            for (SectorActividad s : sectorActividadService.obtenerTodosSectores()) {
                sectorActividadComboBox.getItems().add(s.getTipoSectorActividad());
            }
        } catch (Exception e) {
            sectorActividadComboBox.getItems().addAll(
                    "Agricultura, ganadería y pesca", "Industria manufacturera", "Comercio",
                    "Servicios", "Tecnología e informática", "Construcción", "Salud",
                    "Educación", "Finanzas y seguros", "Transporte y logística", "Turismo y hospedaje"
            );
        }

        // Municipio
        try {
            for (Municipio m : municipioService.obtenerTodosMunicipios()) {
                municipioComboBox.getItems().add(m.getNombreMunicipio());
            }
        } catch (Exception e) {
            municipioComboBox.getItems().addAll("Comondú", "La Paz", "Loreto", "Los Cabos", "Mulegé");
        }

        municipioComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                cargarCiudadesPorMunicipio(newVal);
            }
        });
    }

    private void cargarCiudadesPorMunicipio(String nombreMunicipio) {
        ciudadComboBox.getItems().clear();
        if (nombreMunicipio == null) return;

        try {
            Municipio municipio = municipioService.obtenerMunicipioPorNombre(nombreMunicipio);
            if (municipio != null) {
                for (Ciudad c : ciudadService.obtenerCiudadesPorMunicipio(municipio)) {
                    ciudadComboBox.getItems().add(c.getNombreCiudad());
                }
            }
        } catch (Exception e) {
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
    }

    private void cargarDatosEmpresa() {
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null && usuario.isEsEmpresa()) {
            empresaOriginal = empresaService.obtenerEmpresaPorEmail(usuario.getEmail());

            if (empresaOriginal != null) {
                nombreEmpresaField.setText(empresaOriginal.getNombreEmpresa());
                razonSocialField.setText(empresaOriginal.getRazonSocial());
                emailField.setText(empresaOriginal.getCorreoElectronico());
                telefonoField.setText(empresaOriginal.getNumTelefono());
                rfcField.setText(empresaOriginal.getRfc());
                calleField.setText(empresaOriginal.getCalle());
                coloniaField.setText(empresaOriginal.getColonia());
                codigoPostalField.setText(empresaOriginal.getCodigoPostal());
                actividadEconomicaArea.setText(empresaOriginal.getActEconomicaPrincipal());

                if (empresaOriginal.getTipoEmpresa() != null) {
                    tipoEmpresaComboBox.setValue(empresaOriginal.getTipoEmpresa());
                }

                if (empresaOriginal.getSectorActividad() != null) {
                    sectorActividadComboBox.setValue(empresaOriginal.getSectorActividad().getTipoSectorActividad());
                }

                if (empresaOriginal.getMunicipio() != null) {
                    municipioComboBox.setValue(empresaOriginal.getMunicipio().getNombreMunicipio());
                    cargarCiudadesPorMunicipio(empresaOriginal.getMunicipio().getNombreMunicipio());
                }

                if (empresaOriginal.getCiudad() != null) {
                    ciudadComboBox.setValue(empresaOriginal.getCiudad().getNombreCiudad());
                }
            }
        }
    }

    private void configurarValidadores() {
        // Validar teléfono (solo números, 10 dígitos)
        telefonoField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telefonoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 10) {
                telefonoField.setText(newVal.substring(0, 10));
            }
        });

        // Validar código postal
        codigoPostalField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                codigoPostalField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 5) {
                codigoPostalField.setText(newVal.substring(0, 5));
            }
        });

        // Validar RFC
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
    private void onGuardarClick() {
        if (!validarCampos()) return;

        try {
            // Actualizar datos de la empresa
            empresaOriginal.setNombreEmpresa(nombreEmpresaField.getText().trim());
            empresaOriginal.setRazonSocial(razonSocialField.getText().trim());
            empresaOriginal.setNumTelefono(telefonoField.getText());
            empresaOriginal.setRfc(rfcField.getText().toUpperCase());
            empresaOriginal.setCalle(calleField.getText());
            empresaOriginal.setColonia(coloniaField.getText());
            empresaOriginal.setCodigoPostal(codigoPostalField.getText());
            empresaOriginal.setActEconomicaPrincipal(actividadEconomicaArea.getText());
            empresaOriginal.setTipoEmpresa(tipoEmpresaComboBox.getValue());

            // Sector actividad
            if (sectorActividadComboBox.getValue() != null) {
                SectorActividad sector = sectorActividadService.obtenerSectorPorNombre(sectorActividadComboBox.getValue());
                empresaOriginal.setSectorActividad(sector);
            }

            // Municipio
            if (municipioComboBox.getValue() != null) {
                Municipio municipio = municipioService.obtenerMunicipioPorNombre(municipioComboBox.getValue());
                empresaOriginal.setMunicipio(municipio);
            }

            // Ciudad
            if (ciudadComboBox.getValue() != null) {
                Ciudad ciudad = ciudadService.obtenerCiudadPorNombre(ciudadComboBox.getValue());
                empresaOriginal.setCiudad(ciudad);
            }

            // Contraseña (solo si se ingresó una nueva)
            String nuevaPassword = nuevaPasswordField.getText();
            if (!nuevaPassword.isEmpty()) {
                if (nuevaPassword.length() >= 6) {
                    empresaOriginal.setContrasena(nuevaPassword);
                }
            }

            // Guardar en BD
            empresaService.actualizarEmpresa(empresaOriginal);

            // Actualizar sesión
            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioActual();
            if (usuarioActual != null) {
                usuarioActual.setNombre(nombreEmpresaField.getText().trim());
                usuarioActual.setTelefono(telefonoField.getText());
                usuarioActual.setRfc(rfcField.getText().toUpperCase());
                usuarioActual.setRazonSocial(razonSocialField.getText().trim());
                usuarioActual.setCalle(calleField.getText());
                usuarioActual.setColonia(coloniaField.getText());
                usuarioActual.setCodigoPostal(codigoPostalField.getText());
                usuarioActual.setTipoEmpresa(tipoEmpresaComboBox.getValue());
                if (sectorActividadComboBox.getValue() != null) {
                    usuarioActual.setSectorActividad(sectorActividadComboBox.getValue());
                }
                usuarioActual.setActividadEconomicaPrincipal(actividadEconomicaArea.getText());
            }

            mostrarMensaje("✅ Datos actualizados correctamente", "exito");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::regresarAEmpresas);
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(this::regresarAEmpresas);
                }
            }).start();

        } catch (Exception e) {
            mostrarMensaje("❌ Error al guardar: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (nombreEmpresaField.getText().isEmpty()) {
            mostrarMensaje("El nombre de la empresa es obligatorio", "error");
            return false;
        }

        if (razonSocialField.getText().isEmpty()) {
            mostrarMensaje("La razón social es obligatoria", "error");
            return false;
        }

        if (rfcField.getText().isEmpty()) {
            mostrarMensaje("El RFC es obligatorio", "error");
            return false;
        }

        if (codigoPostalField.getText().isEmpty() || codigoPostalField.getText().length() != 5) {
            mostrarMensaje("El código postal debe tener 5 dígitos", "error");
            return false;
        }

        if (telefonoField.getText().isEmpty()) {
            mostrarMensaje("El teléfono es obligatorio", "error");
            return false;
        }

        if (!telefonoField.getText().matches("\\d{10}")) {
            mostrarMensaje("El teléfono debe tener 10 dígitos", "error");
            return false;
        }

        if (tipoEmpresaComboBox.getValue() == null) {
            mostrarMensaje("Selecciona el tipo de empresa", "error");
            return false;
        }

        if (sectorActividadComboBox.getValue() == null) {
            mostrarMensaje("Selecciona el sector de actividad", "error");
            return false;
        }

        if (actividadEconomicaArea.getText().isEmpty()) {
            mostrarMensaje("La actividad económica principal es obligatoria", "error");
            return false;
        }

        if (municipioComboBox.getValue() == null) {
            mostrarMensaje("Selecciona el municipio", "error");
            return false;
        }

        if (ciudadComboBox.getValue() == null) {
            mostrarMensaje("Selecciona la ciudad", "error");
            return false;
        }

        String nuevaPassword = nuevaPasswordField.getText();
        if (!nuevaPassword.isEmpty() && nuevaPassword.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres", "error");
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
        regresarAEmpresas();
    }

    @FXML
    private void onRegresarClick() {
        regresarAEmpresas();
    }

    private void regresarAEmpresas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trabajos/Empresas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Panel de Empresas");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}