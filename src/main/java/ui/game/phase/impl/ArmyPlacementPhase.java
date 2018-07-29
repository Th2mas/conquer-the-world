package ui.game.phase.impl;

import dto.Country;
import dto.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ui.game.GameController;
import ui.game.phase.Phase;

/**
 * The army placement phase
 */
public class ArmyPlacementPhase implements Phase {

    private GameController gameController;

    public ArmyPlacementPhase(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void click(Country country) {

        Player currentPlayer = gameController.getPlayerService().getCurrentPlayer();

        // Increment the counter of the armies of the clicked country, if the player has the country
        // and has enough armies to place
        currentPlayer.placeArmies(country);
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
        return "Army Placement";
    }
}
