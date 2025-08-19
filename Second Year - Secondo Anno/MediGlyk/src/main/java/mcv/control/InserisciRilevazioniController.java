package mcv.control;

import mcv.model.Paziente;
import mcv.model.Rilevazione;
import mcv.session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import mcv.factory.ServiceFactory;
import mcv.model.CheckRilevazione;

public class InserisciRilevazioniController {

    @FXML private DatePicker rilevazioneCalendar;
    @FXML private Slider prepastoSlider;
    @FXML private Slider postpastoSlider;
    @FXML private RadioButton ColazioneRadioButton;
    @FXML private RadioButton PranzoRadioButton;
    @FXML private RadioButton CenaRadioButton;
    @FXML private Label prePastoSliderLabel;
    @FXML private Label postPastoSliderLabel;
    private final ToggleGroup tipoPastoGroup = new ToggleGroup();

    private Paziente paziente;
    private CheckRilevazione checkRilevazione;

    @FXML
    private void initialize() {
        rilevazioneCalendar.setValue(LocalDate.now());
        paziente = (Paziente) UserSession.getInstance().getLoggedInUser();

        prepastoSlider.setValue(100);
        postpastoSlider.setValue(160);

        prePastoSliderLabel.setText(String.valueOf((int) prepastoSlider.getValue()));
        postPastoSliderLabel.setText(String.valueOf((int) postpastoSlider.getValue()));

        prepastoSlider.valueProperty().addListener((oldVal, Val, newVal) -> {
            prePastoSliderLabel.setText(String.valueOf(newVal.intValue()));
        });

        postpastoSlider.valueProperty().addListener((oldVal, Val, newVal) -> {
            postPastoSliderLabel.setText(String.valueOf(newVal.intValue()));
        });

        ColazioneRadioButton.setToggleGroup(tipoPastoGroup);
        PranzoRadioButton.setToggleGroup(tipoPastoGroup);
        CenaRadioButton.setToggleGroup(tipoPastoGroup);

        checkRilevazione = ServiceFactory.getInstance().getCheckRilevazione();
    }

    private String getTipoPastoSelezionato() {
        if (ColazioneRadioButton.isSelected()) return "COLAZIONE";
        if (PranzoRadioButton.isSelected()) return "PRANZO";
        if (CenaRadioButton.isSelected()) return "CENA";
        return null;
    }

    @FXML
    private void handleInserisciPrePasto() {
        try {
            String tipoPasto = getTipoPastoSelezionato();
            if (tipoPasto == null) {
                mostraAlert(AlertType.WARNING, "Tipo pasto mancante", "Seleziona colazione, pranzo o cena.");
                return;
            }

            LocalDateTime dataOra = LocalDateTime.of(rilevazioneCalendar.getValue(), LocalTime.now());
            int valore = (int) prepastoSlider.getValue();

            Rilevazione rilevazione = new Rilevazione(
                    paziente.getCodiceFiscale(), valore, dataOra, "PREPASTO", tipoPasto
            );

            boolean ok = checkRilevazione.insertRilevazione(rilevazione);


            if (ok) {
                mostraAlert(AlertType.INFORMATION, "Inserimento riuscito", "La rilevazione è stata inserita con successo.");
            } else {
                mostraAlert(AlertType.WARNING, "Valore anomalo", "Fuori range. Notifica inviata al medico.");
            }

        } catch (Exception e) {
            mostraAlert(AlertType.ERROR, "Errore", e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleInserisciPostPasto() {
        try {
            String tipoPasto = getTipoPastoSelezionato();
            if (tipoPasto == null) {
                mostraAlert(AlertType.WARNING, "Tipo pasto mancante", "Seleziona colazione, pranzo o cena.");
                return;
            }

            LocalDateTime dataOra = LocalDateTime.of(rilevazioneCalendar.getValue(), LocalTime.now());
            int valore = (int) postpastoSlider.getValue();

            Rilevazione prePasto = checkRilevazione.getUltimoPrePastoSePresente(
                    paziente.getCodiceFiscale(), rilevazioneCalendar.getValue(), tipoPasto
            );


            if (prePasto == null) {
                mostraAlert(AlertType.WARNING, "Pre-pasto mancante", "Non esiste rilevazione pre-pasto per " + tipoPasto);
                return;
            }

            if (rilevazioneCalendar.getValue().isEqual(LocalDate.now())) {
                if (!checkRilevazione.controllaOraPostPasto(prePasto.getDataOra(), dataOra)) {
                    mostraAlert(AlertType.WARNING, "Troppo presto!", "Aspettare almeno due ore dalla rilevazione pre-pasto.");
                    return;
                }
            }


            Rilevazione rilevazione = new Rilevazione(
                    paziente.getCodiceFiscale(), valore, dataOra, "POSTPASTO", tipoPasto
            );

            boolean ok = checkRilevazione.insertRilevazione(rilevazione);



            if (ok) {
                mostraAlert(AlertType.INFORMATION, "Inserimento riuscito", "La rilevazione è stata inserita con successo.");
            } else {
                mostraAlert(AlertType.WARNING, "Valore anomalo", "Fuori range. Notifica inviata al medico.");
            }

        } catch (Exception e) {
            mostraAlert(AlertType.ERROR, "Errore", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostraAlert(AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePazienteScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore qua");
        }
    }

}
