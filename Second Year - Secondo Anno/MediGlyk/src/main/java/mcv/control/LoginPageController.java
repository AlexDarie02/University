package mcv.control;

import mcv.model.LoginModel;
import mcv.model.Utente;
import mcv.session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginPageController extends BaseController {
    private String ruoloSelezionato;

    @FXML
    private TextField CFTextField;
    @FXML
    private PasswordField PSField;

    @FXML
    private Label esitoLabel;

    public void initRuolo(String ruolo) {
        this.ruoloSelezionato = ruolo;
    }

    @FXML
    private void initialize() {
        CFTextField.requestFocus();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String cf = CFTextField.getText();
        String pw = PSField.getText();

        if (cf.isEmpty() || pw.isEmpty()) {
            esitoLabel.setStyle("-fx-text-fill: red;");
            esitoLabel.setText("Inserisci codice fiscale e password.");
            return;
        }

        LoginModel model = new LoginModel(cf, pw, ruoloSelezionato);
        Utente utente = model.checkLogin();

        if (utente != null) {
            UserSession.getInstance().setLoggedInUser(utente);
            esitoLabel.setStyle("-fx-text-fill: green;");
            esitoLabel.setText("Credenziali corrette.");
            cambiaView("/Home" + capitalize(ruoloSelezionato) + "Scene.fxml", event);
        } else {
            esitoLabel.setStyle("-fx-text-fill: red;");
            esitoLabel.setText("Credenziali errate per " + ruoloSelezionato + ".");
        }
    }

    private void cambiaView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            esitoLabel.setStyle("-fx-text-fill: red;");
            esitoLabel.setText("Errore nel caricamento della schermata.");
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}