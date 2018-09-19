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
        Country c = new Country("Bla", new ArrayList<>(), new Point2D(0,0));

        playerService.addCountry(enemy, c);

        playerService.addCountry(player, c);
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
}
