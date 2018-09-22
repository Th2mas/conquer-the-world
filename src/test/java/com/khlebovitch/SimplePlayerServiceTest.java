package com.khlebovitch;

import dto.Continent;
import dto.Country;
import dto.Player;
import exceptions.CountryNotAvailableException;
import exceptions.NotImplementedException;
import javafx.geometry.Point2D;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PlayerService;
import service.impl.SimplePlayerService;
import util.properties.PropertiesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The tester for the {@link SimplePlayerService}
 */
public class SimplePlayerServiceTest {

    /**
     * The {@link SimplePlayerServiceTest} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePlayerService.class);

    /**
     * The test {@link PlayerService}
     */
    private PlayerService playerService;

    /**
     * A player to be used (ai = false)
     */
    private Player player;

    /**
     * A second player to be used (ai = true)
     */
    private Player enemy;

    /**
     * The list of the {@link #player}'s countries
     */
    private List<Country> playerCountries;

    /**
     * The list of the {@link #enemy}'s countries
     */
    private List<Country> enemyCountries;

    /**
     * The list of countries, which are used for testing
     */
    private List<Country> countries;

    /**
     * The continent, containing all of {@link #countries} countries
     */
    private Continent continent;

    /**
     * The size of the custom lists in this test file
     */
    private int listSize = 10;

    @Before
    public void setUp(){
        LOGGER.info("Set up");

        PropertiesManager.initialize();

        playerService = new SimplePlayerService();
        player = playerService.createPlayer("Game.Player", false);
        enemy = playerService.createPlayer("Game.Enemy", true);

        playerCountries = player.getCountries();
        enemyCountries = enemy.getCountries();

        countries = new ArrayList<>();
        for(int i=0; i<listSize; i++) countries.add(new Country("Country"+i,new ArrayList<>(),new Point2D(0,0)));

        continent = new Continent(countries, 5, "Continent1");
    }

    @After
    public void tearDown(){
        LOGGER.info("Tear down");
    }


    @Test
    public void addCountry_shouldAddCountry() throws CountryNotAvailableException {
        LOGGER.info("Enter addCountry_shouldAddCountry");
        playerService.addCountry(player, new Country("Bla", new ArrayList<>(), new Point2D(0,0)));

        Assert.assertEquals(playerCountries.size()+1, player.getCountries().size());
        LOGGER.info("Finished addCountry_shouldAddCountry");
    }

    @Test(expected = CountryNotAvailableException.class)
    public void addCountry_shouldNotAddEnemiesCountry() throws CountryNotAvailableException {
        LOGGER.info("Enter addCountry_shouldAddCountry");

        playerService.addCountry(enemy, countries.get(0));

        playerService.addCountry(player, countries.get(0));
        LOGGER.info("Finished addCountry_shouldAddCountry");
    }

    @Test
    public void setArmies_shouldSetCorrectAmountOfArmies() throws CountryNotAvailableException {
        LOGGER.info("Enter setArmies_shouldSetCorrectAmountOfArmies");

        // Get the current amount of armies
        int prev = player.getArmies();

        // Add the countries to the player
        for(Country country : countries) playerService.addCountry(player, country);

        // Set the armies for the player (including the continent)
        playerService.setArmies(player, Collections.singletonList(continent));

        // Assert, that the current armies are the previous armies plus the ones, added with the countries
        Assert.assertEquals(prev+(listSize/3)+continent.getPoints(), player.getArmies());

        LOGGER.info("Finished setArmies_shouldSetCorrectAmountOfArmies");
    }

    @Test
    public void getAttackingArmies_shouldReturnZeroBecauseCountryHasNegativeArmies() {
        LOGGER.info("Enter getAttackingArmies_shouldReturnZeroBecauseCountryHasNegativeArmies");

        // Get a country, which will be used for attacking
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        int armies = -5;

        // Add the negative armies to the country
        player.addArmies(country, armies);

        // Assert, that the attacking armies equal 2
        Assert.assertEquals(0, playerService.getAttackingArmies(player, country));

        LOGGER.info("Finished getAttackingArmies_shouldReturnZeroBecauseCountryHasNegativeArmies");
    }

    @Test
    public void getAttackingArmies_shouldReturnZeroBecauseCountryHasOnlyOneArmy() {
        LOGGER.info("Enter getAttackingArmies_shouldReturnZeroBecauseCountryHasOnlyOneArmy");

        // Get a country, which will be used for attacking
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        // Get the current amount of attacking armies
        Assert.assertEquals(0, playerService.getAttackingArmies(player, country));

        LOGGER.info("Finished getAttackingArmies_shouldReturnZeroBecauseCountryHasOnlyOneArmy");
    }

