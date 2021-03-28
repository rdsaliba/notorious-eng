package app;/*
  This class implements the start function which runs first when opening the application.
   It sets the scene / view to the main page.
  @author
  @last_edit 02/7/2020
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.TextConstants;

public class CBMSApplication extends Application {

    static Logger logger = LoggerFactory.getLogger(CBMSApplication.class);

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", utilities.SplashScreenPreloader.class.getName());
        Application.launch(CBMSApplication.class, args);
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set
     * @author Jeff
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(TextConstants.ASSETS_SCENE + TextConstants.FXML));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setUserData(loader);
        primaryStage.setTitle("Minerva");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            try {
                closeProgram();
            } catch (Exception ex) {
                logger.error("Exception in starting the application: ", ex);
            }
        });
    }

    /**
     * Loads all live assets before starting the application
     */
    @Override
    public void init() {
        long loadStartTime = System.currentTimeMillis();
        try {
            ModelController.getInstance().getAllLiveAssets();
        } catch (Exception e) {
            logger.error("Exception in initializing the application: ", e);
        }
        long loadEndTime = System.currentTimeMillis();
        if ((loadEndTime - loadStartTime) < 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Exception in putting to sleep the threads: ", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Closes the program.
     *
     * @author Najim
     */
    private void closeProgram() {
        logger.info("Program closing.");
        Platform.exit();
        System.exit(0);
    }
}
