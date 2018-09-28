package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.AttackOwnCountryException;
import exceptions.NotEnoughArmiesException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.game.GameController;
import ui.game.phase.Phase;
import util.dialog.DialogHelper;
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

    private final GameController gameController;

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

        Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();

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

        // Define what to do, if a player has won
        if(currentPlayer.getCountries().size() == gameController.getCapitalMap().keySet().size()){
            String msg = String.join(" ",
                    PropertiesManager.getString("Game.Player", "lang"),
                    currentPlayer.getName(),
                    PropertiesManager.getString("Dialog.Won", "lang")
            );

            DialogHelper.createInformationDialog(msg).showAndWait();
            LOGGER.info(msg);

            Alert confirmation =
                    DialogHelper.createConfirmationDialog(PropertiesManager.getString("Dialog.Content.StartNewGame", "lang"));
            confirmation.showAndWait().ifPresent(result -> {
                // Decide what to do, if player clicks on ok
                if(result == ButtonType.OK) {
                    LOGGER.info("Player wants to start a new game");
                    gameController.resetCapitalText();
                    gameController.setPhase(new AcquisitionPhase(gameController));
                }
                // Else just finish the game
                else {
                    LOGGER.info("Player exits the game");
                    System.exit(0);
                }
            });
        }
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
