package com.khlebovitch;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.geometry.Point2D;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The tester for the {@link Player}
 */
public class PlayerTest {

    /**
     * The {@link PlayerTest} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerTest.class);

    /**
     * The test {@link Player}
     */
    private Player player;

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
        player = new Player.PlayerBuilder()
                .name("Schokobär")
                .build()
        ;

        countries = new ArrayList<>();
        for(int i=0; i<listSize; i++) countries.add(new Country("Country"+i,new ArrayList<>(),new Point2D(0,0)));

        continent = new Continent(countries, 5, "Continent1");
    }

    @After
    public void tearDown(){
        LOGGER.info("Tear down");
    }

    @Test
    public void addCountry_shouldAddCountryCorrectly() {
        LOGGER.info("Enter addCountry_shouldAddCountryCorrectly");

        for(Country country : countries) {
            Assert.assertFalse(player.getCountries().contains(country));
            player.addCountry(country);
            Assert.assertTrue(player.getCountries().contains(country));
        }
        LOGGER.info("Finished addCountry_shouldAddCountryCorrectly");
    }

    @Test
    public void removeCountry_shouldRemoveCountry() {
        LOGGER.info("Enter removeCountry_shouldRemoveCountry");

        Assert.assertFalse(player.hasCountry(countries.get(0)));
        player.addCountry(countries.get(0));
        Assert.assertTrue(player.hasCountry(countries.get(0)));

        player.removeCountry(countries.get(0));
        Assert.assertFalse(player.hasCountry(countries.get(0)));

        LOGGER.info("Finished removeCountry_shouldRemoveCountry");
    }

    @Test
    public void removeCountry_shouldDoNothing() {
        LOGGER.info("Enter removeCountry_shouldDoNothing");

        Assert.assertFalse(player.hasCountry(countries.get(1)));

        player.removeCountry(countries.get(0));

        Assert.assertFalse(player.hasCountry(countries.get(1)));

        LOGGER.info("Finished removeCountry_shouldDoNothing");
    }

    @Test
    public void hasCountry_shouldReturnTrue() {
        LOGGER.info("Enter hasCountry_shouldReturnTrue");

        Assert.assertFalse(player.hasCountry(countries.get(0)));
        player.addCountry(countries.get(0));
        Assert.assertTrue(player.hasCountry(countries.get(0)));
        LOGGER.info("Finished hasCountry_shouldReturnTrue");
    }

    @Test
    public void hasCountry_shouldReturnFalse() {
        LOGGER.info("Enter hasCountry_shouldReturnFalse");

        Assert.assertFalse(player.hasCountry(countries.get(0)));
        LOGGER.info("Finished hasCountry_shouldReturnFalse");
    }

    @Test
    public void hasContinent_shouldReturnTrue() {
        LOGGER.info("Enter hasContinent_shouldReturnTrue");

        for(Country country : countries) {
            Assert.assertFalse(player.hasCountry(country));
            player.addCountry(country);
            Assert.assertTrue(player.hasCountry(country));
        }

        Assert.assertTrue(player.hasContinent(continent));

        LOGGER.info("Finished hasContinent_shouldReturnTrue");
    }

    @Test
    public void hasContinent_shouldReturnFalse() {
        LOGGER.info("Enter hasContinent_shouldReturnFalse");

        for(Country country : countries) {
            Assert.assertFalse(player.hasCountry(country));
            player.addCountry(country);
            Assert.assertTrue(player.hasCountry(country));
        }

        player.removeCountry(player.getCountries().get(0));

        Assert.assertFalse(player.hasContinent(continent));

        LOGGER.info("Finished hasContinent_shouldReturnFalse");
    }

    @Test
    public void setArmies_shouldSetArmiesCorrectly() {
        LOGGER.info("Enter hasContinent_shouldReturnFalse");

        // Add a country, which can be worked on
        player.addCountry(countries.get(0));

        int armiesForCountry = player.getArmies(countries.get(0));

        Assert.assertEquals(1, armiesForCountry);

        // Set the armies for the country
        player.addArmies(countries.get(0), listSize);

        // Now the player should have no armies left and the country should have listSize+1 countries
        Assert.assertEquals(listSize+1, player.getArmies(countries.get(0)));

        LOGGER.info("Finished hasContinent_shouldReturnFalse");
    }

    @Test
    public void addArmies_shouldAddArmiesToCountryAndRemoveCorrectAmount() {
        LOGGER.info("Enter addArmies_shouldAddArmiesToCountryAndRemoveCorrectAmount");

        // Add the country to the player
        player.addCountry(countries.get(0));

        // Assure, that the player has no armies to be placed
        Assert.assertEquals(0, player.getArmies());

        // Assign those armies to a country
        player.addArmies(countries.get(0), listSize);

        // Assure that the player has no armies left and the country has 'listSize'+1 armies
        Assert.assertEquals(listSize+1, player.getArmies(countries.get(0)));

        LOGGER.info("Finished addArmies_shouldAddArmiesToCountryAndRemoveCorrectAmount");
    }

    @Test
    public void addArmies_shouldAddNoArmiesAsPlayerHasNotEnoughArmies() {
        LOGGER.info("Enter addArmies_shouldAddNoArmiesAsPlayerHasNotEnoughArmies");

        // Add the country to the player
        player.addCountry(countries.get(0));

        // Assure, that the player has no armies to be placed
        Assert.assertEquals(0, player.getArmies());

        // Assure that the player has still no armies and the country has still the same amount of armies
        Assert.assertEquals(1, player.getArmies(countries.get(0)));

        LOGGER.info("Finished addArmies_shouldAddNoArmiesAsPlayerHasNotEnoughArmies");
    }
}
