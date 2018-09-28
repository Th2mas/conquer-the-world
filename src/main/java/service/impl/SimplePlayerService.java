package service.impl;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.AttackOwnCountryException;
import exceptions.CountryNotAvailableException;
import exceptions.NotEnoughArmiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PlayerService;
import util.properties.PropertiesManager;

import java.util.*;
import java.util.stream.Collectors;

public class SimplePlayerService implements PlayerService {

    /**
     * The {@link SimplePlayerService} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePlayerService.class);

    /**
     * The list of current players
     */
    private final List<Player> players;

    /**
     * The SimplePlayerInstance, which will be used throughout the game
     */
    private static SimplePlayerService simplePlayerService;

    /**
     * Creates a new SimplePlayerService
     */
    private SimplePlayerService(){
        players = new ArrayList<>();
    }

    /**
     * Returns the one and only SimplePlayerService in the game
     * @return the simple player service
     */
    public static SimplePlayerService getSimplePlayerService() {
        if(simplePlayerService == null) simplePlayerService = new SimplePlayerService();
        return simplePlayerService;
    }

    @Override
    public Player createPlayer() {
        return createPlayer("",true);
    }

    @Override
    public Player createPlayer(String name, boolean ai) {
        Player p =  new Player.PlayerBuilder()
                .name(name)
                .ai(ai)
                .build();
        players.add(p);
        return p;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void addCountry(Player player, Country country) throws CountryNotAvailableException {
        for(Player p : players) if(p.hasCountry(country)) throw new CountryNotAvailableException(p, country);
        player.addCountry(country);
    }

    @Override
    public void setArmies(Player player, List<Continent> continents) {
        // Get the current amount of armies
        int armies = player.getArmies();

        // Calculate the number of armies the player gets for his countries
        armies += (player.getCountries().size() / 3);

        // Calculate the additional points, if the player has conquered a continent
        for(Continent continent : continents) {
            if(player.hasContinent(continent)) armies += continent.getPoints();
        }

        // Set the new amount
        player.setArmies(armies);
    }

    @Override
    public void moveArmies(Player player, Country from, Country to) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        // Only move armies, if player has both armies
        if(player.hasCountry(from) && player.hasCountry(to)){

            // Get the armies to be moved
            int armies = player.getArmies(from)-1;

            // Only move, if there are any armies to be moved
            if(armies > 0){
                player.addArmies(to, armies);
                player.removeArmies(from, armies);
            }
        }
    }

    @Override
    public void attack(Player p1, Country attackCountry, Country defendCountry) throws NotEnoughArmiesException, AttackOwnCountryException {

        Objects.requireNonNull(p1);
        Objects.requireNonNull(attackCountry);
        Objects.requireNonNull(defendCountry);

        // Assure, that the defendingCountry does not belong to player1
        if(p1.hasCountry(defendCountry)) throw new AttackOwnCountryException(p1);

        // If the player has not enough armies in the attacking country, then nothing should happen
        if(p1.getArmies(attackCountry) == 1) throw new NotEnoughArmiesException(p1);

        // Get the player, who owns the country
        Optional<Player> op = Optional.empty();
        for(Player player : players) if(player.hasCountry(defendCountry)) op = Optional.of(player);

        // If there is no player who owns the country -> illegal state
        if(!op.isPresent()) throw new IllegalStateException("No player owns country " + defendCountry.getName());

        // Get the player the country belongs to
        Player p2 = op.get();

        // If the attacking country has n armies, you can attack only attack with at most n-1 armies
        int attackingArmies = getAttackingArmies(p1, attackCountry);

        // Decide how many armies can defend the attacked country
        int defendingArmies = getDefendingArmies(p2, defendCountry);

        // Remove the attacking and defending armies from the respective country, so that you can work with the values
        p1.removeArmies(attackCountry, attackingArmies);
        p2.removeArmies(defendCountry, defendingArmies);

        // To save the thrown dices, we need to use an array
        Integer[] attackDices = new Integer[attackingArmies];
        Integer[] defendDices = new Integer[defendingArmies];

        // Calculate the thrown dices
        for(int i=0; i<attackDices.length; i++) attackDices[i] = (int) (Math.random()*6)+1;
        for(int i=0; i<defendDices.length; i++) defendDices[i] = (int) (Math.random()*6)+1;

        // Sort the dice arrays in descending order to compare the first elements
        Arrays.sort(attackDices, Collections.reverseOrder());
        Arrays.sort(defendDices, Collections.reverseOrder());

        // Decide how many armies can be used for checking the dices
        int maxArmies = (attackingArmies > defendingArmies) ? defendingArmies : attackingArmies;

        // Check which army has the highest number per dice
        for(int i=0; i<maxArmies; i++){

            int attackingDice = attackDices[i];
            int defendingDice = defendDices[i];

            LOGGER.info(
                    "Attack=[" + attackCountry.getName() + "," + attackingDice + "], " +
                    "Defend=[" + defendCountry.getName() + "," + defendingDice + "]"
            );

            // If the attacking player has a larger number, the number of defending armies should be decremented
            if(attackingDice < defendingDice) attackingArmies--;
            else defendingArmies--;

            if(defendingArmies == 0 || attackingArmies == 0) break;
        }

        // If the country was conquered, remove it from the defending players list and add it to the attacking players list
        if(defendingArmies == 0){
            // Add the remaining attacking armies back to the attacking country
            p1.addArmies(attackCountry, attackingArmies);

            conquerCountry(p1, p2, attackCountry, defendCountry);
            return;
        }

        // Finally, if the country was not conquered, you have to return the armies to the respective country
        p1.addArmies(attackCountry, attackingArmies);
        p2.addArmies(defendCountry, defendingArmies);

    }

