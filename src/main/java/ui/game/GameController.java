package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PlayerService;
import service.impl.SimplePlayerService;
import ui.game.phase.Phase;
import ui.game.phase.impl.AcquisitionPhase;
import ui.game.phase.impl.ArmyPlacementPhase;
import ui.game.phase.impl.EndRoundPhase;
import ui.game.phase.impl.MoveAndAttackPhase;
import util.dialog.DialogHelper;
import util.fxml.FXMLHelper;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Country, Text> capitalMap;

    /**
     * The pane containing the game elements (containing gameBottom and gameContainer)
     */
    @FXML
    public VBox gamePane;

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
    private PlayerService playerService;

    /**
     * The property, which notifies all listener, if the phase was changed
     */
    private StringProperty phaseProperty;

    /**
     * The property, which notifies all listener, if the number of armies was changed
     */
    private StringProperty armiesProperty;

    /**
     * The controller for the additional information
     */
    private InformationController informationController;

    /**
     * Sets the selected country
     */
    private Country selectedCountry;

    @FXML
    public void initialize(){

        LOGGER.info("Initialize");

        // Load the game's content
        LoaderController loaderController = new LoaderController(gameGroup);

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        continentList = loaderController.getContinentList();

        currentPhase = new AcquisitionPhase(this);
        capitalMap = new HashMap<>();

        // Set a new player service
        playerService = new SimplePlayerService();

        // Initialize the bottom pane
        try {
            informationController = FXMLHelper.loadFXMLController("/fxml/InfoPane.fxml");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return;
        }

        // Get the labels and bind their text properties to the respective properties
        gameBottom.getChildren().add(informationController.getView());

        // Create the capital text objects and put them into the map
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = new Text(country.getCapital().getX(), country.getCapital().getY(), "");
            text.setFill(Color.BLACK);
            capitalMap.put(country, text);
            gameGroup.getChildren().add(text);
            text.setVisible(false);

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

        // Create the oponents
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

            if(currentPlayer.isAi()){
                while(currentPhase.getClass() == ArmyPlacementPhase.class){
                    Country randomCountry = currentPlayer.getCountries().get((int) (Math.random() * currentPlayer.getCountries().size()));
                    currentPhase.click(randomCountry);
                }
                if(currentPhase.getClass() == MoveAndAttackPhase.class){
                    List<Country> countries = currentPlayer.getCountries();
                    for(Country country : countries){
                        currentPhase.dragDetect(country);
                        for(Country neighbor : country.getNeighbors()){
                            currentPhase.dragDrop(neighbor.getCapital().getX(), neighbor.getCapital().getY(), country);
                        }
                    }
                    new EndRoundPhase(this);
                    // TODO: Fix next round phase (some stack trace...)
                }
            }

            if(currentPlayer.getCountries().size() == capitalMap.keySet().size()){
                String msg = String.join(" ",
                        PropertiesManager.getString("Game.Player", "lang"),
                        currentPlayer.getName(),
                        PropertiesManager.getString("Dialog.Won", "lang")
                );

                DialogHelper.createInformationDialog(msg);
                LOGGER.info(msg);
            }
        }));
    }

    /**
     * Checks, if the given player has conquered the given continent
     * @param player the player to be checked
     * @param continent the continent to be checked
     * @return true, if the player has conquered all countries of the given continent; otherwise false
     */
    private boolean hasContinent(Player player, Continent continent){
        boolean check = true;
        for(Country country : continent.getCountries())
            if(!player.hasCountry(country)) check = false;
        return check;
    }

    /**
     * Sets the armies for the given player
     * @param player the player for whom the number of armies needs to be calculated
     */
    public void setArmies(Player player){
        // Set the total number of armies the players have
        int totalArmies = player.sizeCountries();
        for(Continent con : continentList) if(hasContinent(player, con)) totalArmies+=con.getPoints();

        // Divide by three, so you get the total number of armies you can place
        totalArmies /= 3;
        player.setArmies(totalArmies);
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
     * Returns the gamecontroller's playerservice
     * @return {@link PlayerService}
     */
    public PlayerService getPlayerService() {
        return playerService;
    }

    /**
     * Draws all armies for every player (in black)
     */
    public void showArmiesOnCountries(){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = capitalMap.get(country);

            playerService.getPlayers().forEach(player -> {
                if(player.hasCountry(country)) text.setText(player.getArmies(country)+"");
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

    public Parent getView(){
        return gamePane;
    }
}