package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.game.GameController;
import ui.menu.MenuController;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.Locale;

/**
 * The main controller for the application
 */
public class MainController {

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
    public VBox mainPane;

    /**
     * The optional primary stage for closing the stage on close request
     */
    private Stage primaryStage;

    /**
     * The controller, which handles the game logic
     */
    private GameController gameController;

    /**
     * The controller for the top menu
     */
    private MenuController menuController;

    /**
     * Manages the different languages
     */
    private PropertiesManager langManager;

    /**
     * Manages the different settings
     */
    private PropertiesManager settingsManager;

    /**
     * Initializes the necessities for the game
     */
    @FXML
    public void initialize(){

        // Create the new language manager
        langManager = new PropertiesManager("properties/lang");

        // Create the new settings manager
        settingsManager = new PropertiesManager("properties/settings");

        //langManager.changeLocale(Locale.ENGLISH);

        // Load the menu pane
        initMenuBar();
        top.getChildren().add(menuController.getView());

        // Load the game pane
        initGamePane();
        center.getChildren().add(gameController.getView());
    }

    /**
     * Starts the actual game
     */
    public void start(){

        // Create the scene object
        Scene scene = new Scene(mainPane);

        gameController.setOnKeyPressed(scene);

        // Add the scene to the stage
        primaryStage.setScene(scene);

        // Show the contents
        primaryStage.show();
    }



    // TODO: Do something about the duplicate code here...
    private void initMenuBar(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(langManager.getBundle());
            loader.setLocation(MainController.class.getResource("/fxml/MenuBar.fxml"));
            loader.load();

            menuController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Do something about the duplicate code here...
    private void initGamePane(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(langManager.getBundle());
            loader.setLocation(MainController.class.getResource("/fxml/GamePane.fxml"));
            loader.load();

            gameController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the primary stage
     * @param primaryStage primary stage
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
}
