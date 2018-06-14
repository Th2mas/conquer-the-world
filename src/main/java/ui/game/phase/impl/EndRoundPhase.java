package ui.game.phase.impl;

import dto.Country;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ui.game.GameController;
import ui.game.phase.Phase;

/**
 * The end round phase
 */
public class EndRoundPhase implements Phase {

    private GameController gameController;

    EndRoundPhase(GameController gameController) {
        this.gameController = gameController;
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
    public void dragDrop(MouseEvent event, Country country) {
        //TODO: Implement 'dragDrop' in " + EndRoundPhase.class.getName());
    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

    }

    @Override
    public String toString() {
        return "End Round";
    }
}
