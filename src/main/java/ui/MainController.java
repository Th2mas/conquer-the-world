package ui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.game.GameController;
import ui.game.LoaderController;

/**
 * The main controller for the application
 */
public class MainController {

    /**
     * The optional primary stage for closing the stage on close request
     */
    private Stage primaryStage;

    private LoaderController loaderController;
    private GameController gameController;

    /**
     * The root group
     */
    @FXML
    private Group root;


    @FXML
    public void initialize(){

        // Load the game's content
        loaderController = new LoaderController(root);

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        gameController = new GameController(loaderController.getContinentList());

        // Start the game
        gameController.start();
    }

    /**
     * Starts the actual game
     */
    public void start(){

        // Create the scene object
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        // Add the scene to the stage
        primaryStage.setScene(scene);

        // If the player wants to close the application -> shut the game down
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> gameController.shutdown());

        // Show the contents
        primaryStage.show();
    }

    /**
     * Sets the primary stage
     * @param primaryStage primary stage
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
}
