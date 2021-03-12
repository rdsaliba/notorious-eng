package Controllers;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreenPreloader extends Preloader {

    private Stage preloaderStage;

    private Scene createPreloaderScene() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/SplashScreen.fxml"));
        return new Scene(root);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;
        preloaderStage.setScene(createPreloaderScene());
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (preloaderStage.isShowing()) {
                //fade out, hide stage at the end of animation
                FadeTransition ft = new FadeTransition(
                        Duration.millis(1500), preloaderStage.getScene().getRoot());
                ft.setFromValue(1.5);
                ft.setToValue(0.0);
                final Stage stageLoader = preloaderStage;
                EventHandler<ActionEvent> eventH = new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        stageLoader.hide();
                    }
                };
                ft.setOnFinished(eventH);
                ft.play();
            } else {
                preloaderStage.hide();
            }
        }
    }
}
