package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class EmpresasController {

    @FXML
    private void onCrearFormularioClick(javafx.event.ActionEvent event) {
        System.out.println("=== Abriendo Formulario.fxml ===");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/trabajos/Formulario.fxml"));

            Parent root = loader.load();
            System.out.println("✔ FXML cargado exitosamente");

            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Llenar Formulario");
            currentStage.setMaximized(true);

            System.out.println("✔ Ventana actualizada exitosamente");

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario");
            alert.setContentText("Detalle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void onAbrirFormularioClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/FormulariosTable.fxml"));
            Parent root = fxmlLoader.load();

            FormulariosTableController controller = fxmlLoader.getController();
            controller.refrescarTabla();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Mis Ofertas de Trabajo");

            System.out.println("✔ FormulariosTable.fxml cargado correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ ERROR al abrir FormulariosTable.fxml");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la vista");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void onTrabajadoresDisponiblesClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/TrabajadoresDisponibles.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Trabajadores Disponibles");

            System.out.println("✔ TrabajadoresDisponibles.fxml cargado correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ ERROR al abrir TrabajadoresDisponibles.fxml");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    protected void onVolverClick(javafx.event.ActionEvent event) {
        SesionManager.getInstancia().cerrarSesion();
        abrirVentana("hello-view.fxml", "Sistema de Trabajos", event);
    }

    private void abrirVentana(String fxml, String titulo, javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/trabajos/" + fxml));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle(titulo);

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}