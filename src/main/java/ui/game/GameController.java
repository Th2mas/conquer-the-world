package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import service.PlayerService;
import service.impl.SimplePlayerService;
import ui.MainController;
import ui.game.phase.Phase;
import ui.game.phase.impl.AcquisitionPhase;
import ui.game.phase.impl.ArmyPlacementPhase;
import ui.game.phase.impl.EndRoundPhase;
import ui.game.phase.impl.MoveAndAttackPhase;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.*;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

    /**
     * Contains all continents
     */
    private List<Continent> continentList;

    /**
     * Contains the {@link Text} objects for every country
     */
    private Map<Country, Text> capitalMap;

    @FXML
    public BorderPane gamePane;

    @FXML
    public Pane gameBottom;


    /**
     * A container for the game group
     */
    @FXML
    public Pane gameContainer;

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
     * Handles the different languages
     */
    private PropertiesManager langManager;

    /**
     * Handles the different settings
     */
    private PropertiesManager settingsManager;

    private InformationController informationController;

    // Sets the selected country
    private Country selectedCountry;

    // TODO: Switch to Spring for Field injection! (e.g. lang-, or settingsManager)

    @FXML
    public void initialize(){
        // Load the game's content
        LoaderController loaderController = new LoaderController(gameGroup);

        // TODO Optional: Maybe toggle colors?
        loaderController.setColors();
        loaderController.darkenPatchesOnMouseOver();

        continentList = loaderController.getContinentList();

        this.langManager = new PropertiesManager("properties/lang");
        this.settingsManager = new PropertiesManager("properties/settings");

        this.currentPhase = new AcquisitionPhase(this, langManager);
        this.capitalMap = new HashMap<>();

        // Set a new player service
        playerService = new SimplePlayerService(settingsManager);

        // Create the capital text objects and put them into the map
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = new Text(country.getCapital().getX(), country.getCapital().getY(), "");
            text.setFill(Color.BLACK);
            capitalMap.put(country, text);
            gamePane.getChildren().add(text);
            text.setVisible(false);
        }));

        // TODO Later: Request the player's name and color
        // Create the player
        Player player = playerService.createPlayer("Game.Player", false);
        player.setMove(true);

        // Create the oponents
        Player ai = playerService.createPlayer("Ai1", true);
        ai.setColor(Color.GREY);    // TODO: MOVE this to CSS

        // Bind the phase to the phase label
        phaseProperty = new SimpleStringProperty(langManager.getString("Game.Phase") +": " + currentPhase);

        // Bind the number of armies to the armies label
        armiesProperty = new SimpleStringProperty(langManager.getString("Game.Armies") +": " + 0);
        player.armiesProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("Old armies: " + oldValue + ", new armies: " + newValue);
            armiesProperty.set(langManager.getString("Game.Armies") +": " + newValue);
        }));

        // Initialize the bottom pane
        initInformationPane();

        // Get the labels and add bind their text properties to the phase property
        gamePane.setCenter(gameContainer);
        gamePane.setBottom(gameBottom);
        gameBottom.getChildren().add(informationController.getView());
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
            System.out.println("Current" + langManager.getString("Game.Player") +": " + currentPlayer.getName());

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
                    new EndRoundPhase(this, langManager);
                    // TODO: Fix next round phase (some stack trace...)
                }
            }

            if(currentPlayer.getCountries().size() == capitalMap.keySet().size()){
                System.out.println(langManager.getString("Game.Player") + currentPlayer.getName() + " has won!");
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
        armiesProperty.set(langManager.getString("Game.Armies") +": " + player.getArmies());
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
        phaseProperty.set(langManager.getString("Game.Phase") +": " + phase);
    }

    /**
     * Delegates key events to the current phase
     * @param scene the Scene object for registering the key
     */
    public void setOnKeyPressed(Scene scene){
        // Define what will happen on key pressed
        scene.setOnKeyPressed(event -> currentPhase.setOnKeyPressed(event));
    }

    // TODO: Do something about the duplicate code here...
    private void initInformationPane(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("/fxml/InfoPane.fxml"));
            loader.load();

            informationController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parent getView(){
        return gamePane;
    }
}