    @Test
    public void getAttackingArmies_shouldReturnTwoBecauseCountryHasThreeArmies() {
        LOGGER.info("Enter getAttackingArmies_shouldReturnZeroBecauseCountryHasOnlyOneArmy");

        // Get a country, which will be used for attacking
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        int armies = 2;

        // Add the two armies to the country
        player.addArmies(country, armies);

        // Assert, that the attacking armies equal 2
        Assert.assertEquals(armies, playerService.getAttackingArmies(player, country));

        LOGGER.info("Finished getAttackingArmies_shouldReturnZeroBecauseCountryHasOnlyOneArmy");
    }

    @Test
    public void getAttackingArmies_shouldReturnMaxAttackingArmiesAsCountryHasMoreArmies() {
        LOGGER.info("Enter getAttackingArmies_shouldReturnMaxAttackingArmiesAsCountryHasMoreArmies");

        // Get a country, which will be used for attacking
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        // Add the armies to the country
        player.addArmies(country, listSize);

        // Assert, that the attacking armies equal max attacking armies
        Assert.assertEquals(
                PropertiesManager.getInt("Game.MaxAttackArmies", "settings"),
                playerService.getAttackingArmies(player, country)
        );

        LOGGER.info("Finished getAttackingArmies_shouldReturnMaxAttackingArmiesAsCountryHasMoreArmies");
    }

    @Test
    public void getDefendingArmies_shouldReturnOneBecauseCountryHasOnlyOneArmy() {
        LOGGER.info("Enter getDefendingArmies_shouldReturnOneBecauseCountryHasOnlyOneArmy");

        // Get a country, which will be used for defending
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        // Assert, that the defending armies equal 0
        Assert.assertEquals(1, playerService.getDefendingArmies(player, country));

        LOGGER.info("Finished getDefendingArmies_shouldReturnOneBecauseCountryHasOnlyOneArmy");
    }

    @Test
    public void getDefendingArmies_shouldReturnTwoAsCountryHasTwoArmies() {
        LOGGER.info("Enter getDefendingArmies_shouldReturnTwoAsCountryHasTwoArmies");

        // Get a country, which will be used for defending
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        int armies = 1;

        // Add one army to the country
        player.addArmies(country, armies);

        // Assert, that the defending armies equal 2
        Assert.assertEquals(armies+1, playerService.getDefendingArmies(player, country));

        LOGGER.info("Finished getDefendingArmies_shouldReturnTwoAsCountryHasTwoArmies");
    }

    @Test
    public void getDefendingArmies_shouldReturnMaxDefendingArmiesAsCountryHasMoreArmies() {
        LOGGER.info("Enter getDefendingArmies_shouldReturnMaxDefendingArmiesAsCountryHasMoreArmies");

        // Get a country, which will be used for defending
        Country country = countries.get(0);

        // Add the country to the player
        player.addCountry(country);

        int armies = 5;

        // Add two armies to the country
        player.addArmies(country, armies);

        // Assert, that the defending armies equal max defending armies
        Assert.assertEquals(
                PropertiesManager.getInt("Game.MaxDefendArmies", "settings"),
                playerService.getDefendingArmies(player, country)
        );

        LOGGER.info("Finished getDefendingArmies_shouldReturnMaxDefendingArmiesAsCountryHasMoreArmies");
    }

    @Test
    public void moveArmies_shouldMoveAllArmies() {
        LOGGER.info("Enter moveArmies_shouldMoveAllArmies");

        // Add two countries to the player
        player.addCountry(countries.get(0));
        player.addCountry(countries.get(1));

        // Give the first country 'listSize' armies
        player.addArmies(countries.get(0), listSize);

        // Move the armies to the second country
        playerService.moveArmies(player, countries.get(0), countries.get(1));

        // Assert that the first country has only one country
        Assert.assertEquals(1, player.getArmies(countries.get(0)));
        // Assert that the second country has all other armies
        Assert.assertEquals(listSize+1, player.getArmies(countries.get(1)));

        LOGGER.info("Finished moveArmies_shouldMoveAllArmies");
    }

    @Test
    public void moveArmies_shouldMoveNoArmiesAsThereAreNotEnoughArmies() {
        LOGGER.info("Enter moveArmies_shouldMoveNoArmiesAsThereAreNotEnoughArmies");

        // Add two countries to the player
        player.addCountry(countries.get(0));
        player.addCountry(countries.get(1));

        // Try moving any armies to the second country
        playerService.moveArmies(player, countries.get(0), countries.get(1));

        // Assert, that no armies were moved
        Assert.assertEquals(1, player.getArmies(countries.get(0)));
        Assert.assertEquals(1, player.getArmies(countries.get(1)));

        LOGGER.info("Finished moveArmies_shouldMoveNoArmiesAsThereAreNotEnoughArmies");
    }

