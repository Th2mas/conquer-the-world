package dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation for the player
 * TODO Later: Move the CRUD methods to a service and store it in a database (or file) -> needed for saving the game
 */
public class Player {

    /**
     * The player's country map -> stores the country and the amount of armies
     */
    private Map<Country, Integer> countryMap;

    /**
     * The player's color
     */
    private Color color;

    /**
     * The player's armies
     */
    private IntegerProperty armies;

    /**
     * Creates a default player with an empty country list and default color red
     */
    public Player(){
        this.countryMap = new HashMap<>();
        this.color = Color.RED;
        this.armies = new SimpleIntegerProperty(0);
    }

    /**
     * Adds the given country to the country list and sets the number of armies for that country to 1
     * @param c {@link Country} to be added
     */
    public void addCountry(Country c){
        countryMap.put(c, 1);
    }

    /**
     * Removes the given country from the country list
     * @param c {@link Country} to be removed
     */
    public void removeCountry(Country c){
        countryMap.remove(c);
    }

    /**
     * Returns a list with all countries the player currently has
     * @return the country list
     */
    public List<Country> getCountries(){
        return new ArrayList<>(countryMap.keySet());
    }

    /**
     * Checks if the player's countrylist contains the given country
     * @param c {@link Country} to be checked
     * @return true, if the player has the country; otherwise false
     */
    public boolean hasCountry(Country c){
        return countryMap.containsKey(c);
    }

    /**
     * Returns the size of the country list
     * @return the number of elements in the country list
     */
    public int sizeCountries(){
        return countryMap.size();
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

    /**
     * Returns the number of armies, the player currently has
     * @return number of armies
     */
    public int getArmies() {
        return armies.get();
    }

    /**
     * Returns the armies property
     * @return armies
     */
    public IntegerProperty armiesProperty() {
        return armies;
    }

    /**
     * Sets the current number of armies
     * @param armies number to be set
     */
    public void setArmies(int armies) {
        this.armies.set(armies);
    }

    /**
     * Sets the number of armies for the given country
     * @param c the {@link Country} for which the number of armies should be set
     * @param armies the number of armies to be set
     */
    public void setArmies(Country c, int armies){
        if(hasCountry(c)) {
            int oldValue = countryMap.get(c);
            countryMap.put(c, armies);
            this.armies.set(this.armies.get()-oldValue+armies);
        }
    }

    /**
     * Increments the number of armies for the given country, if the player has the given country
     * Decrements the number of total armies, if the player has the given country
     * @param c indicates which {@link Country} counter should be incremented
     */
    public void placeArmies(Country c){
        if(hasCountry(c) && getArmies() > 0){
            countryMap.put(c, (countryMap.get(c)+1));
            armies.set(armies.get()-1);
        }
    }

    /**
     * Returns the number of armies for the given country
     * @param c the country to be checked
     * @return the number of armies for {@link Country} c
     */
    public int getArmies(Country c){
        return countryMap.getOrDefault(c, 0);
    }
}
