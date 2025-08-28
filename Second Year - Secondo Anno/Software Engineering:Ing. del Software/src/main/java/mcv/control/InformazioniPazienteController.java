package mcv.control;

import mcv.model.Medico;
import mcv.model.Paziente;
import mcv.model.PazienteManager;
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
import java.util.Arrays;
import java.util.List;

public class InformazioniPazienteController {

    @FXML private CheckBox checkFumatore;
    @FXML private CheckBox checkExFumatore;
    @FXML private CheckBox checkAlcol;
    @FXML private CheckBox checkStupefacenti;
    @FXML private CheckBox checkObesita;

    @FXML private TextArea txtPatologiePregresse;
    @FXML private TextArea txtComorbidita;

    private PazienteManager pazienteManager;
    private Paziente pazienteCorrente;

    private Medico medicoLoggato;

    public void initialize(Paziente paziente, PazienteManager manager) {
        this.pazienteCorrente = paziente;
        this.pazienteManager = manager;
        this.medicoLoggato = (Medico) UserSession.getInstance().getLoggedInUser();
        caricaDatiPaziente();
    }


    private void caricaDatiPaziente() {
        if (pazienteCorrente == null) return;

        List<String> fattoriSelezionati = Arrays.asList(
                pazienteCorrente.getFattoriRischio() != null ?
                        pazienteCorrente.getFattoriRischio().split(",") :
                        new String[]{});

        checkFumatore.setSelected(fattoriSelezionati.contains("Fumatore"));
        checkExFumatore.setSelected(fattoriSelezionati.contains("Ex-fumatore"));
        checkAlcol.setSelected(fattoriSelezionati.contains("Dipendenza da alcol"));
        checkStupefacenti.setSelected(fattoriSelezionati.contains("Dipendenza da stupefacenti"));
        checkObesita.setSelected(fattoriSelezionati.contains("Obesità"));

        txtPatologiePregresse.setText(
                pazienteCorrente.getPatologiePregresse() != null ?
                        pazienteCorrente.getPatologiePregresse() : "");
        txtComorbidita.setText(
                pazienteCorrente.getComorbidita() != null ?
                        pazienteCorrente.getComorbidita() : "");
    }

    @FXML
    private void handleSalva() {
        if (pazienteCorrente == null) {
            mostraErrore("Nessun paziente caricato.");
            return;
        }

        StringBuilder sbFattori = new StringBuilder();
        if (checkFumatore.isSelected()) sbFattori.append("Fumatore,");
        if (checkExFumatore.isSelected()) sbFattori.append("Ex-fumatore,");
        if (checkAlcol.isSelected()) sbFattori.append("Dipendenza da alcol,");
        if (checkStupefacenti.isSelected()) sbFattori.append("Dipendenza da stupefacenti,");
        if (checkObesita.isSelected()) sbFattori.append("Obesità,");

        String fattoriRischio = sbFattori.length() > 0 ? sbFattori.substring(0, sbFattori.length() - 1) : "";
        String patologie = txtPatologiePregresse.getText();
        String comorbidita = txtComorbidita.getText();

        try {
            boolean successo = pazienteManager.aggiornaInformazioniSanitarie(
                    pazienteCorrente.getCodiceFiscale(),
                    fattoriRischio,
                    patologie,
                    comorbidita,
                    medicoLoggato
            );

            if (successo) {
                mostraInfo("Informazioni sanitarie aggiornate con successo.");
            } else {
                mostraErrore("Errore nell'aggiornamento delle informazioni.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraInfo(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettagliPazienteScene.fxml"));
            Parent root = loader.load();

            DettagliPazienteController controller = loader.getController();
            controller.setPaziente(pazienteCorrente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore qua");
        }
    }

}