    @Override
    public void conquerCountry(Player p1, Player p2, Country attackCountry, Country defendingCountry) {
        // TODO: Make exceptions, if any of the conditions is false
        // It is only possible to conquer a country, if the defending player has no armies left in the country
        // And the attacking country has at least two armies left
        if(p2.getArmies(defendingCountry) == 0 && p1.getArmies(attackCountry) >= 2) {

            // Remove the country from the player, who has lost it
            p2.removeCountry(defendingCountry);

            // Add the country to the player, who has conquered it
            p1.addCountry(defendingCountry);

            // Remove the army from the defending country, as it will be added by 'moveArmies'
            p1.removeArmies(defendingCountry, 1);

            // Move all countries from the attacking to the defending country
            moveArmies(p1, attackCountry, defendingCountry);

            // Change the defending country's color
            defendingCountry.getPatches().forEach(polygon -> polygon.setFill(p1.getColor()));
        }
    }

    @Override
    public void nextTurn() {
        int index = -1;
        Player currentPlayer = getCurrentPlayer();
        int playersSize = players.size();
        for(int i=0; i<playersSize; i++) if(players.get(i).equals(currentPlayer)) index = i;
        if(index >= 0){
            index = ((index+1) % players.size());
            currentPlayer.setMove(false);
            currentPlayer = players.get(index);
            currentPlayer.setMove(true);
        }
    }

    @Override
    public int getAttackingArmies(Player player, Country attackingCountry) {
        LOGGER.info("Enter");

        // Get the max attacking / defending property
        int maxAttackingArmies = PropertiesManager.getInt("Game.MaxAttackArmies", "settings");

        // Get all armies of the attacking country
        int allCountryArmies = player.getArmies(attackingCountry);

        // Decide how many armies can attack the opponent
        // Default: 0, as there could be not enough armies in the country (e.g. the attacking country has only one army)
        int attackingArmies = 0;

        if(allCountryArmies > 1)
            attackingArmies = (allCountryArmies > maxAttackingArmies) ? maxAttackingArmies : allCountryArmies - 1;

        return attackingArmies;
    }

    @Override
    public int getDefendingArmies(Player player, Country defendingCountry) {
        LOGGER.info("Enter");

        // Get the max attacking / defending property
        int maxDefendingArmies = PropertiesManager.getInt("Game.MaxDefendArmies", "settings");

        // Get all armies of the defending country
        int allCountryArmies = player.getArmies(defendingCountry);

        // Decide how many armies can defend the country
        // Default: 1, as the defending country has at least one army, which must be used for defending
        int defendingArmies = 1;
        if(allCountryArmies > 1)
            defendingArmies = (allCountryArmies >= maxDefendingArmies) ? maxDefendingArmies : allCountryArmies;

        return defendingArmies;
    }


    @Override
    public boolean isCountryFree(Country country) {
        boolean free = true;
        for(Player player : players) if(player.hasCountry(country)) free = false;
        return free;
    }

    @Override
    public Player getCurrentPlayer() {
        // Get the current player
        List<Player> currentPlayers = players.stream()
                .filter(Player::canBeMoved)
                .collect(Collectors.toList());

        // Assure, that there is exactly one current player
        if(currentPlayers.size() != 1) throw new IllegalStateException("There is none | more than one active players");
        return currentPlayers.get(0);
    }
}
