import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import ui.MainController;
import util.error.ErrorDialog;
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

        // Get the window's properties file
        PropertiesManager windowManager = new PropertiesManager("properties/window");

        // Set the window parameters
        primaryStage.setWidth(windowManager.getInt("window.size.x"));
        primaryStage.setHeight(windowManager.getInt("window.size.y"));
        primaryStage.setTitle(windowManager.getString("window.title"));
        primaryStage.setResizable(windowManager.getBoolean("window.resizable"));

        initRootLayout();
    }

    /**
     * Initializes the root layout
     */
    private void initRootLayout(){
        try {
            // Read the controller for this game
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/fxml/MainApplication.fxml"));
            loader.load();

            MainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.start();

        } catch (IOException e) {
            ErrorDialog.showErrorDialog(e.getMessage());
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
