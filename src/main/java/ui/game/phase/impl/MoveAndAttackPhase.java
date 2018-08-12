package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.AttackOwnCountryException;
import exceptions.NotEnoughArmiesException;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ui.game.GameController;
import ui.game.phase.Phase;

/**
 * The move and attack phase
 */
public class MoveAndAttackPhase implements Phase {

    private GameController gameController;
    private boolean drag;

    MoveAndAttackPhase(GameController gameController) {
        this.gameController = gameController;
        drag = false;
    }

    @Override
    public void click(Country country) {

        // Check if the player has the country and enough armies
        // TODO make it 'glow', if the player wants to use that country for attacking
        /*
        if(gameController.getPlayerService().getCurrentPlayer().hasCountry(country)){

            // Check if there is a selected country
            if(gameController.getSelectedCountry() == null) {
                gameController.selectCountry(country);
                country.getPatches().forEach(polygon -> polygon.setFill(((Color)polygon.getFill()).darker()));
            }

            // Make the effect only, if the country hasn't already been selected
            if(gameController.getSelectedCountry() != country) {
                gameController.getSelectedCountry().getPatches().forEach(polygon -> polygon.setFill(((Color)polygon.getFill()).brighter()));
                country.getPatches().forEach(polygon -> polygon.setFill(((Color)polygon.getFill()).darker()));
                gameController.selectCountry(country);
            }
        }
        */
    }

    @Override
    public void dragDetect(Country country) {
        gameController.selectCountry(country);
        drag = true;
    }

    @Override
    public void dragDrop(double x, double y, Country country) {
        // Only if drag==true, we have an actual drag
        if(drag) {
            // Get the country at the current mouse location
            Country releasedCountry = null;
            for(Continent continent : gameController.getContinentList())
                for(Country c : continent.getCountries())
                    for(Polygon polygon : c.getPatches())
                        if(polygon.contains(x, y))
                            releasedCountry = c;

            // Check if we have selected a country and if it is in our range
            if(releasedCountry != null && country.hasNeighbor(releasedCountry)) {

                Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();

                // Attack the country
                try {
                    gameController.getPlayerService().attack(currentPlayer, gameController.getSelectedCountry(), releasedCountry);
                } catch (NotEnoughArmiesException e) {
                    // Do nothing, if there are not enough armies
                } catch (AttackOwnCountryException e) {

                    // The attacking country is our own country, so we can move the armies
                    // TODO Optional: Let the player decide how many armies he wants to move
                    gameController.getPlayerService().moveArmies(currentPlayer, country, releasedCountry);
                }
            }
        }
        gameController.showArmiesOnCountries();
    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

        switch(event.getText().toLowerCase()){
            case "e":
                //new EndRoundPhase(gameController);
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "Move And Attack";
    }
}
