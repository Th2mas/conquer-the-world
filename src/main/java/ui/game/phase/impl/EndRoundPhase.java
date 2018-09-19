package ui.game.phase.impl;

import dto.Country;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.game.GameController;
import ui.game.phase.Phase;
import util.properties.PropertiesManager;

/**
 * The end round phase
 */
public class EndRoundPhase implements Phase {

    /**
     * The {@link EndRoundPhase} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EndRoundPhase.class);

    private GameController gameController;

    public EndRoundPhase(GameController gameController) {
        LOGGER.info("Initialize");
        this.gameController = gameController;

        // TODO: Something is not working here...
        //gameController.getPlayerService().nextTurn();
        gameController.setPhase(new ArmyPlacementPhase(gameController));
    }

    @Override
    public void click(Country country) {
        //TODO: Implement 'click' in " + EndRoundPhase.class.getName());
    }

    @Override
    public void dragDetect(Country country) {
        //TODO: Implement 'dragDetect' in " + EndRoundPhase.class.getName());
    }

    @Override
    public void dragDrop(double x, double y, Country country) {
        //TODO: Implement 'dragDrop' in " + EndRoundPhase.class.getName());
    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

    }

    @Override
    public String toString() {
        return PropertiesManager.getString("Game.Phase.EndRound", "lang");
    }
}
