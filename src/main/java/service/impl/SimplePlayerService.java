package service.impl;

import dto.Country;
import dto.Player;
import exceptions.AttackOwnCountryException;
import exceptions.CountryNotAvailableException;
import exceptions.NotEnoughArmiesException;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PlayerService;
import util.properties.PropertiesManager;

import java.util.*;
import java.util.stream.Collectors;

public class SimplePlayerService implements PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePlayerService.class);

    private List<Player> players;

    private PropertiesManager settingsManager;

    public SimplePlayerService(PropertiesManager settingsManager){
        players = new ArrayList<>();
        this.settingsManager = settingsManager;
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
                .color(Color.RED)
                .armies(0)
                .countryMap(new HashMap<>())
                .move(false)
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
    public void moveArmies(Player player, Country from, Country to) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        if(player.hasCountry(from) && player.hasCountry(to)){
            int armies = player.getArmies(from);
            if(armies > 1){
                player.setArmies(to, player.getArmies(to)+(armies-1));
                player.setArmies(from, 1);
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

        // If the attacking country has n armies, you can attack only attack with at most n-1 armies
        int attackingArmies;
        int defendArmies;

        // Decide how many armies can attack the opponent
        attackingArmies = (p1.getArmies(attackCountry) <= settingsManager.getInt("Game.MaxAttackArmies") && p1.getArmies(attackCountry) > 1)
                ? p1.getArmies(attackCountry)-1
                : settingsManager.getInt("Game.MaxAttackArmies");
        p1.setArmies(attackCountry, p1.getArmies(attackCountry)-attackingArmies);

        // Get the player, who owns the country
        Optional<Player> op = Optional.empty();
        for(Player player : players) if(player.hasCountry(defendCountry)) op = Optional.of(player);

        // If there is no player who owns the country -> illegal state
        if(!op.isPresent()) {
            // Reset the previous made changes
            p1.setArmies(attackCountry, p1.getArmies(attackCountry)+attackingArmies);

            // throw exception
            throw new IllegalStateException("No player owns country " + defendCountry.getName());
        }
        Player p2 = op.get();

        // Decide how many armies can defend the attacked country
        defendArmies = (p2.getArmies(defendCountry) <= settingsManager.getInt("Game.MaxDefendArmies")) ? p2.getArmies(defendCountry) : settingsManager.getInt("Game.MaxDefendArmies");
        p2.setArmies(defendCountry, p2.getArmies(defendCountry)-defendArmies);

        // To save the thrown dices, we need to use an array
        Integer[] attackDices = new Integer[attackingArmies];
        Integer[] defendDices = new Integer[defendArmies];

        // Calculate the thrown dices
        for(int i=0; i<attackDices.length; i++) attackDices[i] = (int) (Math.random()*6)+1;
        for(int i=0; i<defendDices.length; i++) defendDices[i] = (int) (Math.random()*6)+1;

        // Sort the dice arrays in decscending order to compare the first elements
        Arrays.sort(attackDices, Collections.reverseOrder());
        Arrays.sort(defendDices, Collections.reverseOrder());

        // Check which army has the highest number per dice
        for(int i=0; i<defendDices.length; i++){
            LOGGER.info("Attack=[" + attackCountry.getName() + "," + attackDices[i] + "], Defend=[" + defendCountry.getName() + "," + defendDices[i] + "]");
            // If the attacking player has a larger number, the number of defending armies should be decremented
            if(attackDices[i] > defendDices[i]) defendArmies--;
            else attackingArmies--;

            if(defendArmies == 0 || attackingArmies == 0) break;
        }

        // If the country was conquered, remove it from the defending players list and add it to the attacking players list
        if(defendArmies == 0){
            p2.removeCountry(defendCountry);
            p1.addCountry(defendCountry);
            p1.setArmies(defendCountry, attackingArmies);

            // Change the defendCountry's color
            defendCountry.getPatches().forEach(polygon -> polygon.setFill(p1.getColor()));
            return;
        }

        // If there are still some armies left, you have to return them to their respective countries
        p1.setArmies(attackCountry, p1.getArmies(attackCountry)+attackingArmies);
        p2.setArmies(defendCountry, p2.getArmies(defendCountry)+defendArmies);
    }

    @Override
    public void nextTurn() {
        int index = -1;
        Player currentPlayer = getCurrentPlayer();
        for(int i=0; i<players.size(); i++) if(players.get(i).equals(currentPlayer)) index = i;
        if(index >= 0){
            index = ((index+1)%players.size());
            currentPlayer.setMove(false);
            currentPlayer = players.get(index);
            currentPlayer.setMove(true);
        }
    }

    /**
     * Checks if the given country is not conquered
     * @param country the country to be checked
     * @return true, if free; otherwise false
     */
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
