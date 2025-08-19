package mcv.control;

import mcv.model.Medico;
import mcv.model.Prescrizione;
import mcv.model.PrescrizioneService;
import mcv.model.Paziente;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mcv.session.UserSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PrescrizioniMedicoController {

    @FXML private TableView<PrescrizioneTableModel> prescrizioniTable;
    @FXML private TableColumn<PrescrizioneTableModel, String> farmacoColumn;
    @FXML private TableColumn<PrescrizioneTableModel, String> dosaggioColumn;
    @FXML private TableColumn<PrescrizioneTableModel, String> frequenzaColumn;
    @FXML private TableColumn<PrescrizioneTableModel, String> indicazioniColumn;
    @FXML private TableColumn<PrescrizioneTableModel, LocalDate> dataColumn;

    @FXML private TextField farmacoField;
    @FXML private TextField dosaggioField;
    @FXML private TextField frequenzaField;
    @FXML private TextField indicazioniField;
    @FXML private Button salvaButton;

    private Paziente paziente;
    private PrescrizioneService service;
    private final ObservableList<PrescrizioneTableModel> prescrizioniData = FXCollections.observableArrayList();

    private PrescrizioneTableModel selezionata = null;

    private Medico medicoLoggato;

    public void initialize(Paziente paziente, PrescrizioneService service) {
        this.paziente = paziente;
        this.service = service;
        this.medicoLoggato =  (Medico) UserSession.getInstance().getLoggedInUser();;
        caricaPrescrizioni();
    }

    public static class PrescrizioneTableModel {
        private final int id;
        private final StringProperty nomeFarmaco;
        private final StringProperty dosaggio;
        private final StringProperty frequenza;
        private final StringProperty indicazioni;
        private final ObjectProperty<LocalDate> dataPrescrizione;

        public PrescrizioneTableModel(int id, String nomeFarmaco, String dosaggio,
                                      String frequenza, String indicazioni, LocalDate dataPrescrizione) {
            this.id = id;
            this.nomeFarmaco = new SimpleStringProperty(nomeFarmaco);
            this.dosaggio = new SimpleStringProperty(dosaggio);
            this.frequenza = new SimpleStringProperty(frequenza);
            this.indicazioni = new SimpleStringProperty(indicazioni);
            this.dataPrescrizione = new SimpleObjectProperty<>(dataPrescrizione);
        }

        public int getId() { return id; }
        public String getNomeFarmaco() { return nomeFarmaco.get(); }
        public String getDosaggio() { return dosaggio.get(); }
        public String getFrequenza() { return frequenza.get(); }
        public String getIndicazioni() { return indicazioni.get(); }
        public LocalDate getDataPrescrizione() { return dataPrescrizione.get(); }

        public StringProperty nomeFarmacoProperty() { return nomeFarmaco; }
        public StringProperty dosaggioProperty() { return dosaggio; }
        public StringProperty frequenzaProperty() { return frequenza; }
        public StringProperty indicazioniProperty() { return indicazioni; }
        public ObjectProperty<LocalDate> dataPrescrizioneProperty() { return dataPrescrizione; }
    }

    @FXML
    public void initialize() {
        farmacoColumn.setCellValueFactory(cell -> cell.getValue().nomeFarmacoProperty());
        dosaggioColumn.setCellValueFactory(cell -> cell.getValue().dosaggioProperty());
        frequenzaColumn.setCellValueFactory(cell -> cell.getValue().frequenzaProperty());
        indicazioniColumn.setCellValueFactory(cell -> cell.getValue().indicazioniProperty());
        dataColumn.setCellValueFactory(cell -> cell.getValue().dataPrescrizioneProperty());

        prescrizioniTable.setItems(prescrizioniData);

        prescrizioniTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selezionata = newSel;
                farmacoField.setText(newSel.getNomeFarmaco());
                dosaggioField.setText(newSel.getDosaggio());
                frequenzaField.setText(newSel.getFrequenza());
                indicazioniField.setText(newSel.getIndicazioni());

                salvaButton.setText("Modifica Terapia");
            }
        });

    }

    public void setPaziente(Paziente p) {
        this.paziente = p;
        caricaPrescrizioni();
    }

    private void caricaPrescrizioni() {
        prescrizioniData.clear();
        List<Prescrizione> lista = service.getPrescrizioniPerPaziente(paziente.getCodiceFiscale());
        for (Prescrizione p : lista) {
            prescrizioniData.add(new PrescrizioneTableModel(
                    p.getId(),
                    p.getNomeFarmaco(),
                    p.getDosaggio(),
                    p.getFrequenza(),
                    p.getIndicazioni(),
                    p.getDataPrescrizione()
            ));
        }
    }

    @FXML
    private void handleSalva() {
        String farmaco = farmacoField.getText();
        String dosaggio = dosaggioField.getText();
        String frequenza = frequenzaField.getText();
        String indicazioni = indicazioniField.getText();

        if (farmaco.isEmpty() || dosaggio.isEmpty() || frequenza.isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Inserisci tutti i campi obbligatori (farmaco, dosaggio, frequenza).");
            return;
        }

        if (selezionata == null) {
            Prescrizione nuova = service.aggiungiPrescrizione(
                    paziente.getCodiceFiscale(), farmaco, dosaggio, frequenza, indicazioni, medicoLoggato);
            if (nuova != null) {
                prescrizioniData.add(new PrescrizioneTableModel(
                        nuova.getId(),
                        nuova.getNomeFarmaco(),
                        nuova.getDosaggio(),
                        nuova.getFrequenza(),
                        nuova.getIndicazioni(),
                        nuova.getDataPrescrizione()
                ));
            } else {
                mostraAlert(Alert.AlertType.ERROR, "Errore durante il salvataggio della prescrizione.");
            }

        } else {
            Prescrizione modificata = new Prescrizione(
                    selezionata.getId(), farmaco, dosaggio, frequenza, indicazioni,
                     selezionata.getDataPrescrizione()
            );
            boolean ok = service.aggiornaPrescrizione(modificata, paziente.getCodiceFiscale(), medicoLoggato);
            if (ok) caricaPrescrizioni();
        }

        pulisciCampi();
    }

    private void pulisciCampi() {
        farmacoField.clear();
        dosaggioField.clear();
        frequenzaField.clear();
        indicazioniField.clear();
        selezionata = null;
        prescrizioniTable.getSelectionModel().clearSelection();
        salvaButton.setText("Salva Terapia");
    }

    @FXML
    private void handleElimina() {
        if (selezionata == null) {
            mostraAlert(Alert.AlertType.WARNING, "Seleziona una prescrizione da eliminare.");
            return;
        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma eliminazione");
        conferma.setHeaderText("Eliminare la terapia selezionata?");
        conferma.setContentText("Questa azione non può essere annullata.");

        conferma.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = service.eliminaPrescrizione(selezionata.getId(), paziente.getCodiceFiscale(), medicoLoggato);
                if (success) {
                    prescrizioniData.remove(selezionata);
                    pulisciCampi();
                    mostraAlert(Alert.AlertType.INFORMATION, "Prescrizione eliminata con successo.");
                } else {
                    mostraAlert(Alert.AlertType.ERROR, "Errore durante l'eliminazione.");
                }
            }
        });
    }


    private void mostraAlert(Alert.AlertType tipo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettagliPazienteScene.fxml"));
            Parent root = loader.load();

            DettagliPazienteController controller = loader.getController();

            controller.setPaziente(paziente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore qua");
        }
    }
}
