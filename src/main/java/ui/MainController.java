package ui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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

    /**
     * The controller, which handles the game logic
     */
    private GameController gameController;

    /**
     * The container for the polygons
     */
    @FXML
    private Group group;

    /**
     * The pane, in which the main elements will be drawn
     */
    @FXML
    private Pane root;

    /**
     * The pane in which all information will be displayed
     */
    @FXML
    private Pane infoPane;

    /**
     * Initializes the necessities for the game
     */
    @FXML
    public void initialize(){

        // Load the game's content
        LoaderController loaderController = new LoaderController(group);

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        // Create the new game controller
        gameController = new GameController(loaderController.getContinentList(), group, infoPane);

        // Start the game
        gameController.start();
    }

    /**
     * Starts the actual game
     */
    public void start(){

        // Create the scene object
        Scene scene = new Scene(root);

        // Add the scene to the stage
        primaryStage.setScene(scene);

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
