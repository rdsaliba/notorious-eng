package CBMS;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CBMSApplication extends Application{

    public static void main(String[] args) {
        launch(args);
    }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/Systems.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("CBMS");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                try {
                    closeProgram();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        }

        private void closeProgram() {
            System.out.println("Program closing.");
            Platform.exit();
            System.exit(0);
        }
}
