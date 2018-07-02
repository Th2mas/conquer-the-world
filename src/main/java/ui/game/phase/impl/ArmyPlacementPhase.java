package ui.game.phase.impl;

import dto.Country;
import dto.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ui.game.GameController;
import ui.game.phase.Phase;

import java.util.Random;

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
        gameController.showArmiesForPlayer(currentPlayer);

        // Set an army randomly for the ai
        //Country random = gameController.getAi().getCountries().get(new Random().nextInt(gameController.getAi().getCountries().size()));
        //gameController.getAi().placeArmies(random);
        // TODO: Set armies for the bots

        // Show the newly placed armies
        gameController.showArmies();

        // Check if the phase switches to 'CONQUERING_MOVE_AND_ATTACK'
        if(currentPlayer.getArmies()==0) gameController.setPhase(new MoveAndAttackPhase(gameController));
    }

    @Override
    public void dragDetect(Country country) {
        //TODO: Implement 'dragDetect' in " + ArmyPlacementPhase.class.getName());
    }

    @Override
    public void dragDrop(MouseEvent event, Country country) {
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
