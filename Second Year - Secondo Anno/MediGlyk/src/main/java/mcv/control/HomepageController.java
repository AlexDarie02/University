package mcv.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HomepageController extends BaseController {
    @FXML
    private Button pazienteButton;

    @FXML
    private Button medicoButton;

    @FXML
    private Button segreteriaButton;

    @FXML
    private void initialize() {
        pazienteButton.setFocusTraversable(false);
        medicoButton.setFocusTraversable(false);
        segreteriaButton.setFocusTraversable(false);
    }

    @FXML
    private void handlePazienteClick(ActionEvent event) {
        caricaAutenticazione(event, "paziente");
    }

    @FXML
    private void handleMedicoClick(ActionEvent event) {
        caricaAutenticazione(event, "medico");
    }

    @FXML
    private void handleSegreteriaClick(ActionEvent event) {
        caricaAutenticazione(event, "segreteria");
    }

    private void caricaAutenticazione(ActionEvent event, String ruolo) {

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/LoginPage.fxml")));
            Parent root = loader.load();

            LoginPageController controller = loader.getController();
            controller.initRuolo(ruolo);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene newScene = new Scene(root);
            stage.setScene(newScene);


            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
