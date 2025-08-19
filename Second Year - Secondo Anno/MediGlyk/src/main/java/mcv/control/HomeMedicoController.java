package mcv.control;

import mcv.session.UserSession;
import mcv.factory.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import mcv.model.Paziente;
import mcv.model.Notifica;
import mcv.model.PazienteManager;
import mcv.model.NotificaHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeMedicoController extends BaseController {

    @FXML private ListView<Notifica> notificheListView;
    @FXML private TilePane pazientiTilePane;

    private PazienteManager pazService;
    private NotificaHandler notifHandler;

    private ServiceFactory factory;

    public void initialize() {
        try {
            factory = ServiceFactory.getInstance();
            pazService = factory.getPazienteManager();
            notifHandler = factory.getNotificaHandler();

            notificheListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Notifica item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String simbolo = switch (item.getTipo()) {
                            case "GLICEMIA_MOLTO_ALTA" -> "‼️   ";
                            case "GLICEMIA_ALTA" -> "⚠️ ";
                            default -> "";
                        };

                        String nome;
                        try {
                            Paziente p = pazService.getByCodiceFiscale(item.getPazienteCF());
                            nome = (p != null) ? p.getNome() + " " + p.getCognome() : item.getPazienteCF();
                        } catch (Exception e) {
                            nome = item.getPazienteCF();
                        }

                        setText(simbolo + nome + ": " + item.getMessaggio());

                        if ("GLICEMIA_MOLTO_ALTA".equals(item.getTipo())) {
                            setStyle("-fx-background-color: #f8d7da;");
                        } else if ("GLICEMIA_ALTA".equals(item.getTipo())) {
                            setStyle("-fx-background-color: #fff3cd;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });

            loadPazienti();
            loadNotifiche();

            notificheListView.setOnMouseClicked(e -> {
                Notifica sel = notificheListView.getSelectionModel().getSelectedItem();
                if (sel != null && "IN_ATTESA".equals(sel.getStatoRisposta())) {
                    if ("TERAPIA_EXTRA".equals(sel.getTipo())) {
                        apriVerificaTerapia(sel);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        notificheListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    if (!notificheListView.isHover() &&
                            notificheListView.getSelectionModel().getSelectedIndex() != -1) {
                        notificheListView.getSelectionModel().clearSelection();
                    }
                });
            }
        });

    }

    private void loadNotifiche() {
        try {
            notificheListView.getItems().clear();

            String medicoCF = UserSession.getInstance().getLoggedInUserCF();
            List<Notifica> notificheListaObj = notifHandler.getNotificheByMedico(medicoCF);

            notificheListView.getItems().addAll(notificheListaObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPazienti() {
        try {
            pazientiTilePane.getChildren().clear();

            List<Paziente> lista = pazService.getAllPazienti();
            for (Paziente p : lista) {
                String iniziali = p.getNome().substring(0, 1).toUpperCase() + p.getCognome().substring(0, 1).toUpperCase();

                Circle circle = new Circle(30);
                circle.setFill(Color.LIGHTBLUE);

                Label inizialiLabel = new Label(iniziali);
                inizialiLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                Label nomeCompleto = new Label(p.getNome() + " " + p.getCognome());
                nomeCompleto.setStyle("-fx-font-size: 12px;");

                VBox vbox = new VBox(5, new StackPane(circle, inizialiLabel), nomeCompleto);
                vbox.setStyle("-fx-alignment: center;");
                vbox.setOnMouseClicked(evt -> openPazienteScene(p));

                pazientiTilePane.getChildren().add(vbox);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apriVerificaTerapia(Notifica n) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Verifica terapia extra");
        dialog.setContentText(n.getMessaggio());
        ButtonType comp = new ButtonType("Compatibile");
        ButtonType nonComp = new ButtonType("Non compatibile");
        dialog.getDialogPane().getButtonTypes().addAll(comp, nonComp, ButtonType.CANCEL);
        dialog.setResultConverter(bt -> bt == comp ? "COMPATIBILE" : bt == nonComp ? "NON_COMPATIBILE" : null);

        dialog.showAndWait().ifPresent(stato -> {
            try {
                notifHandler.rispondiNotifica(n.getId(), stato);
                loadNotifiche();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void openPazienteScene(Paziente p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettagliPazienteScene.fxml"));
            Parent root = loader.load();

            DettagliPazienteController controller = loader.getController();
            controller.setPaziente(p);

            Stage currentStage = (Stage) pazientiTilePane.getScene().getWindow();
            Scene newScene = new Scene(root, 1000, 850);
            currentStage.setScene(newScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminaNotifiche() {
        try {
            notifHandler.eliminaNotifichePerMedico();
            notificheListView.getItems().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomepageScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
