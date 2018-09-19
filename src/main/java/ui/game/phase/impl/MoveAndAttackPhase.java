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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.game.GameController;
import ui.game.phase.Phase;
import util.properties.PropertiesManager;

import java.util.Objects;

/**
 * The move and attack phase
 */
public class MoveAndAttackPhase implements Phase {

    /**
     * The {@link MoveAndAttackPhase} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveAndAttackPhase.class);

    private GameController gameController;

    private boolean drag;

    MoveAndAttackPhase(GameController gameController) {
        LOGGER.info("Initialize");
        Objects.requireNonNull(gameController);

        this.gameController = gameController;
        drag = false;
    }

    @Override
    public void click(Country country) {

        // Check if the player has the country and enough armies
        // TODO make it 'glow', if the player wants to use that country for attacking
        // @Glow: Make it darker and brighter over time -> Maybe this will be an effect object!
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
                new EndRoundPhase(gameController);
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return PropertiesManager.getString("Game.Phase.MoveAndAttack", "lang");
    }
}
