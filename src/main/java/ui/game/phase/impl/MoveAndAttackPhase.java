package ui.game.phase.impl;

import dto.Continent;
import dto.Country;
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

        // Check if the player has the country and enough armies -> make it 'glow', if the player wants to use that country for attacking
        // TODO Optional: Alternating color effect
        if(gameController.getPlayer().hasCountry(country)){

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
    }

    @Override
    public void dragDetect(Country country) {
        gameController.selectCountry(country);
        drag = true;
    }

    @Override
    public void dragDrop(MouseEvent event, Country country) {
        // Only if drag==true, we have an actual drag
        if(drag) {
            // Get the country at the current mouse location
            Country releasedCountry = null;
            for(Continent continent : gameController.getContinentList()) for(Country c : continent.getCountries()) for(Polygon polygon : c.getPatches()) if(polygon.contains(event.getX(), event.getY())) releasedCountry = c;
            // Check if we have selected a country and if it is in our range
            if(releasedCountry != null && country.hasNeighbor(releasedCountry)) {

                // Check if the country on which the mouse was released is an enemy
                if(gameController.getAi().hasCountry(releasedCountry)){
                    // Attack the country
                    gameController.attack(gameController.getPlayer(), gameController.getAi(), gameController.getSelectedCountry(), releasedCountry);
                }
                // Otherwise it is our own and we can move our troops
                else {
                    // TODO Optional: Let the player decide how many armies he wants to move

                    if(gameController.getPlayer().getArmies(country) > 1){
                        int amount = gameController.getPlayer().getArmies(country)-1;
                        gameController.getPlayer().setArmies(country, 1);
                        gameController.getPlayer().setArmies(releasedCountry, gameController.getPlayer().getArmies(releasedCountry)+amount);
                    }
                }
            }
        }
        gameController.showArmies();
    }

    @Override
    public void setOnKeyPressed(KeyEvent event) {

        switch(event.getText().toLowerCase()){
            case "e":
                gameController.setPhase(new EndRoundPhase(gameController));
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
