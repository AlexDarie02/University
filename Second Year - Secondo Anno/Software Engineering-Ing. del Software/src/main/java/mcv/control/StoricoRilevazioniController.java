package mcv.control;

import javafx.scene.control.TableCell;
import mcv.model.Paziente;
import mcv.model.StoricoRilevazioniManager;
import mcv.model.Rilevazione;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StoricoRilevazioniController {
    @FXML private TableView<RilevazioneTableModel> rilevazioniTable;
    @FXML private TableColumn<RilevazioneTableModel, Integer> valoreColumn;
    @FXML private TableColumn<RilevazioneTableModel, String> tipoColumn;
    @FXML private TableColumn<RilevazioneTableModel, LocalDateTime> dataColumn;

    private Paziente paziente;
    private StoricoRilevazioniManager manager;

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
        try {
            this.manager = new StoricoRilevazioniManager();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void caricaRilevazioni() {
        valoreColumn.setCellValueFactory(new PropertyValueFactory<>("valore"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataOra"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dataColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });


        try {
            List<Rilevazione> rilevazioni = manager.getRilevazioniByPaziente(paziente.getCodiceFiscale());
            for (Rilevazione r : rilevazioni) {
                rilevazioniTable.getItems().add(new RilevazioneTableModel(
                        r.getValore(),
                        r.getTipo(),
                        r.getDataOra()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        rilevazioniTable.getSelectionModel().clearSelection();
        rilevazioniTable.setFocusTraversable(false);

    }

    public static class RilevazioneTableModel {
        private final int valore;
        private final String tipo;
        private final LocalDateTime dataOra;

        public RilevazioneTableModel(int valore, String tipo, LocalDateTime dataOra) {
            this.valore = valore;
            this.tipo = tipo;
            this.dataOra = dataOra;
        }

        public int getValore() {
            return valore;
        }

        public String getTipo() {
            return tipo;
        }

        public LocalDateTime getDataOra() {
            return dataOra;
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
}
