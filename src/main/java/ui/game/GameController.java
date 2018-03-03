package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

    private final Optional<List<Continent>> continentList;
    private Phase currentPhase;
    private final Player player;
    private final Player ai;
    private final StringProperty phaseProperty;
    private final StringProperty armiesProperty;

    public GameController(Optional<List<Continent>> continentList, Label lbl_phase, Label lbl_armies){
        this.continentList = continentList;
        this.currentPhase = Phase.ACQUISITION;

        // TODO Later: Request the player's name and color
        // Create the player
        player = new Player();

        // Create the oponent
        ai = new Player();
        ai.setColor(Color.GREY);

        // Bind the phase to the phase label
        phaseProperty = new SimpleStringProperty("Phase: " + Phase.ACQUISITION);
        lbl_phase.textProperty().bindBidirectional(phaseProperty);

        // Bind the number of armies to the armies label
        armiesProperty = new SimpleStringProperty("Armies: " + "0");
        lbl_armies.textProperty().bindBidirectional(armiesProperty);
        player.armiesProperty().addListener(((observable, oldValue, newValue) -> armiesProperty.set(newValue+"")));
    }

    /**
     * Starts the phases
     */
    public void start(){

        // Start the game
        continentList.ifPresent(continents -> continents.forEach(continent -> continent.getCountries().forEach(country -> {

            // CLICKED
            country.getPatches().forEach(patch -> patch.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
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
                                Continent randomContinent = continents.get((int)(Math.random()*continents.size()));
                                Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));
                                if(!ai.hasCountry(test) && !player.hasCountry(test)) randomCountry = test;
                            }

                            ai.addCountry(randomCountry);
                            randomCountry.getPatches().forEach(polygon -> polygon.setFill(ai.getColor()));
                        }

                        // Check if all countries are assigned to a player
                        int sumCountries = 0;
                        for(Continent cont : continents) sumCountries += cont.getCountries().size();

                        // Check if the phase switches to 'CONQUERING'
                        if(sumCountries == (ai.sizeCountries()+player.sizeCountries())) {
                            currentPhase = Phase.CONQUERING_ARMY_PLACEMENT;
                            phaseProperty.set("Phase: " + currentPhase);
                            // TODO: Set another text for the phaseproperty -> read it from a specific language properties file

                            // Set the armies for the player
                            setArmies(player);
                            armiesProperty.set("Armies: " + player.getArmies());

                            setArmies(ai);
                        }
                        break;
                    case CONQUERING_ARMY_PLACEMENT:
                        // TODO: Print the number of armies at the capital
                        break;
                }
            }));

            // DRAGGED
            country.getPatches().forEach(patch -> patch.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
                switch (currentPhase){
                    case CONQUERING_MOVE_AND_ATTACK:
                        // TODO: Implement this functionality on dragged
                        break;
                }
            }));
        })));

        // TODO: Implement conquering phase!
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
        continentList.ifPresent(continents -> {
            // Set the total number of armies the players have
            int totalArmies = player.sizeCountries();
            for(Continent con : continents) if(hasContinent(player, con)) totalArmies+=con.getPoints();

            // Divide by three, so you get the total number of armies you can place
            totalArmies /= 3;
            player.setArmies(totalArmies);
        });
    }
}