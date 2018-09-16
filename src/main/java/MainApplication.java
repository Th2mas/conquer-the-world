import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainController;
import util.error.DialogHelper;
import util.fxml.FXMLHelper;
import util.properties.PropertiesManager;

import java.io.IOException;

/**
 * The main application
 */
public class MainApplication extends Application{

    /**
     * The application's primary stage
     */
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        PropertiesManager.initialize();

        // Set the window parameters
        primaryStage.setWidth(PropertiesManager.getInt("window.size.x", "window"));
        primaryStage.setHeight(PropertiesManager.getInt("window.size.y", "window"));
        primaryStage.setTitle(PropertiesManager.getString("window.title", "window"));
        primaryStage.setResizable(PropertiesManager.getBoolean("window.resizable", "window"));

        initRootLayout();
    }

    /**
     * Initializes the root layout
     */
    private void initRootLayout(){
        try {
            // Read the controller for this game
            MainController controller = FXMLHelper.loadFXMLController("/fxml/MainApplication.fxml");
            controller.setPrimaryStage(primaryStage);
            controller.start();

        } catch (IOException e) {
            DialogHelper.createErrorDialog(e.getMessage());
        }
    }

    /**
     * The main method, which starts the game
     * @param args no args
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}
