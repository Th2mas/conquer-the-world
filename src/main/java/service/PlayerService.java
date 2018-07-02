package service;

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
     * Moves the armies to a country, which belongs the player or does attack the
     * @param player
     * @param from
     * @param to
     */
    void moveArmies(Player player, Country from, Country to);

    /**
     * Player1 attacks the defending country
     * @param p1 the attacking player
     * @return true, if the defending country was conquered by player1; false otherwise
     */
    void attack(Player p1, Country attackCountry, Country defendingCountry) throws NotEnoughArmiesException, AttackOwnCountryException;

    /**
     * Indicates the end of one players turn and lets the next player play
     */
    void move();

    /**
     * Checks, if the given country belongs to a player
     * @param country the country to be checked
     * @return true, if country does not belong to anybody; false otherwise
     */
    boolean isCountryFree(Country country);

    /**
     * Returns the current player
     * @return the current player
     */
    Player getCurrentPlayer();
}
