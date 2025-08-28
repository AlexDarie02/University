package mcv.control;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mcv.factory.ServiceFactory;
import mcv.model.*;

import java.io.IOException;
import java.util.List;

public class HomeSegreteriaController extends BaseController{

    @FXML private TextField cfMedicoField, nomeMedicoField, cognomeMedicoField, emailMedicoField, generePazienteField, ulterInfoPazienteField, patologiePregresseField, comorbiditaField;
    @FXML private TextArea fattoriRischioField;
    @FXML private DatePicker dataNascitaPazientePicker;
    @FXML private TextField passwordMedicoField;
    @FXML private ListView<String> suggerimentiFattoriRischioListView;

    @FXML private TextField cfPazienteField, nomePazienteField, cognomePazienteField, emailPazienteField;
    @FXML private TextField passwordPazienteField;
    @FXML private TextField cfMedicoAssocField;

    @FXML
    private StackPane centerStackPane;

    @FXML private ListView<String> suggerimentiMediciListView;
    private MedicoManager medicoManager;
    private final SegreteriaService segreteriaService;

    public HomeSegreteriaController() {
        this.segreteriaService = ServiceFactory.getInstance().getSegreteriaService();
    }

    @FXML
    public void initialize() {
        this.medicoManager = ServiceFactory.getInstance().getMedicoManager();

        centerStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Object target = event.getTarget();
            if (!(target instanceof TextField) && !(target instanceof DatePicker) && !(target instanceof TextArea)) {
                centerStackPane.requestFocus();
            }
        });

        List<String> opzioniFattori = List.of(
                "Fumatore", "Ex-fumatore", "Dipendenza da alcol",
                "Dipendenza da stupefacenti", "Obesità", "Sedentarietà"
        );

        suggerimentiFattoriRischioListView.setItems(FXCollections.observableArrayList(opzioniFattori));

        suggerimentiFattoriRischioListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                private final Label label = new Label();

                {
                    label.setWrapText(true);
                    label.setMaxWidth(230);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        label.setText(item);
                        setGraphic(label);
                    }
                }
            };
            return cell;
        });


        suggerimentiFattoriRischioListView.setOnMouseClicked(event -> {
            String selezione = suggerimentiFattoriRischioListView.getSelectionModel().getSelectedItem();
            if (selezione != null && !selezione.isBlank()) {
                String attuale = fattoriRischioField.getText();
                if (attuale == null || attuale.isBlank()) {
                    fattoriRischioField.setText(selezione);
                } else {
                    List<String> elementi = List.of(attuale.split("\\s*,\\s*"));
                    if (!elementi.contains(selezione)) {
                        fattoriRischioField.setText(attuale + "," + selezione);
                    }
                }
                suggerimentiFattoriRischioListView.getSelectionModel().clearSelection();
                centerStackPane.requestFocus();
            }
        });


        cfMedicoAssocField.textProperty().addListener((oldVal, Val, newVal) -> {
            if (newVal.length() >= 1) {
                try {
                    List<String> risultati = medicoManager.suggerisciCodiciFiscali(newVal);
                    suggerimentiMediciListView.setItems(FXCollections.observableArrayList(risultati));
                    suggerimentiMediciListView.setVisible(!risultati.isEmpty());
                } catch (Exception e) {
                    suggerimentiMediciListView.getItems().clear();
                    suggerimentiMediciListView.setVisible(false);
                }
            } else {
                suggerimentiMediciListView.getItems().clear();
                suggerimentiMediciListView.setVisible(false);
            }
        });

        suggerimentiMediciListView.setOnMouseClicked(event -> {
            String selezionato = suggerimentiMediciListView.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                cfMedicoAssocField.setText(selezionato);
                suggerimentiMediciListView.setVisible(false);
            }
        });
    }


    @FXML
    private void handleAggiungiMedico() {
        try {
            segreteriaService.aggiungiMedico(new Medico(
                    cfMedicoField.getText(),
                    nomeMedicoField.getText(),
                    cognomeMedicoField.getText(),
                    emailMedicoField.getText(),
                    "medico",
                    passwordMedicoField.getText()
            ));
            mostraInfo("Medico aggiunto con successo!");
            pulisciCampi();
        } catch (Exception e) {
            mostraErrore("Errore: " + e.getMessage());
        }
    }

    @FXML
    private void handleAggiungiPaziente() {
        try {
            Medico medicoRif = segreteriaService.getMedicoByCodiceFiscale(cfMedicoAssocField.getText());
            if (medicoRif == null) {
                mostraErrore("Medico di riferimento non trovato.");
                return;
            }

            segreteriaService.aggiungiPaziente(new Paziente(
                    nomePazienteField.getText(),
                    cognomePazienteField.getText(),
                    cfPazienteField.getText(),
                    emailPazienteField.getText(),
                    "paziente",
                    generePazienteField.getText(),
                    passwordPazienteField.getText(),
                    ulterInfoPazienteField.getText(),
                    dataNascitaPazientePicker.getValue(),
                    medicoRif,
                    fattoriRischioField.getText(),
                    patologiePregresseField.getText(),
                    comorbiditaField.getText()
            ));
            mostraInfo("Paziente aggiunto con successo!");
            pulisciCampi();
        } catch (Exception e) {
            mostraErrore("Errore: " + e.getMessage());
        }
    }


    private void mostraInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void pulisciCampi() {
        cfPazienteField.clear();
        nomePazienteField.clear();
        cognomePazienteField.clear();
        emailPazienteField.clear();
        passwordPazienteField.clear();
        generePazienteField.clear();
        ulterInfoPazienteField.clear();

        if (dataNascitaPazientePicker != null) {
            dataNascitaPazientePicker.setValue(null);
        }

        fattoriRischioField.clear();
        patologiePregresseField.clear();
        comorbiditaField.clear();
        cfMedicoAssocField.clear();

        cfMedicoField.clear();
        nomeMedicoField.clear();
        cognomeMedicoField.clear();
        emailMedicoField.clear();
        passwordMedicoField.clear();
    }

    @FXML
    private void apriFinestraEliminaMedico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EliminaMedicoScene.fxml"));
            Parent root = loader.load();

            EliminaMedicoController controller = loader.getController();
            controller.setSegreteriaService(segreteriaService);

            Stage stage = new Stage();
            stage.setTitle("Elimina Medico");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriFinestraEliminaPaziente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EliminaPazienteScene.fxml"));
            Parent root = loader.load();

            EliminaPazienteController controller = loader.getController();
            controller.setSegreteriaService(segreteriaService);

            Stage stage = new Stage();
            stage.setTitle("Elimina Paziente");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriFinestraModificaMedicoCurante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModificaMedicoCuranteScene.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifica Medico Curante");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
