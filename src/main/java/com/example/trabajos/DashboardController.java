package com.example.trabajos;

import com.example.trabajos.models.Postulacion;
import com.example.trabajos.services.PostulacionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private Button volverButton;

    @FXML private Label bienvenidoLabel;

    @FXML private Label vistasLabel;
    @FXML private Label sesionesLabel;
    @FXML private Label formulariosLabel;
    @FXML private Label postulacionesLabel;

    @FXML private StackPane chartContainer;
    @FXML private ComboBox<String> periodoComboBox;

    @FXML private TableView<Postulacion> postulacionesTable;
    @FXML private TableColumn<Postulacion, String> colNombre;
    @FXML private TableColumn<Postulacion, String> colEdad;
    @FXML private TableColumn<Postulacion, String> colTrabajo;
    @FXML private TableColumn<Postulacion, String> colEstado;

    @FXML private TableView<Postulacion> ofertasTable;
    @FXML private TableColumn<Postulacion, String> colNombre2;
    @FXML private TableColumn<Postulacion, String> colEdad2;
    @FXML private TableColumn<Postulacion, String> colTrabajo2;
    @FXML private TableColumn<Postulacion, String> colEstado2;

    private final PostulacionService postulacionService = new PostulacionService();

    @FXML
    public void initialize() {
        // Inicializar contadores en 0
        setLabel(vistasLabel,       "0");
        setLabel(sesionesLabel,     "0");
        setLabel(formulariosLabel,  "0");
        setLabel(postulacionesLabel,"0");

        configurarBienvenida();
        configurarTablas();
        cargarDatos();
        generarGrafica();

        if (periodoComboBox != null) {
            periodoComboBox.setValue("Este trimestre");
            periodoComboBox.setOnAction(e -> generarGrafica());
        }
    }

    private void setLabel(Label label, String value) {
        if (label != null) label.setText(value);
    }

    private void configurarBienvenida() {
        if (bienvenidoLabel == null) return;
        Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
        if (usuario != null) {
            bienvenidoLabel.setText("Bienvenido, " + usuario.getEmail());
        } else {
            bienvenidoLabel.setText("Bienvenido");
        }
    }

    private void configurarTablas() {
        if (colNombre  != null) colNombre .setCellValueFactory(new PropertyValueFactory<>("nombreTrabajador"));
        if (colEdad    != null) colEdad   .setCellValueFactory(new PropertyValueFactory<>("idTrabajador"));
        if (colTrabajo != null) colTrabajo.setCellValueFactory(new PropertyValueFactory<>("nombreOferta"));
        if (colEstado  != null) colEstado .setCellValueFactory(new PropertyValueFactory<>("estadoPostulacion"));

        if (colNombre2  != null) colNombre2 .setCellValueFactory(new PropertyValueFactory<>("nombreTrabajador"));
        if (colEdad2    != null) colEdad2   .setCellValueFactory(new PropertyValueFactory<>("idTrabajador"));
        if (colTrabajo2 != null) colTrabajo2.setCellValueFactory(new PropertyValueFactory<>("nombreOferta"));
        if (colEstado2  != null) colEstado2 .setCellValueFactory(new PropertyValueFactory<>("estadoPostulacion"));
    }

    private void cargarDatos() {
        try {
            Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
            List<Postulacion> postulaciones = null;

            if (usuario != null) {
                postulaciones = postulacionService.obtenerPostulacionesPorEmpresa(usuario.getEmail());
            }

            int total = (postulaciones != null) ? postulaciones.size() : 0;

            setLabel(postulacionesLabel, String.valueOf(total));
            setLabel(formulariosLabel,   String.valueOf(total));
            setLabel(vistasLabel,        "0");
            setLabel(sesionesLabel,      "0");

            if (postulaciones != null && !postulaciones.isEmpty()) {
                ObservableList<Postulacion> lista = FXCollections.observableArrayList(
                        postulaciones.subList(0, Math.min(10, postulaciones.size()))
                );
                if (postulacionesTable != null) postulacionesTable.setItems(lista);
                if (ofertasTable != null)       ofertasTable.setItems(lista);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Dashboard: no se pudieron cargar datos — " + e.getMessage());
        }
    }

    private void generarGrafica() {
        if (chartContainer == null) return;

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Período");
        yAxis.setLabel("Cantidad");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Actividad Reciente");
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Postulaciones");

        String[] etiquetas = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};
        try {
            Usuario usuario = SesionManager.getInstancia().getUsuarioActual();
            List<Postulacion> postulaciones = (usuario != null)
                    ? postulacionService.obtenerPostulacionesPorEmpresa(usuario.getEmail())
                    : null;

            if (postulaciones != null && !postulaciones.isEmpty()) {
                int[] meses = new int[6];
                for (int i = 0; i < postulaciones.size(); i++) meses[i % 6]++;
                for (int i = 0; i < 6; i++) serie.getData().add(new XYChart.Data<>(etiquetas[i], meses[i]));
            } else {
                for (String mes : etiquetas) serie.getData().add(new XYChart.Data<>(mes, 0));
            }
        } catch (Exception e) {
            for (String mes : etiquetas) serie.getData().add(new XYChart.Data<>(mes, 0));
        }

        barChart.getData().add(serie);
        barChart.setPrefHeight(chartContainer.getPrefHeight());
        barChart.setPrefWidth(chartContainer.getPrefWidth());

        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
    }

    @FXML
    private void onIrAlPanel() {
        try {
            boolean esEmpresa = SesionManager.getInstancia().esEmpresa();
            String fxml  = esEmpresa ? "/com/example/trabajos/Empresas.fxml" : "/com/example/trabajos/Trabajos.fxml";
            String titulo = esEmpresa ? "Panel de Empresas" : "Buscar Trabajos";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);
        } catch (IOException e) {
            mostrarError("No se pudo navegar al panel: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
