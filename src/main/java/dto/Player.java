package dto;

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
     * Creates a default player with an empty country list
     */
    public Player(){
        this.countryList = new ArrayList<>();
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
}