    @Test
    public void moveArmies_shouldMoveNoArmiesAsThePlayerDoesNotHaveTheSecondCountry() {
        LOGGER.info("Enter moveArmies_shouldMoveNoArmiesAsThePlayerDoesNotHaveTheSecondCountry");

        // Add only the first country to the player
        player.addCountry(countries.get(0));

        // Try moving any armies to the second country
        playerService.moveArmies(player, countries.get(0), countries.get(1));

        // Assert, that no armies were moved
        Assert.assertEquals(1, player.getArmies(countries.get(0)));
        Assert.assertEquals(0, player.getArmies(countries.get(1)));

        LOGGER.info("Finished moveArmies_shouldMoveNoArmiesAsThePlayerDoesNotHaveTheSecondCountry");
    }

    @Test
    public void conquerCountry_shouldMoveAllRemainingArmiesToDefendingCountryAndAddItToThePlayer() {
        LOGGER.info("Enter conquerCountry_shouldMoveAllRemainingArmiesToDefendingCountryAndAddItToThePlayer");

        Country attackingCountry = countries.get(0);
        Country defendingCountry = countries.get(1);

        // Give the player and the enemy a country
        player.addCountry(attackingCountry);
        enemy.addCountry(defendingCountry);

        // Give the player 'listSize' armies
        player.addArmies(attackingCountry, listSize);

        // Remove the enemy's armies
        enemy.removeArmies(defendingCountry, 1);

        // Let player conquer the defending country
        playerService.conquerCountry(player, enemy, attackingCountry, defendingCountry);

        // Assert, that the enemy has no longer the defending country
        Assert.assertFalse(enemy.hasCountry(defendingCountry));

        // Assert, that the player has the country
        Assert.assertTrue(player.hasCountry(defendingCountry));

        // Assert, that the attacking country has only one army left
        Assert.assertEquals(1,player.getArmies(attackingCountry));

        // Assert, that the defending country has now all of attacking country's armies (-1, as the attacking country still needs one army)
        Assert.assertEquals(listSize, player.getArmies(defendingCountry));

        LOGGER.info("Finished conquerCountry_shouldMoveAllRemainingArmiesToDefendingCountryAndAddItToThePlayer");
    }

    @Test
    public void conquerCountry_shouldNotConquerCountryAsThereAreArmiesLeft() {
        LOGGER.info("Enter conquerCountry_shouldNotConquerCountryAsThereAreArmiesLeft");

        Country attackingCountry = countries.get(0);
        Country defendingCountry = countries.get(1);

        // Give the player and the enemy a country
        player.addCountry(attackingCountry);
        enemy.addCountry(defendingCountry);

        // Give the player 'listSize' armies
        player.addArmies(attackingCountry, listSize);

        // Let player conquer the defending country
        playerService.conquerCountry(player, enemy, attackingCountry, defendingCountry);

        // Assert, that the enemy still has the defending country
        Assert.assertTrue(enemy.hasCountry(defendingCountry));

        // Assert, that the player has the country
        Assert.assertFalse(player.hasCountry(defendingCountry));

        // Assert, that the attacking country still all its armies
        Assert.assertEquals(listSize+1,player.getArmies(attackingCountry));

        // Assert, that the defending country still has all its armies
        Assert.assertEquals(1, enemy.getArmies(defendingCountry));

        LOGGER.info("Finished conquerCountry_shouldNotConquerCountryAsThereAreArmiesLeft");
    }

    @Test
    public void conquerCountry_shouldConquerCountryIfPlayerHasTwoArmies() {
        LOGGER.info("Enter conquerCountry_shouldConquerCountryIfPlayerHasTwoArmies");

        Country attackingCountry = countries.get(0);
        Country defendingCountry = countries.get(1);

        // Give the player and the enemy a country
        player.addCountry(attackingCountry);
        enemy.addCountry(defendingCountry);

        // Give the attacking country one army, so that we have two
        player.addArmies(attackingCountry, 1);

        // Remove the defending country, so that it has zero armies
        enemy.removeArmies(defendingCountry,1);

        // Try to conquer the defending country
        playerService.conquerCountry(player, enemy, attackingCountry, defendingCountry);

        // Assert, that the enemy does not have the defending country anymore
        Assert.assertFalse(enemy.hasCountry(defendingCountry));

        // Assert, that the player does have the defending country
        Assert.assertTrue(player.hasCountry(defendingCountry));

        // Assert, that the attacking country has only one army left
        Assert.assertEquals(1, player.getArmies(attackingCountry));

        // Assert, that the defending country has also only one army
        Assert.assertEquals(1,player.getArmies(defendingCountry));

        LOGGER.info("Finished conquerCountry_shouldConquerCountryIfPlayerHasTwoArmies");
    }
}
