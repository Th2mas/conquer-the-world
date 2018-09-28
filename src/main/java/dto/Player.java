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
     * @param country {@link Country} to be added
     */
    public void addCountry(Country country){
        countryMap.put(country, 1);
    }

    /**
     * Removes the given country from the country list
     * @param country {@link Country} to be removed
     */
    public void removeCountry(Country country){
        countryMap.remove(country);
    }

    /**
     * Clears the player's map of countries
     */
    public void clearCountryMap() { this.countryMap = new HashMap<>();}

    /**
     * Returns a list with all countries the player currently has
     * @return the country list
     */
    public List<Country> getCountries(){
        return new ArrayList<>(countryMap.keySet());
    }

    /**
     * Checks if the player's country list contains the given country
     * @param country {@link Country} to be checked
     * @return true, if the player has the country; otherwise false
     */
    public boolean hasCountry(Country country){
        return countryMap.containsKey(country);
    }

    /**
     * Checks if the given player has the given continent
     * @param continent the continent to be checked
     * @return true, if the player has conquered the continent; false otherwise
     */
    public boolean hasContinent(Continent continent) {
        boolean returnValue = true;
        List<Country> countries = continent.getCountries();
        for(Country country : countries) {
            if(!hasCountry(country))
                returnValue = false;
        }
        return returnValue;
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
     * Adds the given number of armies to the current amount
     * @param armies number to be added
     */
    public void addArmies(int armies) { setArmies(this.armies.get()+armies); }

    /**
     * Removes the given number of armies from the current amount
     * @param armies number to be subtracted
     */
    public void removeArmies(int armies) { addArmies(armies*-1);}

    /**
     * Sets the number of armies for the given country
     * @param country the {@link Country} for which the number of armies should be set
     * @param armies the number of armies to be set
     */
    public void addArmies(Country country, int armies){
        if(hasCountry(country))
            countryMap.put(country, (countryMap.get(country))+armies);
    }

    /**
     * Remove the given number of armies from the given country
     * @param country the {@link Country} from which the armies should be removed
     * @param armies the number of armies to be removed
     */
    public void removeArmies(Country country, int armies) {
        addArmies(country, armies*-1);
    }

    /**
     * Increments the number of armies for the given country, if the player has the given country
     * Decrements the number of total armies, if the player has the given country
     * @param country indicates which {@link Country} counter should be incremented
     */
    public void placeArmies(Country country){
        addArmies(country, 1);
    }

    /**
     * Checks, if the player is a real person or not
     * @return true, if player is controlled by a computer; false, if the player is a real person
     */
    public boolean isAi() {
        return ai;
    }

    /**
     * Sets the map for the player's countries
     * @param countryMap the new map
     */
    private void setCountryMap(Map<Country, Integer> countryMap) {
        this.countryMap = countryMap;
    }

    /**
     * Sets the flag, indicating if the player is an ai or not
     * @param ai true, if ai; false otherwise
     */
    private void setAi(boolean ai) {
        this.ai = ai;
    }

    /**
     * Checks, if it is the player's turn in the game
     * @return true, if player can be moved; false otherwise
     */
    public boolean canBeMoved() {
        return move;
    }

    /**
     * Sets the flag, indicating if the player can be moved or not
     * @param move true, if can be moved; false otherwise
     */
    public void setMove(boolean move) {
        this.move = move;
    }

    /**
     * Returns the number of armies for the given country
     * @param country the country to be checked
     * @return the number of armies for {@link Country} c
     */
    public int getArmies(Country country){
        return countryMap.getOrDefault(country, 0);
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

        /**
         * The player's countries. Per default this map is empty
         */
        private Map<Country, Integer> countryMap = new HashMap<>();

        /**
         * The player's color, which will be used for coloring the countries. Per default this color is red
         */
        private Color color = Color.RED;

        /**
         * The player's armies. Per default the player has no armies at the beginning
         */
        private int armies = 0;

        /**
         * The player's name. Per default this name is empty
         */
        private String name = "";

        /**
         * A flag, indicating if the player is a person or just a computer. Per default the player is not a person
         */
        private boolean ai = false;

        /**
         * A flag, indicating if it is the player's turn to move. Per default this is false
         */
        private boolean move = false;

        /**
         * The setter for the player name
         * @param name the player's name
         * @return the builder
         */
        public PlayerBuilder name(String name){
            this.name = name;
            return this;
        }

        /**
         * The setter for the player's armies
         * @param armies the player's armies
         * @return the builder
         */
        public PlayerBuilder armies(int armies){
            this.armies = armies;
            return this;
        }

        /**
         * The setter for the player's color
         * @param color the player's color
         * @return the builder
         */
        public PlayerBuilder color(Color color){
            this.color = color;
            return this;
        }

        /**
         * The setter for the flag, if the player is a computer or not
         * @param ai the flag
         * @return the builder
         */
        public PlayerBuilder ai(boolean ai){
            this.ai = ai;
            return this;
        }

        /**
         * The setter for the player's country map
         * @param countryMap the player's country map
         * @return the builder
         */
        public PlayerBuilder countryMap(Map<Country, Integer> countryMap){
            this.countryMap = countryMap;
            return this;
        }

        /**
         * The setter for the flag, whether it's the player's turn or not
         * @param move the flag
         * @return the builder
         */
        public PlayerBuilder move(boolean move){
            this.move = move;
            return this;
        }

        /**
         * Builds the player with the current properties
         * @return the built player
         */
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
