package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.impl.SimplePlayerService;
import ui.game.GameController;
import ui.game.phase.Phase;
import util.properties.PropertiesManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The acquisition phase
 */
public class AcquisitionPhase implements Phase {

    /**
     * The {@link AcquisitionPhase} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AcquisitionPhase.class);

    private final GameController gameController;

    /**
     * Creates a new acquisition phase
     * @param gameController the {@link GameController}, containing relevant information for this phase
     */
    public AcquisitionPhase(GameController gameController){
        LOGGER.info("Initialize");
        this.gameController = gameController;

        // Reset the game -> We need this, if the player has started a new game
        // TODO: Maybe add a boolean to check, if this is a new game?

        // Clear the country list of all players
        SimplePlayerService.getSimplePlayerService().getPlayers().forEach(Player::clearCountryMap);

        // Reset the color of the continents and their countries
        gameController.getContinentList().forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(patch -> patch.setFill(continent.getColor()));
        }));
    }

    @Override
    public void click(Country country) {

        Player currentPlayer = SimplePlayerService.getSimplePlayerService().getCurrentPlayer();

        // Check if the country is not assigned
        if(gameController.getPlayerService().isCountryFree(country)) {
            // Add the country to the player and change its color
            currentPlayer.addCountry(country);
            for(Polygon p : country.getPatches()) p.setFill(currentPlayer.getColor());
        }
        // Else the player has clicked on a conquered country: then just return
        else return;

        // Move and check if the next player is a real person. If it is a bot, then just add randomly a country
        gameController.getPlayerService().nextTurn();

        // If the next player is a bot, then just assign a random country to it
        currentPlayer = gameController.getPlayerService().getCurrentPlayer();

        while(currentPlayer.isAi()) {
            Country randomCountry = null;
            while(randomCountry == null) {
                int iRandomContinent = (int)(Math.random()*gameController.getContinentList().size());
                Continent randomContinent = gameController.getContinentList().get(iRandomContinent);
                Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));

                if(gameController.getPlayerService().isCountryFree(test))
                    randomCountry = test;
            }

            currentPlayer.addCountry(randomCountry);
            for(Polygon p : randomCountry.getPatches()) p.setFill(currentPlayer.getColor());

            // Change the user, so he can play as well (can also be another AI)
            gameController.getPlayerService().nextTurn();
            currentPlayer = gameController.getPlayerService().getCurrentPlayer();
        }

        // Get the total number of countries
        int sumCountries = gameController.getContinentList().stream().mapToInt(cont -> cont.getCountries().size()).sum();

        // Calculate the total number of conquered countries
        int sizes = gameController.getPlayerService().getPlayers().stream().mapToInt(Player::sizeCountries).sum();

        // Check if the phase switches to the army placement phase
        if(sumCountries == sizes) {
            gameController.setPhase(new ArmyPlacementPhase(gameController));
            gameController.showArmiesLabel(currentPlayer);
            gameController.showArmiesOnCountries();
        }
    }

    @Override
    public void dragDetect(Country country) {
        // TODO: Implement 'dragDetect' in " + AcquisitionPhase.class.getName());
    }

    @Override
    public void dragDrop(double x, double y, Country country) {
        // TODO: Implement 'dragDrop' in " + AcquisitionPhase.class.getName());

    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

    }

    @Override
    public String toString(){
        return PropertiesManager.getString("Game.Phase.Acquisition", "lang");
    }
}
