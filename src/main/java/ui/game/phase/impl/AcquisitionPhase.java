package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
        // Check if the country is not assigned
        if(!gameController.getAi().hasCountry(country) && !gameController.getPlayer().hasCountry(country)) {
            // Add the country to the player and change its color
            gameController.getPlayer().addCountry(country);
            country.getPatches().forEach(polygon -> polygon.setFill(gameController.getPlayer().getColor()));

            // Add a random country of a random continent to the ai
            Country randomCountry = null;
            while(randomCountry == null){
                Continent randomContinent = gameController.getContinentList().get((int)(Math.random()*gameController.getContinentList().size()));
                Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));
                if(!gameController.getAi().hasCountry(test) && !gameController.getPlayer().hasCountry(test)) randomCountry = test;
            }

            gameController.getAi().addCountry(randomCountry);
            randomCountry.getPatches().forEach(polygon -> polygon.setFill(gameController.getAi().getColor()));
        }

        // Check if all countries are assigned to a player
        int sumCountries = 0;
        for(Continent cont : gameController.getContinentList()) sumCountries += cont.getCountries().size();

        // Check if the phase switches to 'CONQUERING_ARMY_PLACEMENT'
        if(sumCountries == (gameController.getAi().sizeCountries()+gameController.getPlayer().sizeCountries())) {
            gameController.setPhase(new ArmyPlacementPhase(gameController));
            // TODO: Set another text for the phaseproperty -> read it from a specific language properties file
            // TODO: Do the same for armiesproperty

            // Set the armies for the player
            gameController.setArmies(gameController.getPlayer());
            gameController.showArmiesForPlayer(gameController.getPlayer());

            //TODO: Do the random adding as soon as the player has finished his round
            // gameController.setArmies(ai);
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
