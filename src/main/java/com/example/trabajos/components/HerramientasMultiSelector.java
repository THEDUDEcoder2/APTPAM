package com.example.trabajos.components;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.*;


 //Componente que permite seleccionar múltiples herramientas de un ComboBox
 //y mostrarlas en un TextField separadas por comas.
 //Evita seleccionar herramientas duplicadas.

public class HerramientasMultiSelector extends VBox {

    private TextField herramientasField;
    private ComboBox<String> herramientasComboBox;
    private Set<String> herramientasSeleccionadas;
    private Map<String, List<String>> herramientasPorTrabajo;
    private List<ComboBox<String>> trabajosComboBoxes;

    public HerramientasMultiSelector() {
        herramientasSeleccionadas = new LinkedHashSet<>();
        herramientasPorTrabajo = new HashMap<>();
        trabajosComboBoxes = new ArrayList<>();
        inicializarComponente();
    }

    private void inicializarComponente() {
        setSpacing(8);
        setPadding(new Insets(5, 0, 5, 0));

        // Label
        Label label = new Label("Herramientas que domina (puede seleccionar varias)");
        label.setStyle("-fx-text-fill: #1a3a5c; -fx-font-size: 14; -fx-font-weight: bold;");

        // Campo de texto para mostrar herramientas seleccionadas (no editable)
        herramientasField = new TextField();
        herramientasField.setEditable(false);
        herramientasField.setPromptText("Las herramientas seleccionadas aparecerán aquí");
        herramientasField.setStyle("-fx-background-color: #f8fafd; -fx-border-color: #b8cfe8; -fx-border-radius: 6;");

        // ComboBox para seleccionar herramientas
        herramientasComboBox = new ComboBox<>();
        herramientasComboBox.setPromptText("Seleccione una herramienta...");
        herramientasComboBox.setPrefHeight(32);
        herramientasComboBox.setPrefWidth(400);

        // Listener para agregar herramienta al seleccionar
        herramientasComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                agregarHerramienta(newVal);
                herramientasComboBox.getSelectionModel().clearSelection();
            }
        });

        // HBox para ComboBox y botón de limpiar
        HBox seleccionBox = new HBox(10);
        seleccionBox.setAlignment(Pos.CENTER_LEFT);
        seleccionBox.getChildren().addAll(herramientasComboBox);

        // Botón para limpiar todas las herramientas
        Button limpiarButton = new Button("🗑️ Limpiar todo");
        limpiarButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        limpiarButton.setOnAction(e -> limpiarTodasHerramientas());
        seleccionBox.getChildren().add(limpiarButton);

        getChildren().addAll(label, herramientasField, seleccionBox);
    }

    //Carga las herramientas disponibles según los trabajos seleccionados

    public void actualizarHerramientasDisponibles(List<ComboBox<String>> trabajosComboBoxes,
                                                  Map<String, List<String>> herramientasPorTrabajo) {
        this.herramientasPorTrabajo = herramientasPorTrabajo;
        this.trabajosComboBoxes = trabajosComboBoxes;

        Set<String> herramientasUnicas = new TreeSet<>();

        for (ComboBox<String> combo : trabajosComboBoxes) {
            String trabajo = combo.getValue();
            if (trabajo != null && herramientasPorTrabajo.containsKey(trabajo)) {
                herramientasUnicas.addAll(herramientasPorTrabajo.get(trabajo));
            }
        }

        herramientasComboBox.getItems().clear();
        if (herramientasUnicas.isEmpty()) {
            herramientasComboBox.setPromptText("Primero seleccione uno o más trabajos");
        } else {
            List<String> herramientasList = new ArrayList<>(herramientasUnicas);
            Collections.sort(herramientasList);
            herramientasComboBox.getItems().addAll(herramientasList);
            herramientasComboBox.setPromptText("Seleccione una herramienta...");
        }
    }

    //Agrega una herramienta a la selección si no está ya seleccionada

    private void agregarHerramienta(String herramienta) {
        if (herramientasSeleccionadas.contains(herramienta)) {
            mostrarAlerta("⚠️ Herramienta duplicada",
                    "La herramienta '" + herramienta + "' ya está seleccionada.\n" +
                            "No puede seleccionar la misma herramienta dos veces.");
            return;
        }

        herramientasSeleccionadas.add(herramienta);
        actualizarTextField();
    }

    // Actualiza el TextField con las herramientas seleccionadas

    private void actualizarTextField() {
        if (herramientasSeleccionadas.isEmpty()) {
            herramientasField.setText("");
            herramientasField.setPromptText("Las herramientas seleccionadas aparecerán aquí");
        } else {
            String texto = String.join(", ", herramientasSeleccionadas);
            herramientasField.setText(texto);
        }
    }

    //Elimina una herramienta específica de la selección
    public void eliminarHerramienta(String herramienta) {
        herramientasSeleccionadas.remove(herramienta);
        actualizarTextField();
    }

    //Limpia todas las herramientas seleccionadas
    public void limpiarTodasHerramientas() {
        herramientasSeleccionadas.clear();
        actualizarTextField();
    }

    //Obtiene las herramientas seleccionadas como String separado por comas

    public String getHerramientasComoString() {
        if (herramientasSeleccionadas.isEmpty()) {
            return "";
        }
        return String.join(", ", herramientasSeleccionadas);
    }

    //Establece herramientas desde un String (para editar perfil)

    public void setHerramientasDesdeString(String herramientasStr) {
        herramientasSeleccionadas.clear();
        if (herramientasStr != null && !herramientasStr.isEmpty() && !"No especificada".equals(herramientasStr)) {
            String[] herramientasArray = herramientasStr.split(", ");
            for (String h : herramientasArray) {
                herramientasSeleccionadas.add(h.trim());
            }
        }
        actualizarTextField();
    }

    //Verifica si hay herramientas seleccionadas

    public boolean tieneHerramientas() {
        return !herramientasSeleccionadas.isEmpty();
    }

    //Muestra una alerta simple

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}