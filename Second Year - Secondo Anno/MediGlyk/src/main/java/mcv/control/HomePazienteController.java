package mcv.control;

import javafx.scene.layout.BorderPane;
import mcv.factory.ServiceFactory;
import mcv.model.*;
import mcv.session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomePazienteController extends BaseController implements Initializable {
    @FXML private Label infoPazienteLabel;
    @FXML private ListView<String> notificheListView;
    @FXML private BorderPane rootPane;

    private Paziente paziente;
    private NotificaHandler notificaHandler;
    private ServiceFactory serviceFactory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            rootPane.setOnMousePressed(event -> {
                if (!notificheListView.isHover()) {
                    notificheListView.getSelectionModel().clearSelection();
                }
            });

            paziente = (Paziente) UserSession.getInstance().getLoggedInUser();
            serviceFactory = ServiceFactory.getInstance();
            notificaHandler = serviceFactory.getNotificaHandler();

            if (paziente != null) {
                Medico medico = paziente.getMedicoRiferimento();
                String nomeMedico = medico != null ? medico.getNome() : "N/D";
                String cognomeMedico = medico != null ? medico.getCognome() : "N/D";

                String infoText = String.format("""
                Nome: %s
                Cognome: %s
                Codice Fiscale: %s
                Email: %s
                Sesso: %s
                Medico: %s %s
                """,
                        paziente.getNome(),
                        paziente.getCognome(),
                        paziente.getCodiceFiscale(),
                        paziente.getEmail(),
                        paziente.getGenere(),
                        nomeMedico,
                        cognomeMedico
                );

                infoPazienteLabel.setText(infoText);
                loadNotifiche();
            } else {
                infoPazienteLabel.setText("Dati paziente non disponibili");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInsertRilevazione(ActionEvent event) {
        cambiaScena(event, "/InserisciRilevazioniScene.fxml");
    }

    @FXML
    private void handleStoricoRilevazioni(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StoricoRilevazioniScene.fxml"));
            Parent root = loader.load();
            StoricoRilevazioniController controller = loader.getController();
            controller.setPaziente(paziente);
            controller.caricaRilevazioni();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSegnalaSintomi(ActionEvent event) {
        cambiaScena(event, "/SegnalaSintomiScene.fxml");
    }

    @FXML
    private void handleSegnalaFarmaci(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PrescrizioniScene.fxml"));
            Parent root = loader.load();
            PrescrizioniController controller = loader.getController();
            controller.setPaziente(paziente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAssunzioni(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AssunzioniScene.fxml"));
            Parent root = loader.load();
            AssunzioniController controller = loader.getController();
            controller.setPaziente(paziente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifiche() {
        try {
            notificheListView.getItems().clear();
            List<Notifica> tutte = notificaHandler.getAllNotifiche();

            for (Notifica n : tutte) {
                if (
                        "TERAPIA_EXTRA".equals(n.getTipo()) &&
                                n.getPazienteCF().equals(paziente.getCodiceFiscale())
                ) {
                    String testo;
                    switch (n.getStatoRisposta()) {
                        case "COMPATIBILE":
                            testo = "✅ Terapia compatibile: " + n.getMessaggio();
                            break;
                        case "NON_COMPATIBILE":
                            testo = "❌ Terapia NON compatibile: " + n.getMessaggio();
                            break;
                        default:
                            testo = "⏳ In attesa di valutazione: " + n.getMessaggio();
                    }
                    notificheListView.getItems().add(testo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminaNotifiche(ActionEvent event) {
        try {
            notificaHandler.eliminaNotifichePerPaziente(paziente.getCodiceFiscale());
            loadNotifiche();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
