package ui.game.phase.impl;

import dto.Country;
import dto.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PlayerService;
import service.impl.SimplePlayerService;
import ui.game.GameController;
import ui.game.phase.Phase;
import util.properties.PropertiesManager;

/**
 * The army placement phase
 */
public class ArmyPlacementPhase implements Phase {

    /**
     * The {@link ArmyPlacementPhase} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArmyPlacementPhase.class);

    /**
     * The {@link GameController} containing game and ui relevant information
     */
    private GameController gameController;

    /**
     * Creates a new ArmyPlacementPhase and sets the information needed for this phase
     * @param gameController the gameController
     */
    ArmyPlacementPhase(GameController gameController) {
        LOGGER.info("Initialize");
        this.gameController = gameController;

        PlayerService playerService = new SimplePlayerService();

        Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();
        playerService.setArmies(currentPlayer, gameController.getContinentList());
    }

    @Override
    public void click(Country country) {
        LOGGER.info("Clicked");
        Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();

        // Increment the counter of the armies of the clicked country, if the player has the country
        // and has enough armies to place
        currentPlayer.placeArmies(country);
        currentPlayer.removeArmies(1);

        gameController.showArmiesLabel(currentPlayer);

        // Show the newly placed armies
        gameController.showArmiesOnCountries();

        // Check if the phase switches to 'CONQUERING_MOVE_AND_ATTACK'
        if(currentPlayer.getArmies()==0) gameController.setPhase(new MoveAndAttackPhase(gameController));
    }

    @Override
    public void dragDetect(Country country) {
        //TODO: Implement 'dragDetect' in " + ArmyPlacementPhase.class.getName());
    }

    @Override
    public void dragDrop(double x, double y, Country country) {
        //TODO: Implement 'dragDrop' in " + ArmyPlacementPhase.class.getName());
    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

    }

    @Override
    public String toString() {
        return PropertiesManager.getString("Game.Phase.ArmyPlacement", "lang");
    }
}
