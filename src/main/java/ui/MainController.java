package ui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    private GameController gameController;

    /**
     * The container for the polygons
     */
    @FXML
    private Group group;

    @FXML
    private Pane root;

    @FXML
    private Label lbl_phase;

    @FXML
    private Label lbl_armies;


    @FXML
    public void initialize(){

        // Load the game's content
        LoaderController loaderController = new LoaderController(group);

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        gameController = new GameController(loaderController.getContinentList(), lbl_phase, lbl_armies);

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
