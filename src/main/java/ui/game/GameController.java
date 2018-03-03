package ui.game;

import dto.Continent;
import dto.Player;

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
    }

    /**
     * Starts the phases
     */
    public void start(){

        // Start the game
        executorService.execute(() -> {
            // Acquisition phase
            /*
            while(currentPhase == Phase.ACQUISITION){

            }
            */
        });
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
