package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.game.GameController;
import ui.menu.MenuController;
import util.fxml.FXMLHelper;
import util.properties.PropertiesManager;

import java.io.IOException;

/**
 * The main controller for the application
 */
public class MainController {

    /**
     * The {@link MainController} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class.getName());

    /**
     * The top pane for the menu
     */
    @FXML
    public Pane top;

    /**
     * The center pane, which will contain the continents
     */
    @FXML
    public Pane center;

    /**
     * The main pane, which will contain all elements
     */
    @FXML
    public AnchorPane mainPane;

    /**
     * The optional primary stage for closing the stage on close request
     */
    private Stage primaryStage;

    /**
     * The controller, which handles the game logic
     */
    private GameController gameController;

    /**
     * Initializes the necessities for the game
     */
    @FXML
    public void initialize(){

        LOGGER.info("Initialize");

        String file = "/fxml/menu/MenuBar.fxml";
        // Load the menu pane (the controller for the top menu)
        MenuController menuController;
        try {
            menuController = FXMLHelper.loadFXMLController(file);
        } catch (IOException e) {
            LOGGER.error("Could not load " + file + ": " + e.getMessage());
            return;
        }

        // Add the contents to the bottom
        top.getChildren().add(menuController.getView());

        file = "/fxml/GamePane.fxml";
        // Load the game pane
        try {
            gameController = FXMLHelper.loadFXMLController(file);
        } catch (IOException e) {
            LOGGER.error("Could not load " + file + ": " + e.getMessage());
            return;
        }

        // Add the contents to the center
        center.getChildren().add(gameController.getView());

    }

    /**
     * Starts the actual game
     */
    public void start(){

        // Create the scene object
        Scene scene = new Scene(mainPane);

        // Add the scene to the stage
        primaryStage.setScene(scene);

        if(gameController != null) {
            // Set the game controllers on key pressed
            gameController.setOnKeyPressed(scene);

            // Bind the game controller's width and height property to the stage's width and height property
            // TODO: Add the binding
            if(PropertiesManager.getBoolean("window.resizable","window")) {
                primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> gameController.resize((Double)oldValue, primaryStage.getHeight(), (Double)newValue, primaryStage.getHeight()));
                primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> gameController.resize(primaryStage.getWidth(), (Double)oldValue, primaryStage.getWidth(), (Double)newValue));

            }
        }

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
