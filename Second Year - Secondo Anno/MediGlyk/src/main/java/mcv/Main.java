package mcv;

import mcv.session.UserSession;
import mcv.factory.ServiceFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = Objects.requireNonNull(FXMLLoader.load(getClass().getResource("/HomepageScene.fxml")));
            Scene scene = new Scene(root, 800, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MediGlyk");

            // Imposto i limiti della finestra
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(990);
            primaryStage.setWidth(1000);
            primaryStage.setHeight(990);
            primaryStage.setMaxWidth(1000);
            primaryStage.setMaxHeight(990);


            // Handler per le chiusura con la X
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                showCloseConfirmation(primaryStage);
            });
            
            primaryStage.show();
            primaryStage.centerOnScreen();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showCloseConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Si sta per effettuare il logout");
        alert.setContentText("Si è sicuri della scelta?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                ServiceFactory.getInstance().close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Pulizia della sessione
            UserSession.getInstance().cleanUserSession();
            stage.close();
        }

        javafx.application.Platform.exit();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
