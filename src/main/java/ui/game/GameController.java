package ui.game;

import dto.Continent;
import dto.Country;
import dto.Player;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The controller, which handles the actual game logic
 */
public class GameController {

    private final Optional<List<Continent>> continentList;
    private Phase currentPhase;
    private final ExecutorService executorService;
    private final Player player;
    private final Player ai;

    public GameController(Optional<List<Continent>> continentList){
        this.continentList = continentList;
        this.currentPhase = Phase.ACQUISITION;
        executorService = Executors.newSingleThreadExecutor();

        // TODO Later: Request the player's name and color
        player = new Player();

        ai = new Player();
        ai.setColor(Color.GREY);
    }

    /**
     * Starts the phases
     */
    public void start(){

        // Start the game
        continentList.ifPresent(continents -> continents.forEach(continent -> continent.getCountries().forEach(country -> {
            // Determine what will happen, if a country is clicked
            country.getPatches().forEach(patch -> patch.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                // Click during acquisition phase
                if(currentPhase == Phase.ACQUISITION){
                    // Check if the country is not assigned
                    if(!ai.hasCountry(country) && !player.hasCountry(country)) {
                        // Add the country to the player and change its color
                        player.addCountry(country);
                        country.getPatches().forEach(polygon -> polygon.setFill(player.getColor()));

                        // Add a random country of a random continent to the ai
                        Country randomCountry = null;
                        while(randomCountry == null){
                            Continent randomContinent = continents.get((int)(Math.random()*continents.size()));
                            Country test = randomContinent.getCountries().get((int)(Math.random()*randomContinent.getCountries().size()));
                            if(!ai.hasCountry(test) && !player.hasCountry(test)) randomCountry = test;
                        }

                        ai.addCountry(randomCountry);
                        randomCountry.getPatches().forEach(polygon -> polygon.setFill(ai.getColor()));
                    }

                    // Check if all countries are assigned to a player
                    int sumCountries = 0;
                    for(Continent cont : continents) sumCountries += cont.getCountries().size();
                    if(sumCountries == (ai.sizeCountries()+player.sizeCountries())) currentPhase = Phase.CONQUERING_ARMY_PLACEMENT;
                }
            }));
        })));

        // TODO: Implement conquering phase!
    }

    /**
     * Shuts the game down
     */
    public void shutdown(){
        try {
            System.out.println("attempt to shutdown executor service");

            // The executor shuts down softly by waiting a certain amount of time for termination of currently running tasks
            executorService.shutdown();

            // After a maximum of five seconds the executor finally shuts down by interrupting all running tasks
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executorService.isTerminated()) System.err.println("cancel non-finished tasks");
            executorService.shutdownNow();
            System.out.println("shutdown finished");
        }
    }
}

enum Phase {
    ACQUISITION,
    CONQUERING_ARMY_PLACEMENT,
    CONQUERING_MOVE_AND_ATTACK,
    CONQUERING_END_ROUND
}
