package mcv.control;

import mcv.model.*;
import mcv.factory.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mcv.session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DettagliPazienteController implements Initializable {

    @FXML private Label nomePazienteLabel, cfPazienteLabel, dataNascitaLabel, medicoLabel, generePazienteLabel, ulterInfoPazienteLabel;
    @FXML private Label sintomiSegnalatiLabel;
    @FXML private ComboBox<String> periodoComboBox;
    @FXML private LineChart<String, Number> glicemiaChart, insulinaChart;
    @FXML private Button btnInserisciPrescrizione, btnFattoriRischio;

    private Paziente paziente;
    private RilevazioneHandler rilHandler;
    private PrescrizioneService prescrizioneService;
    private PazienteManager pazienteManager;
    private Medico medicoLoggato;
    private ServiceFactory serviceFactory;
    private SintomoManager sintomoManager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        periodoComboBox.getItems().addAll("Ultima settimana", "Ultimo mese", "Ultimi 3 mesi");
        periodoComboBox.setValue("Ultima settimana");
    }

    public void setPaziente(Paziente p) {
        this.paziente = p;
        this.medicoLoggato = (Medico) UserSession.getInstance().getLoggedInUser();
        this.serviceFactory = ServiceFactory.getInstance();
        this.prescrizioneService = serviceFactory.getPrescrizioneService();
        this.pazienteManager = serviceFactory.getPazienteManager();
        this.rilHandler = serviceFactory.getRilevazioneHandler();
        this.sintomoManager = new SintomoManager(serviceFactory.getSintomoDAO());

        caricaAnagrafica();
        inizializzaCombo();
        caricaGrafici();
        caricaSintomiSegnalati();
    }

    private void caricaAnagrafica() {
        nomePazienteLabel.setText(paziente.getNome() + " " + paziente.getCognome());
        cfPazienteLabel.setText(paziente.getCodiceFiscale());
        generePazienteLabel.setText(paziente.getGenere().equals("M") ? "Maschio" : "Femmina");
        dataNascitaLabel.setText(paziente.getDataNascita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ulterInfoPazienteLabel.setText(paziente.getUlter_info() != null ? paziente.getUlter_info() : "Nessuna informazione aggiuntiva");
        medicoLabel.setText(paziente.getMedicoRiferimento().getNome() + " " + paziente.getMedicoRiferimento().getCognome());
    }

    private void inizializzaCombo() {
        periodoComboBox.getItems().setAll("Ultima settimana", "Ultimo mese", "Ultimi 3 mesi");
        periodoComboBox.setValue("Ultima settimana");
        periodoComboBox.setOnAction(e -> caricaGrafici());
    }


    private void caricaSintomiSegnalati() {
        if (paziente == null || sintomoManager == null) return;

        try {
            List<String> sintomi = sintomoManager.getSintomiSegnalatiUltime24h(paziente.getCodiceFiscale());
            if (sintomi != null && !sintomi.isEmpty()) {
                String sintomiText = String.join(", ", sintomi);
                sintomiSegnalatiLabel.setText(sintomiText);
            } else {
                sintomiSegnalatiLabel.setText("Nessun sintomo segnalato nelle ultime 24h.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sintomiSegnalatiLabel.setText("Errore nel caricamento dei sintomi.");
        }
    }


    @FXML
    private void caricaGrafici() {
        if (paziente == null) return;

        String valorePeriodo;
        switch (periodoComboBox.getValue()) {
            case "Ultima settimana": valorePeriodo = "ULTIMA_SETTIMANA"; break;
            case "Ultimo mese": valorePeriodo = "ULTIMO_MESE"; break;
            case "Ultimi 3 mesi": valorePeriodo = "ULTIMI_3_MESI"; break;
            default: valorePeriodo = "ULTIMA_SETTIMANA";
        }

        glicemiaChart.getXAxis().setLabel("Data");
        insulinaChart.getXAxis().setLabel("Data");

        glicemiaChart.getData().clear();
        insulinaChart.getData().clear();

        try {
            List<Rilevazione> glicemie = rilHandler.getRilevazioni(paziente.getCodiceFiscale(), valorePeriodo);
            XYChart.Series<String, Number> serieG = new XYChart.Series<>();
            serieG.setName("Glicemia");

            for (Rilevazione r : glicemie) {
                String label = r.getDataOra().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                serieG.getData().add(new XYChart.Data<>(label, r.getValore()));
            }
            glicemiaChart.getData().add(serieG);

            List<Assunzione> insuline = serviceFactory.getAssunzioneService().getAssunzioniInsulina(paziente.getCodiceFiscale(), valorePeriodo);
            XYChart.Series<String, Number> serieI = new XYChart.Series<>();
            serieI.setName("Insulina");

            for (Assunzione a : insuline) {
                String label = a.getDataOra().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                double dose = Double.parseDouble(a.getDosaggio());
                serieI.getData().add(new XYChart.Data<>(label, dose));
            }
            insulinaChart.getData().add(serieI);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void apriPrescrizioni() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PrescrizioniMedico.fxml"));
            Parent root = loader.load();

            PrescrizioniMedicoController ctrl = loader.getController();
            ctrl.initialize(paziente, prescrizioneService);

            Stage currentStage = (Stage) btnInserisciPrescrizione.getScene().getWindow();
            currentStage.setScene(new Scene(root, 1000, 850));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriFattoriRischio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InformazioniPaziente.fxml"));
            Parent root = loader.load();

            InformazioniPazienteController ctrl = loader.getController();
            ctrl.initialize(paziente, pazienteManager);

            Stage currentStage = (Stage) btnFattoriRischio.getScene().getWindow();
            currentStage.setScene(new Scene(root, 1000, 850));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeMedicoScene.fxml"));
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
