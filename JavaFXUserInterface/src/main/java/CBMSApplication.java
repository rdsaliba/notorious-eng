/*
  This class implements the start function which runs first when opening the application.
   It sets the scene / view to the main page.
  @author
  @last_edit 02/7/2020
 */

import app.ModelController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CBMSApplication extends Application {

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreenPreloader.class.getName());
        Application.launch(CBMSApplication.class, args);
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set
     * @author
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Assets.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Minerva");
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

    /**
     * Loads all live assets before starting the application
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        long loadStartTime = System.currentTimeMillis();
        try {
            ModelController.getInstance().getAllLiveAssets();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long loadEndTime = System.currentTimeMillis();
        if ((loadEndTime - loadStartTime) < 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the program.
     *
     * @author Najim
     */
    private void closeProgram() {
        System.out.println("Program closing.");
        Platform.exit();
        System.exit(0);
    }
}
