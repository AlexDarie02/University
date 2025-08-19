package mcv.control;

import mcv.factory.ServiceFactory;
import mcv.model.*;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrescrizioniController {
    @FXML private TableView<PrescrizioneTableModel> prescrizioniTable;
    @FXML private TableColumn<PrescrizioneTableModel, String> farmacoColumn;
    @FXML private TableColumn<PrescrizioneTableModel, String> dosaggioColumn;
    @FXML private TableColumn<PrescrizioneTableModel, String> frequenzaColumn;
    @FXML private TableColumn<PrescrizioneTableModel, LocalDate> dataColumn;
    @FXML private ListView<String> terapieExtraListView;
    @FXML private Button eliminaTerapiaButton;

    private ObservableList<String> terapieExtraList = FXCollections.observableArrayList();
    private PazienteManager pazienteManager;
    private ObservableList<PrescrizioneTableModel> prescrizioniData = FXCollections.observableArrayList();
    private Paziente currentPaziente;
    private PrescrizioneService prescrizioneService;
    private ServiceFactory serviceFactory;

    public static class PrescrizioneTableModel {
        private final int id;
        private final StringProperty nomeFarmaco;
        private final StringProperty dosaggio;
        private final StringProperty frequenza;
        private final ObjectProperty<LocalDate> dataPrescrizione;

        public PrescrizioneTableModel(int id, String nomeFarmaco, String dosaggio,
                                      String frequenza, LocalDate dataPrescrizione) {
            this.id = id;
            this.nomeFarmaco = new SimpleStringProperty(nomeFarmaco);
            this.dosaggio = new SimpleStringProperty(dosaggio);
            this.frequenza = new SimpleStringProperty(frequenza);
            this.dataPrescrizione = new SimpleObjectProperty<>(dataPrescrizione);
        }

        public int getId() { return id; }
        public StringProperty nomeFarmacoProperty() { return nomeFarmaco; }
        public StringProperty dosaggioProperty() { return dosaggio; }
        public StringProperty frequenzaProperty() { return frequenza; }
        public ObjectProperty<LocalDate> dataPrescrizioneProperty() { return dataPrescrizione; }
    }

    @FXML
    public void initialize() {
        farmacoColumn.setCellValueFactory(cellData -> cellData.getValue().nomeFarmacoProperty());
        dosaggioColumn.setCellValueFactory(cellData -> cellData.getValue().dosaggioProperty());
        frequenzaColumn.setCellValueFactory(cellData -> cellData.getValue().frequenzaProperty());
        dataColumn.setCellValueFactory(cellData -> cellData.getValue().dataPrescrizioneProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dataColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });

        terapieExtraListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    if (!terapieExtraListView.isHover() &&
                            !eliminaTerapiaButton.isHover() &&
                            terapieExtraListView.getSelectionModel().getSelectedIndex() != -1) {
                        terapieExtraListView.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    public void setPaziente(Paziente paziente) {
        this.currentPaziente = paziente;
        this.serviceFactory = ServiceFactory.getInstance();
        this.prescrizioneService = serviceFactory.getPrescrizioneService();
        this.pazienteManager = serviceFactory.getPazienteManager();

        caricaPrescrizioni();
        caricaTerapieExtra();
    }

    private void caricaPrescrizioni() {
        prescrizioniData.clear();
        List<Prescrizione> prescrizioni = prescrizioneService.getPrescrizioniPerPaziente(
                currentPaziente.getCodiceFiscale()
        );

        for (Prescrizione p : prescrizioni) {
            PrescrizioneTableModel model = new PrescrizioneTableModel(
                    p.getId(),
                    p.getNomeFarmaco(),
                    p.getDosaggio(),
                    p.getFrequenza(),
                    p.getDataPrescrizione()
            );
            prescrizioniData.add(model);
        }

        prescrizioniTable.setItems(prescrizioniData);
    }

    @FXML
    private void handleEliminaTerapiaExtra(ActionEvent event) {
        String selected = terapieExtraListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean removed = terapieExtraList.remove(selected);
            terapieExtraListView.getSelectionModel().clearSelection();
            salvaTerapieExtra();
        }
    }


    private void caricaTerapieExtra() {
        terapieExtraList.clear();
        String ulterInfo = currentPaziente.getUlter_info();

        if (ulterInfo != null && !ulterInfo.isBlank()) {
            List<String> parsed = List.of(ulterInfo.split("\\r?\\n|;"))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            terapieExtraList.addAll(parsed);
        }

        terapieExtraListView.setItems(terapieExtraList);
    }

    private void salvaTerapieExtra() {
        String nuovoUlterInfo = String.join("\n", terapieExtraList);
        boolean ok = pazienteManager.setUlterInfo(currentPaziente.getCodiceFiscale(), nuovoUlterInfo);

        if (ok) {
            currentPaziente.setUlter_info(nuovoUlterInfo);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore durante il salvataggio.", ButtonType.OK);
            alert.showAndWait();
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
            System.out.println("Errore qua");
        }
    }
}
