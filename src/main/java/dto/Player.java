package dto;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation for the player
 * TODO Later: Move the CRUD methods to a service and store it in a database (or file) -> needed for saving the game
 */
public class Player {

    /**
     * The player's country list
     */
    private List<Country> countryList;

    /**
     * The player's color
     */
    private Color color;

    /**
     * Creates a default player with an empty country list and default color red
     */
    public Player(){
        this.countryList = new ArrayList<>();
        this.color = Color.RED;
    }

    /**
     * Adds the given country to the country list
     * @param c {@link Country} to be added
     */
    public void addCountry(Country c){
        countryList.add(c);
    }

    /**
     * Removes the given country from the country list
     * @param c {@link Country} to be removed
     */
    public void removeCountry(Country c){
        countryList.remove(c);
    }

    /**
     * Checks if the player's countrylist contains the given country
     * @param c {@link Country} to be checked
     * @return true, if the player has the country; otherwise false
     */
    public boolean hasCountry(Country c){
        return countryList.contains(c);
    }

    /**
     * Returns the size of the country list
     * @return the number of elements in the country list
     */
    public int sizeCountries(){
        return countryList.size();
    }

    /**
     * Gets the player's color
     * @return the player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the player's color
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
