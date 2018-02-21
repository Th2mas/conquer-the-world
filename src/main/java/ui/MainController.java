package ui;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * The main controller for the application
 */
public class MainController {

    /**
     * The optional primary stage for closing the stage on close request
     */
    private Optional<Stage> primaryStage;

    /**
     * Creates a new main controller
     */
    public MainController(){
        //mapReader = new SimpleMapReader();
        primaryStage = Optional.empty();
    }

    @FXML
    public void initialize(){
        /*
        // TODO: Move me to a continent controller!
        // Read the map file
        // If anything goes wrong: display an error message
        List<Continent> continentList;
        try { continentList = mapReader.readFile(MainController.class.getResource("/map/world.map").getPath()); }
        catch (Exception e){
            ErrorDialog.showErrorDialog(e.getLocalizedMessage());
            primaryStage.ifPresent(Stage::close);
            return;
        }
        // Now we have all continents
        */
    }

    /**
     * Sets the primary stage
     * @param primaryStage primary stage
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = Optional.of(primaryStage);
    }
}
