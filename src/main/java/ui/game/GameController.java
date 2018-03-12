package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.NodeNotFoundException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import util.GuiUtil;

import java.util.*;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

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

    private Country startCountry;
    private boolean drag = false;

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
        this.currentPhase = Phase.ACQUISITION;
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
        phaseProperty = new SimpleStringProperty("Phase: " + Phase.ACQUISITION);

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
                patch.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    switch (currentPhase){

                        // Click during acquisition phase
                        case ACQUISITION:
                            // Check if the country is not assigned
                            if(!ai.hasCountry(country) && !player.hasCountry(country)) {
                                // Add the country to the player and change its color
                                player.addCountry(country);
                                country.getPatches().forEach(polygon -> polygon.setFill(player.getColor()));

                                // Add a random country of a random continent to the ai
                                Country randomCountry = null;
                                while(randomCountry == null){
                                    Continent randomContinent = continentList.get((int)(Math.random()*continentList.size()));
                                    Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));
                                    if(!ai.hasCountry(test) && !player.hasCountry(test)) randomCountry = test;
                                }

                                ai.addCountry(randomCountry);
                                randomCountry.getPatches().forEach(polygon -> polygon.setFill(ai.getColor()));
                            }

                            // Check if all countries are assigned to a player
                            int sumCountries = 0;
                            for(Continent cont : continentList) sumCountries += cont.getCountries().size();

                            // Check if the phase switches to 'CONQUERING_ARMY_PLACEMENT'
                            if(sumCountries == (ai.sizeCountries()+player.sizeCountries())) {
                                setPhase(Phase.CONQUERING_ARMY_PLACEMENT);
                                // TODO: Set another text for the phaseproperty -> read it from a specific language properties file
                                // TODO: Do the same for armiesproperty

                                // Set the armies for the player
                                setArmies(player);
                                armiesProperty.set("Armies: " + player.getArmies());

                                //TODO: Do the random adding as soon as the player has finished his round
                                // setArmies(ai);
                            }
                            break;

                        // Click during army placement phase
                        case CONQUERING_ARMY_PLACEMENT:

                            // Increment the counter of the armies of the clicked country, if the player has the country
                            // and has enough armies to place
                            player.placeArmies(country);
                            armiesProperty.set("Armies: " + player.getArmies());

                            // Set an army randomly for the ai
                            Country random = ai.getCountries().get(new Random().nextInt(ai.getCountries().size()));
                            ai.placeArmies(random);

                            // Show the newly placed armies
                            showArmies();

                            // Check if the phase switches to 'CONQUERING_MOVE_AND_ATTACK'
                            if(player.getArmies()==0) setPhase(Phase.CONQUERING_MOVE_AND_ATTACK);
                            break;
                    }
                });

                // DRAG DETECTED
                patch.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
                    switch (currentPhase){
                        case CONQUERING_MOVE_AND_ATTACK:
                            // Save the starting country
                            if(player.hasCountry(country)) startCountry = country;
                            drag = true;
                            break;
                    }
                });

                // ENTERED
                patch.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                    switch (currentPhase) {
                        case CONQUERING_MOVE_AND_ATTACK:

                            // TODO: Remove dragging bug: when you drag and stay in your country, the dragging
                            // is still on and you can move to another country and the armies will move too

                            // Check, if 'dragging' is on and the country is a neighbor
                            if(drag && startCountry != null && startCountry.hasNeighbor(country)){
                                // Attack a hostile country, if it is in the country's surrounding
                                if(!player.hasCountry(country)){
                                    System.out.println("ENEMY!");
                                    // TODO: Implement attacking
                                }

                                // Move the armies, if you have dragged into your country
                                // Leave min. one army in the country
                                // TODO: Let the player decide how many armies he wants to move
                                else if(player.getArmies(startCountry) > 1){
                                    System.out.println("MOVE!");
                                    int armies = player.getArmies(startCountry)-1;
                                    player.setArmies(startCountry, 1);
                                    player.setArmies(country, player.getArmies(country)+armies);
                                }
                            }

                            showArmies();
                            drag = false;
                            break;
                    }
                });
            });

        }));

        // Specify what will happen, if the phase changes
        phaseProperty.addListener(((observable, oldValue, newValue) -> {
            // Show the armies, if currentPhase != ACQUISITION
            if(!newValue.equalsIgnoreCase(Phase.ACQUISITION.toString()))
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
    private void setArmies(Player player){
        // Set the total number of armies the players have
        int totalArmies = player.sizeCountries();
        for(Continent con : continentList) if(hasContinent(player, con)) totalArmies+=con.getPoints();

        // Divide by three, so you get the total number of armies you can place
        totalArmies /= 3;
        player.setArmies(totalArmies);
    }

    /**
     * Draws all armies for every player (in black)
     */
    private void showArmies(){
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
    private void setPhase(Phase phase){
        currentPhase = phase;
        phaseProperty.set("Phase: " + currentPhase);
    }
}