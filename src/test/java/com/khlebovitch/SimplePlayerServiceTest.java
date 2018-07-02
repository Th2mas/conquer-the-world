package com.khlebovitch;

import dto.Country;
import dto.Player;
import exceptions.CountryNotAvailableException;
import exceptions.NotImplementedException;
import javafx.geometry.Point2D;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.PlayerService;
import service.impl.SimplePlayerService;

import java.util.ArrayList;
import java.util.List;

public class SimplePlayerServiceTest {

    private PlayerService playerService;

    @Before
    public void setUp(){
        playerService = new SimplePlayerService();
    }

    @After
    public void tearDown(){

    }


    @Test
    public void addCountry_shouldAddCountry() throws CountryNotAvailableException {
        Player player = playerService.createPlayer();
        List<Country> countries = player.getCountries();

        playerService.addCountry(player, new Country("Bla", new ArrayList<>(), new Point2D(0,0)));

        Assert.assertEquals(countries.size()+1, player.getCountries().size());
    }

    @Test(expected = CountryNotAvailableException.class)
    public void addCountry_shouldNotAddEnemiesCountry() throws CountryNotAvailableException {
        Player player = playerService.createPlayer("Player", false);
        Player enemy = playerService.createPlayer("Enemy", false);
        Country c = new Country("Bla", new ArrayList<>(), new Point2D(0,0));

        playerService.addCountry(enemy, c);

        playerService.addCountry(player, c);
    }

    @Test
    public void setArmies_shouldSetCorrectNumberOfArmies(){
        Player player = playerService.createPlayer();
        Assert.assertEquals(player.getArmies(), 0);



        throw new NotImplementedException();
    }

    @Test
    public void placeArmies_shouldIncrementArmiesForCountry(){
        throw new NotImplementedException();
    }

    @Test(expected = CountryNotAvailableException.class)
    public void placeArmies_shouldNotIncrementeArmiesForCountry(){
        throw new NotImplementedException();
    }
}
