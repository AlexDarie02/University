package mcv.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import mcv.factory.ServiceFactory;
import mcv.model.Sintomo;
import mcv.model.SintomoManager;
import mcv.session.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SegnalazioneSintomiController {

    @FXML private CheckBox febbreCheck;
    @FXML private CheckBox tosseCheck;
    @FXML private CheckBox malDiTestaCheck;
    @FXML private CheckBox nauseaCheck;
    @FXML private CheckBox irritabilitàCheck;
    @FXML private CheckBox vistaOffuscataCheck;
    @FXML private CheckBox pelleSeccaCheck;
    @FXML private CheckBox feriteLenteCheck;
    @FXML private CheckBox pruritoCheck;
    @FXML private CheckBox seteEccessivaCheck;
    @FXML private CheckBox formicoliiCheck;
    @FXML private CheckBox perditaPesoCheck;
    @FXML private CheckBox palpitazioniCheck;

    private SintomoManager sintomoManager;
    private String cfPaziente;

    @FXML
    public void initialize() {
        cfPaziente = UserSession.getInstance().getLoggedInUserCF();
        sintomoManager = new SintomoManager(ServiceFactory.getInstance().getSintomoDAO());

        try {
            List<String> sintomiRecenti = sintomoManager.getSintomiSegnalatiUltime24h(cfPaziente);

            for (CheckBox cb : getCheckBoxList()) {
                if (sintomiRecenti.contains(cb.getText())) {
                    cb.setSelected(true);
                    cb.setDisable(true);
                    cb.setStyle("-fx-opacity: 0.5;");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Errore nel caricamento dei sintomi segnalati");
        }
    }

    @FXML
    private void handleInserisciSintomi() {
        for (CheckBox cb : getCheckBoxList()) {
            if (cb.isSelected() && !cb.isDisabled()) {
                try {
                    sintomoManager.inserisciSintomo(new Sintomo(cfPaziente, cb.getText(), null));
                    cb.setDisable(true);
                    cb.setStyle("-fx-opacity: 0.5;");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
        }
    }

    private List<CheckBox> getCheckBoxList() {
        return List.of(
                febbreCheck, tosseCheck, malDiTestaCheck,
                nauseaCheck, irritabilitàCheck, vistaOffuscataCheck,
                pelleSeccaCheck, feriteLenteCheck, pruritoCheck,
                seteEccessivaCheck, formicoliiCheck, perditaPesoCheck, palpitazioniCheck
        );
    }
}
