package com.example.trabajos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {

    @FXML
    protected void onBuscarEmpleoClick(javafx.event.ActionEvent event) {
        abrirPantallaSesion(false, event);
    }

    @FXML
    protected void onOfrecerTrabajoClick(javafx.event.ActionEvent event) {
        abrirPantallaSesion(true, event);
    }

    private void abrirPantallaSesion(boolean esEmpresa, javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Sesion.fxml"));
            Parent root = fxmlLoader.load();

            SesionController controller = fxmlLoader.getController();
            controller.setTipoUsuario(esEmpresa);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Iniciar Sesión");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}