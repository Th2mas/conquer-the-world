package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ContinentService;
import service.PlayerService;
import service.impl.SimpleContinentService;
import service.impl.SimplePlayerService;
import ui.game.phase.Phase;
import ui.game.phase.impl.AcquisitionPhase;
import ui.game.phase.impl.ArmyPlacementPhase;
import ui.game.phase.impl.EndRoundPhase;
import ui.game.phase.impl.MoveAndAttackPhase;
import util.fxml.FXMLHelper;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

    /**
     * The {@link GameController} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    /**
     * Contains all continents
     */
    private List<Continent> continentList;

    /**
     * Contains the {@link Text} objects for every country
     */
    private Map<String, Text> capitalMap;

    /**
     * The pane containing the game elements (containing gameBottom and gameContainer)
     */
    @FXML
    public BorderPane gamePane;

    /**
     * The container for additional information, such as current phase or armies
     */
    @FXML
    public Pane gameBottom;

    /**
     * The container for the game group (the actual game)
     */
    @FXML
    public Pane gameContainer;

    /**
     * A group containing all game elements
     */
    @FXML
    public Group gameGroup;

    /**
     * Defines the current phase
     */
    private Phase currentPhase;

    /**
     * The service for handling actions on players
     */
    private PlayerService playerService = SimplePlayerService.getSimplePlayerService();

    /**
     * The service for handling actions on continents
     */
    private ContinentService continentService = SimpleContinentService.getContinentService();

    /**
     * The property, which notifies all listener, if the phase was changed
     */
    private StringProperty phaseProperty;

    /**
     * The property, which notifies all listener, if the number of armies was changed
     */
    private StringProperty armiesProperty;

    /**
     * A property, which notifies all listener, if the language property has changed
     */
    private StringProperty languageProperty;

    /**
     * The controller for loading and manipulating map information
     */
    private LoaderController loaderController;

    /**
     * The controller for the additional information
     */
    private InformationController informationController;

    /**
     * Sets the selected country
     */
    private Country selectedCountry;

    /**
     * The window's base width
     */
    private static final int BASE_WIDTH = PropertiesManager.getInt("window.size.x","window");

    /**
     * The window's base height
     */
    private static final int BASE_HEIGHT = PropertiesManager.getInt("window.size.y", "window");

    @FXML
    public void initialize(){

        LOGGER.info("Initialize");

        // Load the game's content
        loaderController = new LoaderController("/map/world.map");      // TODO: Let the user decide which map to use!

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        continentList = loaderController.getContinentList();

        currentPhase = new AcquisitionPhase(this);
        capitalMap = new HashMap<>();

        // Get all relevant services
        playerService = SimplePlayerService.getSimplePlayerService();
        continentService = SimpleContinentService.getContinentService();

        continentService.setupContinentService(gameGroup);
        redrawLines(BASE_WIDTH);

        // Initialize the bottom pane
        try {
            informationController = FXMLHelper.loadFXMLController("/fxml/InfoPane.fxml");
        } catch (IOException e) {
            //LOGGER.error(e.getMessage());
            e.printStackTrace();
            return;
        }

        // Get the labels and bind their text properties to the respective properties
        gameBottom.getChildren().add(informationController.getView());

        // Set the hover property
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            // Set the hover property: If a player hovers over a country, display the name in the information pane
            country.getPatches().forEach(
                    patch -> patch.hoverProperty().addListener(
                            listener -> informationController.countryTextProperty().setValue(patch.isHover() ? country.getName() : "")
                    )
            );
        }));

        // TODO Later: Request the player's name and color
        // Create the player
        Player player = playerService.createPlayer("Player", false);
        player.setMove(true);

        // Create the opponents
        Player ai = playerService.createPlayer("Ai1", true);
        ai.setColor(Color.GREY);    // TODO: MOVE this to CSS

        // Bind the phase to the phase label
        phaseProperty = new SimpleStringProperty(PropertiesManager.getString("Game.Phase", "lang") +": " + currentPhase);
        phaseProperty.addListener((observable, oldValue, newValue) -> setPhase(currentPhase));
        PropertiesManager.addSubscriber(phaseProperty);

        // Bind the number of armies to the armies label
        armiesProperty = new SimpleStringProperty(PropertiesManager.getString("Game.Armies", "lang") +": " + 0);
        armiesProperty.addListener((observable, oldValue, newValue) -> showArmiesLabel(player));
        player.armiesProperty().addListener((observable, oldValue, newValue) -> showArmiesLabel(player));
        PropertiesManager.addSubscriber(armiesProperty);

        // Bind the items to the language property
        languageProperty = new SimpleStringProperty(" ");
        languageProperty.addListener(((observable, oldValue, newValue) -> {
            // Define what will happen, as soon, as the language changes

            // Change the game values
            continentList.forEach(continent -> {
                // Change the continent's name
                continent.setName(PropertiesManager.getString("Continent."+continent.getBaseName().replaceAll("\\s+",""),"lang"));

                // Change the countries name
                continent.getCountries().forEach(country -> country.setName(PropertiesManager.getString("Country." + country.getBaseName().replaceAll("\\s+",""),"lang")));
            });
            LOGGER.info("Changed countries names");

            // Set the language property to blank space, so that it can be notified again
            languageProperty.setValue(" ");
        }));
        PropertiesManager.addSubscriber(languageProperty);

        informationController.armiesTextProperty().bindBidirectional(armiesProperty);
        informationController.phaseTextProperty().bindBidirectional(phaseProperty);

        start();
    }

    /**
     * Starts the phases
     */
    private void start(){

        // Start the game
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(patch -> {

                // CLICKED
                patch.setOnMouseClicked(event -> currentPhase.click(country));

                // DRAGGED
                patch.setOnDragDetected(event -> currentPhase.dragDetect(country));
                patch.setOnMouseReleased(event -> currentPhase.dragDrop(event.getX(), event.getY(), country));

            });
        }));

        // Specify what will happen, if the phase changes
        phaseProperty.addListener(((observable, oldValue, newValue) -> {

            Player currentPlayer = playerService.getCurrentPlayer();
            LOGGER.info("Current " + PropertiesManager.getString("Game.Player", "lang") +": " + currentPlayer.getName());

            // Define what to do, if it's the computer's turn
            if(currentPlayer.isAi()) {
                LOGGER.info("Current " + PropertiesManager.getString("Game.Player", "lang") + " isAi");

                // Define what to do on ArmyPlacement
                if (currentPhase.getClass() == ArmyPlacementPhase.class) {
                    while(currentPlayer.getArmies() > 0) {
                        // Place armies in random countries
                        Country randomCountry = currentPlayer.getCountries().get((int) (Math.random() * currentPlayer.getCountries().size()));
                        currentPhase.click(randomCountry);
                    }
                }

                // Define what to do on MoveAndAttack
                if (currentPhase.getClass() == MoveAndAttackPhase.class) {
                    currentPlayer.getCountries().forEach(country -> {
                        // Detect the dragging
                        currentPhase.dragDetect(country);
                        country.getNeighbors().forEach(
                                neighbor -> currentPhase.dragDrop(neighbor.getCapital().getX(), neighbor.getCapital().getY(), country)
                        );
                    });
                    new EndRoundPhase(this);
                }
            }
        }));
    }

    /**
     * Shows armies for the player on the screen
     * @param player the player which is used to display army information
     */
    public void showArmiesLabel(Player player){
        armiesProperty.set(PropertiesManager.getString("Game.Armies", "lang") +": " + player.getArmies());
    }

    /**
     * Returns the list with continents
     * @return continentList
     */
    public List<Continent> getContinentList() {
        return continentList;
    }

    /**
     * Selects a country for dragging
     * @param country Country for dragging
     */
    public void selectCountry(Country country){
        this.selectedCountry = country;
    }

    /**
     * Returns the country, which was selected for dragging
     * @return country
     */
    public Country getSelectedCountry() {
        return selectedCountry;
    }

    /**
     * Returns the {@link GameController}'s playerService
     * @return {@link PlayerService}
     */
    public PlayerService getPlayerService() {
        return playerService;
    }

    public Map<String, Text> getCapitalMap() {
        return capitalMap;
    }

    /**
     * Resets the text of the capitals (for a new game)
     */
    public void resetCapitalText() {
        // Create the capital text objects and put them into the map
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            // Check, if there is already a text and remove it, if present (needed for new game)
            Text oldText = capitalMap.get(country.getBaseName());
            if(oldText != null) gameGroup.getChildren().remove(oldText);

            // Create a new text
            Text text = new Text(country.getCapital().getX(), country.getCapital().getY(), "");
            text.setFill(Color.BLACK);
            text.setVisible(false);

            capitalMap.put(country.getBaseName(), text);

            gameGroup.getChildren().add(text);
        }));
    }

    /**
     * Draws all armies for every player (in black)
     */
    public void showArmiesOnCountries(){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = capitalMap.get(country.getBaseName());

            playerService.getPlayers().forEach(player -> {
                if(player.hasCountry(country)) text.setText(player.getArmies(country) + "");
            });

            text.setVisible(true);
        }));
    }

    /**
     * Sets the given phase and modifies the ui
     * @param phase current phase
     */
    public void setPhase(Phase phase){
        currentPhase = phase;
        phaseProperty.set(PropertiesManager.getString("Game.Phase", "lang") +": " + phase);
    }

    /**
     * Delegates key events to the current phase and define what will happen on key pressed
     * @param scene the Scene object for registering the key
     */
    public void setOnKeyPressed(Scene scene){
        scene.setOnKeyPressed(event -> currentPhase.setOnKeyPressed(event));
    }

    /**
     * TODO: Move this method somewhere else...
     * Resize all patches in the game
     * @param newWidth the window's new width
     * @param newHeight the window's new height
     */
    public void resize(double newWidth, double newHeight) {
        // Only resize, if it is allowed
        if(PropertiesManager.getBoolean("window.resizable","window")) {
            // Calculate the factors
            double factorX = newWidth / BASE_WIDTH;
            double factorY = newHeight / BASE_HEIGHT;

            // Scale the patches according to the stage size and only if it is allowed
            continentService.resizePatches(factorX, factorY);
            redrawLines(newWidth);

            // Set the capital text to the correct position
            moveCapitalText();
        }
    }

    private void moveCapitalText() {
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            // If the text is available: Move it
            if(capitalMap.containsKey(country.getBaseName())) {
                Text text = capitalMap.get(country.getBaseName());
                Point2D capital = country.getCapital();
                text.setX(capital.getX());
                text.setY(capital.getY());
            }
            // Else: Create the text
            else {
                resetCapitalText();
            }
        }));
    }

    /**
     * Draws the lines, connecting the neighboring countries
     * @param width the window's current width
     */
    private void redrawLines(double width) {
        // Remove all lines, which might be present due to already drawn once
        gameGroup.getChildren().removeAll(gameGroup.getChildren().stream()
                .filter(n -> n instanceof Line)
                .collect(Collectors.toList()));

        // Add lines, which connect the countries with their neighbors
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            // Draw the line between the country and the country's neighbors
            country.getNeighbors().forEach(neighbor -> {
                Line line;
                // Check if the line length would be greater than the half of the window width
                // If this is the case, then just draw the line to 0 or window width
                if(Math.abs((country.getCapital().getX()-neighbor.getCapital().getX())) > width/2){
                    // Check if the start position is on the left or right half of the screen and set the correct end x position
                    if(country.getCapital().getX() < width/2)
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), 0, neighbor.getCapital().getY());
                    else
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), width, neighbor.getCapital().getY());
                }
                else
                    line = new Line(country.getCapital().getX(), country.getCapital().getY(), neighbor.getCapital().getX(), neighbor.getCapital().getY());

                // Add the lines at index 0, so that all other elements will be drawn over it
                gameGroup.getChildren().add(0, line);
            });
        }));
    }

    public Parent getView(){
        return gamePane;
    }
}