package ui.game.phase;

import dto.Country;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The interface, which describes the actions in the game (State pattern)
 */
public interface Phase {

    /**
     * Defines what will happen on click with a country
     * @param country the {@link Country} that was used, as the CLICK happened
     */
    void click(Country country);

    /**
     * Defines what will happen on dragDetect on a country
     * @param country the {@link Country} that was used, as the DRAG happened
     */
    void dragDetect(Country country);

    /**
     * Defines what will happen on mouse dragDrop on a country
     * @param country should be the country, that was used as the RELEASE happened
     *                // TODO: Test this JAVADOC
     */
    void dragDrop(MouseEvent event, Country country);

    /**
     * Defines what will on a keyPressed
     * @param event
     */
    void setOnKeyPressed(KeyEvent event);
}
