package ui.game.phase.impl;

import dto.Country;
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
        // Increment the counter of the armies of the clicked country, if the player has the country
        // and has enough armies to place
        gameController.getPlayer().placeArmies(country);
        gameController.showArmiesForPlayer(gameController.getPlayer());

        // Set an army randomly for the ai
        Country random = gameController.getAi().getCountries().get(new Random().nextInt(gameController.getAi().getCountries().size()));
        gameController.getAi().placeArmies(random);

        // Show the newly placed armies
        gameController.showArmies();

        // Check if the phase switches to 'CONQUERING_MOVE_AND_ATTACK'
        if(gameController.getPlayer().getArmies()==0) gameController.setPhase(new MoveAndAttackPhase(gameController));
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
    public String toString() {
        return "Army Placement";
    }
}
