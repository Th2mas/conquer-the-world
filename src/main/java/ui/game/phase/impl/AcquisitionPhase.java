package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import ui.game.GameController;
import ui.game.phase.Phase;

/**
 * The acquisition phase
 */
public class AcquisitionPhase implements Phase {

    private GameController gameController;

    public AcquisitionPhase(GameController gameController){
        this.gameController = gameController;
    }

    @Override
    public void click(Country country) {

        Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();


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

        while(currentPlayer.isAi()){
            Country randomCountry = null;
            while(randomCountry == null){
                Continent randomContinent = gameController.getContinentList().get((int)(Math.random()*gameController.getContinentList().size()));
                Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));

                if(gameController.getPlayerService().isCountryFree(test))
                    randomCountry = test;
            }

            currentPlayer.addCountry(randomCountry);
            for(Polygon p : randomCountry.getPatches()) p.setFill(currentPlayer.getColor());

            gameController.getPlayerService().nextTurn();
            currentPlayer = gameController.getPlayerService().getCurrentPlayer();
        }

        // Check if all countries are assigned to a player
        int sumCountries = gameController.getContinentList().stream().mapToInt(cont -> cont.getCountries().size()).sum();

        // Calculate the total number of conquered countries
        int sizes = gameController.getPlayerService().getPlayers().stream().mapToInt(Player::sizeCountries).sum();

        // Check if the phase switches to 'CONQUERING_ARMY_PLACEMENT'
        if(sumCountries == sizes) {
            gameController.setPhase(new ArmyPlacementPhase(gameController));
            // TODO: Set another text for the phaseproperty -> read it from a specific language properties file
            // TODO: Do the same for armiesproperty

            // Set the armies for the player
            gameController.setArmies(currentPlayer);
            gameController.showArmiesLabel(currentPlayer);
            gameController.showArmiesOnCountries();
        }
    }

    @Override
    public void dragDetect(Country country) {
        // TODO: Implement 'dragDetect' in " + AcquisitionPhase.class.getName());
    }

    @Override
    public void dragDrop(MouseEvent event, Country country) {
        // TODO: Implement 'dragDrop' in " + AcquisitionPhase.class.getName());

    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

    }

    // TODO: Make the text dependent from the language
    @Override
    public String toString() {
        return "Acquisition";
    }
}
