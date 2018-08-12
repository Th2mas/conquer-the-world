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
     * The player's name
     */
    private String name;

    /**
     * Indicates, if the player is a real player, or not
     */
    private boolean ai;

    /**
     * Indicates, if the player can move
     */
    private boolean move;

    // Don't let the programmer create a plain player object
    private Player(){}

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
     * Returns the player's name
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's name
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
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
            countryMap.put(c, armies);
            setArmies(getArmies()-getArmies(c)+armies);
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
     * Checks, if the player is a real person or not
     * @return true, if player is controlled by a computer; false, if the player is a real person
     */
    public boolean isAi() {
        return ai;
    }

    public void setCountryMap(Map<Country, Integer> countryMap) {
        this.countryMap = countryMap;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }

    public boolean canBeMoved() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    /**
     * Returns the number of armies for the given country
     * @param c the country to be checked
     * @return the number of armies for {@link Country} c
     */
    public int getArmies(Country c){
        return countryMap.getOrDefault(c, 0);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Player: " + name + "=[");
        for(Country c : countryMap.keySet()) s.append(c.getName()).append("=").append(getArmies(c)).append(",");
        if(s.charAt(s.length()-1) == ',') s.deleteCharAt(s.length()-1);
        s.append("]");
        return s.toString();
    }

    /**
     * A simple builder for a player
     */
    public static final class PlayerBuilder {
        private Map<Country, Integer> countryMap;

        private Color color;

        private int armies;

        private String name;

        private boolean ai;

        private boolean move;

        public PlayerBuilder name(String name){
            this.name = name;
            return this;
        }

        public PlayerBuilder armies(int armies){
            this.armies = armies;
            return this;
        }

        public PlayerBuilder color(Color color){
            this.color = color;
            return this;
        }

        public PlayerBuilder ai(boolean ai){
            this.ai = ai;
            return this;
        }

        public PlayerBuilder countryMap(Map<Country, Integer> countryMap){
            this.countryMap = countryMap;
            return this;
        }

        public PlayerBuilder move(boolean move){
            this.move = move;
            return this;
        }

        public Player build(){
            Player player = new Player();
            player.armies = new SimpleIntegerProperty();

            player.setName(name);
            player.setArmies(armies);
            player.setColor(color);
            player.setAi(ai);
            player.setCountryMap(countryMap);
            player.setMove(move);

            return player;
        }
    }
}
