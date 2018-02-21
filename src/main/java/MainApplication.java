import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui.MainController;
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

        primaryStage.show();
    }

    /**
     * Initializes the root layout
     */
    private void initRootLayout(){
        try {
            // Read the controller for this game
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/fxml/MainApplication.fxml"));
            AnchorPane rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            MainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
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
