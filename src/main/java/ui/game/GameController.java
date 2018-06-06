package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.NodeNotFoundException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ui.game.phase.Phase;
import ui.game.phase.impl.AcquisitionPhase;
import util.GuiUtil;

import java.util.*;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

    /**
     * Defines how many armies can attack a country at once
     * TODO: Move me to a properties file and load it afterwards
     */
    private static final int MAX_ATTACK_ARMIES = 3;

    /**
     * Tells how many armies can defend a country at once
     * TODO: Move me to a properties file and load it afterwards
     */
    private static final int MAX_DEFENDING_ARMIES = 2;

    /**
     * Contains all continents
     */
    private final List<Continent> continentList;

    /**
     * Contains the {@link Text} objects for every country
     */
    private final Map<Country, Text> capitalMap;

    /**
     * Defines the current phase
     */
    private Phase currentPhase;

    /**
     * Stores all properties for the human player
     */
    private final Player player;

    /**
     * Stores all properties for the ai player
     */
    private final Player ai;

    /**
     * The property, which notifies all listener, if the phase was changed
     */
    private final StringProperty phaseProperty;

    /**
     * The property, which notifies all listener, if the number of armies was changed
     */
    private final StringProperty armiesProperty;

    /**
     * The group, which contains all elements, that will be drawn on the main pane
     */
    private final Group root;

    /**
     * Contains the elements, which will be drawn on the info pane
     */
    private final Pane infoPane;

    // Sets the selected country
    private Country selectedCountry;


    /**
     * The constructor for the game controller, which handles the actual game logic
     * @param continentList the list with all continents
     * @param root the {@link Group} containing all objects, that will be drawn in the main pane
     * @param infoPane the {@link Pane} containing all objects, that will be drawn in the info pane
     */
    public GameController(List<Continent> continentList, Group root, Pane infoPane){

        Objects.requireNonNull(continentList);
        Objects.requireNonNull(root);
        Objects.requireNonNull(infoPane);

        this.continentList = continentList;
        this.currentPhase = new AcquisitionPhase(this);
        this.capitalMap = new HashMap<>();

        // Create the capital text objects and put them into the map
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = new Text(country.getCapital().getX(), country.getCapital().getY(), "");
            text.setFill(Color.BLACK);
            capitalMap.put(country, text);
            root.getChildren().add(text);
            text.setVisible(false);
        }));

        this.root = root;
        this.infoPane = infoPane;

        // TODO Later: Request the player's name and color
        // Create the player
        player = new Player();

        // Create the oponent
        ai = new Player();
        ai.setColor(Color.GREY);

        // Bind the phase to the phase label
        phaseProperty = new SimpleStringProperty("Phase: " + currentPhase);

        // Bind the number of armies to the armies label
        armiesProperty = new SimpleStringProperty("Armies: " + "0");
        player.armiesProperty().addListener(((observable, oldValue, newValue) -> armiesProperty.set("Armies: " + newValue)));

        // Get the labels and add bind their text properties to the phase property
        try {
            Label lbl_armies = (Label) GuiUtil.searchNodeById(infoPane, "lbl_armies");
            Label lbl_phase = (Label) GuiUtil.searchNodeById(infoPane, "lbl_phase");

            lbl_phase.textProperty().bindBidirectional(phaseProperty);
            lbl_armies.textProperty().bindBidirectional(armiesProperty);
        } catch (NodeNotFoundException e) {
            System.err.println("Could not find given nodes");
        }
    }

    /**
     * Starts the phases
     */
    public void start(){

        // Start the game
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            country.getPatches().forEach(patch -> {

                // CLICKED
                patch.setOnMouseClicked(event -> currentPhase.click(country));

                // DRAGGED
                patch.setOnDragDetected(event -> currentPhase.dragDetect(country));
                patch.setOnMouseReleased(event -> currentPhase.dragDrop(event, country));

            });

        }));

        // Specify what will happen, if the phase changes
        phaseProperty.addListener(((observable, oldValue, newValue) -> {
            // Show the armies, if currentPhase != ACQUISITION TODO: Change the hardcoded string
            if(!newValue.equalsIgnoreCase("Acquisition"))
                showArmies();
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
     * Returns the human player object
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the ai player object
     * @return the opponent
     */
    public Player getAi() {
        return ai;
    }

    /**
     * Shows armies for the player on the screen
     * @param player the player which is used to display army information
     */
    public void showArmiesForPlayer(Player player){
        armiesProperty.set("Armies: " + player.getArmies());
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
     * Draws all armies for every player (in black)
     */
    public void showArmies(){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            Text text = capitalMap.get(country);

            if(player.hasCountry(country)) text.setText(player.getArmies(country)+"");
            else text.setText(ai.getArmies(country)+"");

            text.setVisible(true);
        }));
    }

    /**
     * Sets the given phase and modifies the ui
     * @param phase current phase
     */
    public void setPhase(Phase phase){
        currentPhase = phase;
        phaseProperty.set("Phase: " + phase);
    }

    /**
     * This method lets player1 attack player2
     * @param p1 the attacking player
     * @param p2 the defending player
     * @return true, if the country was conquered by player1
     * TODO: Should this method really be standing here, or better as an object method in a PlayerService class?
     */
    public boolean attack(Player p1, Player p2, Country attackCountry, Country defendCountry){

        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);
        Objects.requireNonNull(attackCountry);
        Objects.requireNonNull(defendCountry);

        // If the player has not enough armies in the attacking country, then nothing should happen
        if(p1.getArmies(attackCountry) == 1) return false;

        // If the attacking country has n armies, you can attack only attack with at most n-1 armies
        int attackingArmies;
        int defendArmies;

        // Decide how many armies can attack the opponent
        attackingArmies = (p1.getArmies(attackCountry) <= MAX_ATTACK_ARMIES) ? p1.getArmies(attackCountry)-1 : MAX_ATTACK_ARMIES;
        p1.setArmies(attackCountry, p1.getArmies(attackCountry)-attackingArmies);

        // Decide how many armies can defend the attacked country
        defendArmies = (p2.getArmies(defendCountry) <= MAX_DEFENDING_ARMIES) ? p2.getArmies(defendCountry) : MAX_DEFENDING_ARMIES;
        p2.setArmies(defendCountry, p2.getArmies(defendCountry)-defendArmies);

        // To save the thrown dices, we need to use an array
        Integer[] attackDices = new Integer[attackingArmies];
        Integer[] defendDices = new Integer[defendArmies];

        // Calculate the thrown dices
        for(int i=0; i<attackDices.length; i++) attackDices[i] = (int) (Math.random()*6)+1;
        for(int i=0; i<defendDices.length; i++) defendDices[i] = (int) (Math.random()*6)+1;

        // Sort the dice arrays in decscending order to compare the first elements
        Arrays.sort(attackDices, Collections.reverseOrder());
        Arrays.sort(defendDices, Collections.reverseOrder());

        // Check which army has the highest number per dice
        for(int i=0; i<defendDices.length; i++){
            System.out.println("Attack=[" + attackCountry.getName() + "," + attackDices[i] + "], Defend=[" + defendCountry.getName() + "," + defendDices[i] + "]");
            // If the attacking player has a larger number, the number of defending armies should be decremented
            if(attackDices[i] > defendDices[i]) defendArmies--;
            else attackingArmies--;
        }

        // If the country was conquered, remove it from the defending players list and add it to the attacking players list
        if(defendArmies == 0){
            p2.removeCountry(defendCountry);
            p1.addCountry(defendCountry);
            p1.setArmies(defendCountry, attackingArmies);

            // Change the defendCountry's color
            defendCountry.getPatches().forEach(polygon -> polygon.setFill(p1.getColor()));
            return true;
        }

        // If there are still some armies left, you have to return them to their respective countries
        p1.setArmies(attackCountry, p1.getArmies(attackCountry)+attackingArmies);
        p2.setArmies(defendCountry, p2.getArmies(defendCountry)+defendArmies);

        return false;
    }
}