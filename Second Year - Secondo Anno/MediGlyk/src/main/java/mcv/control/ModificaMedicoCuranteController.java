package mcv.control;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mcv.model.MedicoManager;
import mcv.model.PazienteManager;
import mcv.factory.ServiceFactory;

import java.sql.SQLException;
import java.util.List;

public class ModificaMedicoCuranteController {

    @FXML private TextField cfPazienteField;
    @FXML private ListView<String> suggerimentiPaziente;

    @FXML private TextField cfMedicoField;
    @FXML private ListView<String> suggerimentiMedico;

    private PazienteManager pazienteManager;
    private MedicoManager medicoManager;

    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.pazienteManager = factory.getPazienteManager();
        this.medicoManager = factory.getMedicoManager();

        cfPazienteField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                List<String> risultati = pazienteManager.suggerisciPazienti(newVal);
                suggerimentiPaziente.setItems(FXCollections.observableArrayList(risultati));
            } else {
                suggerimentiPaziente.getItems().clear();
            }
        });

        suggerimentiPaziente.setOnMouseClicked(e -> {
            String selezionato = suggerimentiPaziente.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                cfPazienteField.setText(selezionato);
                suggerimentiPaziente.getItems().clear();
            }
        });

        cfMedicoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                List<String> risultati = medicoManager.suggerisciCodiciFiscali(newVal);
                suggerimentiMedico.setItems(FXCollections.observableArrayList(risultati));
            } else {
                suggerimentiMedico.getItems().clear();
            }
        });

        suggerimentiMedico.setOnMouseClicked(e -> {
            String selezionato = suggerimentiMedico.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                cfMedicoField.setText(selezionato);
                suggerimentiMedico.getItems().clear();
            }
        });
    }

    @FXML
    private void handleAggiorna() {
        String cfPaz = cfPazienteField.getText().trim();
        String cfMed = cfMedicoField.getText().trim();

        if (cfPaz.isEmpty() || cfMed.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Inserisci entrambi i codici fiscali.");
            return;
        }

        try {
            boolean aggiornato = pazienteManager.aggiornaMedicoCurante(cfPaz, cfMed);
            if (aggiornato) {
                showAlert(Alert.AlertType.INFORMATION, "Successo", "Medico curante aggiornato.");
                ((Stage) cfPazienteField.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Errore", "Paziente non trovato o medico inesistente.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Errore DB", "Errore: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }

}
