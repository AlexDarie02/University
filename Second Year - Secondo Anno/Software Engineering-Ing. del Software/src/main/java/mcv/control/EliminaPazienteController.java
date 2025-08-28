package mcv.control;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import mcv.model.PazienteManager;
import mcv.model.SegreteriaService;
import mcv.factory.ServiceFactory;

import java.sql.SQLException;
import java.util.List;

public class EliminaPazienteController {

    @FXML private TextField inputTextField;
    @FXML private ListView<String> suggerimentiListView;
    @FXML private Button eliminaButton;

    private SegreteriaService segreteriaService;
    private PazienteManager pazienteManager;

    public void setSegreteriaService(SegreteriaService segreteriaService) {
        this.segreteriaService = segreteriaService;
    }

    @FXML
    private void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.pazienteManager = factory.getPazienteManager();
        inputTextField.textProperty().addListener((oldVal, Val, newVal) -> {
            if (newVal.length() >= 1) {
                try {
                    List<String> risultati = pazienteManager.suggerisciPazienti(newVal);
                    suggerimentiListView.setItems(FXCollections.observableArrayList(risultati));
                } catch (Exception e) {
                    suggerimentiListView.getItems().clear();
                }
            } else {
                suggerimentiListView.getItems().clear();
            }
        });

        suggerimentiListView.setOnMouseClicked(this::gestisciSelezioneSuggerimento);
        eliminaButton.setOnAction(e -> handleElimina());
    }

    private void gestisciSelezioneSuggerimento(MouseEvent event) {
        String selezionato = suggerimentiListView.getSelectionModel().getSelectedItem();
        if (selezionato != null) {
            inputTextField.setText(selezionato);
            suggerimentiListView.getItems().clear();
        }
    }

    private void handleElimina() {
        String codiceFiscale = inputTextField.getText();

        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Campo vuoto", "Inserisci un codice fiscale da eliminare.");
            return;
        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di voler eliminare il paziente con CF: " + codiceFiscale + "?",
                ButtonType.YES, ButtonType.NO);
        conferma.setTitle("Conferma Eliminazione");

        conferma.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    segreteriaService.eliminaPaziente(codiceFiscale.trim());
                    mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Paziente eliminato: " + codiceFiscale);
                    inputTextField.clear();
                } catch (SQLException e) {
                    mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore durante l'eliminazione: " + e.getMessage());
                }
            }
        });
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Eliminato");
        alert.setHeaderText(null);
        alert.setContentText("Paziente eliminato con successo");
        alert.showAndWait();

    }
}
