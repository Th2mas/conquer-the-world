package service;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.AttackOwnCountryException;
import exceptions.CountryNotAvailableException;
import exceptions.NotEnoughArmiesException;

import java.util.List;

/**
 * An interface for a player service
 */
public interface PlayerService {

    /**
     * Creates a new simple player
     * @return player
     */
    Player createPlayer();

    /**
     * Creates a new simple player with a given name and checks, if it's controlled by a computer or not
     * @param name the player's name
     * @param ai tells, if the player is controller by a computer or not
     * @return player
     */
    Player createPlayer(String name, boolean ai);

    /**
     * Returns a list of players
     * @return a list with all current players
     */
    List<Player> getPlayers();

    /**
     * Adds the given country to the player, if it does not belong to any other player
     * @param player the player the country needs to be assigned to
     * @param country the country, which should be assigned
     * @throws CountryNotAvailableException if country is not available
     */
    void addCountry(Player player, Country country) throws CountryNotAvailableException;

    /**
     * Sets the number of armies the player can use in a round
     * @param player the player to be used for calculation
     * @param continents the continents in the game
     */
    void setArmies(Player player, List<Continent> continents);

    /**
     * Moves all armies from 'from' to 'to', if both belong to the player
     * @param player the player
     * @param from the country from which the armies should be moved
     * @param to the country to which the armies should be moved
     */
    void moveArmies(Player player, Country from, Country to);

    /**
     * Player1 attacks the defending country
     * @param p1 the attacking player
     *
     */
    void attack(Player p1, Country attackCountry, Country defendingCountry) throws NotEnoughArmiesException, AttackOwnCountryException;

    /**
     * Conquers the defending country and moves all left armies to that country
     * @param p1 the player, to whom the country will belong to
     * @param p2 the player, from whom the country will be removed
     * @param attackCountry the attacking country
     * @param defendingCountry the defending country
     */
    void conquerCountry(Player p1, Player p2, Country attackCountry, Country defendingCountry);

    /**
     * Indicates the end of one players turn and lets the next player play
     */
    void nextTurn();

    /**
     * Checks if the given country is not conquered
     * @param country the country to be checked
     * @return true, if free; otherwise false
     */
    boolean isCountryFree(Country country);

    /**
     * Returns the current player
     * @return the current player
     */
    Player getCurrentPlayer();

    /**
     * Decides how many armies can be used for attacking
     * @param player the player used for calculation
     * @param attackingCountry the country attacking
     * @return the number of armies, which can be used for attacking
     */
    int getAttackingArmies(Player player, Country attackingCountry);

    /**
     * Decides how many armies can be used for defending
     * @param player the player used for calculation
     * @return the number of armies, which can be used for defending
     */
    int getDefendingArmies(Player player, Country defendingCountry);
}
