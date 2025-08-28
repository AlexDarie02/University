package mcv.control;

import mcv.factory.NotificaFactory;
import mcv.model.*;
import mcv.factory.ServiceFactory;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssunzioniController {

    @FXML  TableView<Assunzione> assunzioniTable;
    @FXML  TableColumn<Assunzione, String> farmacoColumn;
    @FXML  TableColumn<Assunzione, LocalDateTime> dataOraColumn;
    @FXML  TableColumn<Assunzione, String> dosaggioColumn;

    @FXML  ComboBox<String> farmacoComboBox;
    @FXML  DatePicker dataPicker;
    @FXML  Spinner<Integer> oreSpinner;
    @FXML  Spinner<Integer> minutiSpinner;
    @FXML  ComboBox<String> dosaggioComboBox;
    @FXML  TextArea infoAggiuntiveTextArea;
    @FXML  Button indietroButton;

    protected Paziente currentPaziente;
    protected AssunzioneService assunzioneService;
    protected PrescrizioneService prescrizioneService;
    protected List<Prescrizione> prescrizioniCorrenti;
    protected ServiceFactory serviceFactory;
    protected PazienteManager pazienteManager;


    @FXML
    public void initialize() {
        serviceFactory = ServiceFactory.getInstance();
        assunzioneService = serviceFactory.getAssunzioneService();
        prescrizioneService = serviceFactory.getPrescrizioneService();
        pazienteManager = serviceFactory.getPazienteManager();

        farmacoColumn.setCellValueFactory(cellData -> cellData.getValue().farmacoProperty());
        dosaggioColumn.setCellValueFactory(cellData -> cellData.getValue().dosaggioProperty());
        dataOraColumn.setCellValueFactory(cellData -> cellData.getValue().dataOraProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dataOraColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });

        farmacoComboBox.setOnAction(e -> onFarmacoSelezionato());

        SpinnerValueFactory<Integer> oreFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour());
        oreFactory.setWrapAround(true);
        oreSpinner.setValueFactory(oreFactory);
        oreSpinner.setEditable(true);

        SpinnerValueFactory<Integer> minutiFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute());
        minutiFactory.setWrapAround(true);
        minutiSpinner.setValueFactory(minutiFactory);
        minutiSpinner.setEditable(true);
    }

    public void setPaziente(Paziente paziente) {
        this.currentPaziente = paziente;
        serviceFactory = ServiceFactory.getInstance();
        this.assunzioneService = serviceFactory.getAssunzioneService();
        this.prescrizioneService = serviceFactory.getPrescrizioneService();
        assunzioneService.controllaInattivitaAssunzioni(paziente.getCodiceFiscale());

        caricaAssunzioni();
        caricaFarmaciPrescritti();
    }

    private void caricaAssunzioni() {
        List<Assunzione> lista = assunzioneService.getAssunzioniPerPaziente(currentPaziente.getCodiceFiscale());
        assunzioniTable.setItems(FXCollections.observableArrayList(lista));
    }

    private void caricaFarmaciPrescritti() {
        prescrizioniCorrenti = prescrizioneService.getPrescrizioniPerPaziente(currentPaziente.getCodiceFiscale());
        ObservableList<String> farmaci = FXCollections.observableArrayList();
        for (Prescrizione p : prescrizioniCorrenti) {
            farmaci.add(p.getNomeFarmaco());
        }
        farmacoComboBox.setItems(farmaci);
    }

    @FXML
    public void onFarmacoSelezionato() {
        String farmacoSelezionato = farmacoComboBox.getValue();
        ObservableList<String> dosaggi = FXCollections.observableArrayList();
        for (Prescrizione p : prescrizioniCorrenti) {
            if (p.getNomeFarmaco().equals(farmacoSelezionato)) {
                dosaggi.add(p.getDosaggio());
            }
        }
        dosaggioComboBox.setItems(dosaggi);
    }

    @FXML
    protected void handleRegistraAssunzione() {
        try {
            String farmaco = farmacoComboBox.getValue();
            String dosaggio = dosaggioComboBox.getValue();
            LocalDate data = dataPicker.getValue();
            Integer oraVal = oreSpinner.getValue();
            Integer minutiVal = minutiSpinner.getValue();

            if (farmaco == null || dosaggio == null || data == null || oraVal == null || minutiVal == null) {
                mostraAlert(AlertType.WARNING, "Dati mancanti", "Compila tutti i campi.");
                return;
            }

            LocalTime ora = LocalTime.of(oraVal, minutiVal);
            LocalDateTime dataOra = LocalDateTime.of(data, ora);

            Assunzione a = new Assunzione(currentPaziente.getCodiceFiscale(), farmaco, dosaggio, dataOra);
            assunzioneService.salvaAssunzione(a);

            mostraAlert(AlertType.INFORMATION, "Successo", "Assunzione registrata");
            caricaAssunzioni();

        } catch (Exception e) {
            mostraAlert(AlertType.ERROR, "Errore", "Verifica i dati inseriti: " + e.getMessage());
        }
    }

    @FXML
    protected void handleSegnalaTerapiaExtra() {
        String info = infoAggiuntiveTextArea.getText();
        if (info != null && !info.isEmpty()) {
            boolean success = pazienteManager.aggiornaInfoAggiuntive(
                    currentPaziente.getCodiceFiscale(), info
            );

            if (success) {
                Notifica notifica = NotificaFactory.creaTerapiaExtra(
                        currentPaziente.getCodiceFiscale(), info, currentPaziente.getMedicoRiferimento().getCodiceFiscale()
                );
                NotificaHandler.inviaNotifica(notifica);

                mostraAlert(AlertType.INFORMATION, "Segnalazione inviata", "La terapia extra è stata segnalata.");
                infoAggiuntiveTextArea.clear();
            } else {
                mostraAlert(AlertType.ERROR, "Errore", "Non è stato possibile salvare le informazioni");
            }
        } else {
            mostraAlert(AlertType.WARNING, "Campo vuoto", "Inserisci informazioni sulla terapia extra.");
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePazienteScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostraAlert(AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}
