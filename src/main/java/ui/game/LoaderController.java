package ui.game;

import dto.Continent;
import exceptions.IllegalCommandException;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import util.error.ErrorDialog;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The controller class for loading the continents
 */
public class LoaderController {

    /**
     * The reader, which will read the .map file and return the continents
     */
    private Optional<List<Continent>> continentList;


    /**
     * Creates the game controller with the default map
     */
    public LoaderController(Group root){

        // Create an optional continent list -> it could be empty, if an exception occured during loading
        continentList = Optional.empty();

        // Try to get the default map
        try { continentList = Optional.of(new SimpleMapReader().readFile(LoaderController.class.getResource("/map/world.map").getPath())); }
        catch (IOException | IllegalCommandException e) { ErrorDialog.showErrorDialog(e.getMessage()); }

        // Add all patches to the root group
        continentList.ifPresent(continents -> continents.forEach(continent -> continent.getCountries().forEach(country -> country.getPatches().forEach(patch -> root.getChildren().add(patch)))));
    }

    /**
     * This method sets different colors for the continents
     */
    public void setColors(){

        // If the continentlist is present, set the default colors to the continents
        continentList.ifPresent(continents -> continents.forEach(continent -> {
            Color color = Color.WHITE;
            switch (continent.getName()){
                case "North America":
                    color = new Color(1,0.9,0,1);
                    break;
                case "South America":
                    color = Color.RED;
                    break;
                case "Europe":
                    color = Color.BLUE;
                    break;
                case "Africa":
                    color = Color.BROWN;
                    break;
                case "Asia":
                    color = Color.FORESTGREEN;
                    break;
                case "Australia":
                    color = Color.VIOLET;
                    break;
            }
            continent.setColor(color);
            continent.getCountries().forEach(country -> country.getPatches().forEach(polygon -> { polygon.fillProperty().set(continent.getColor()); }));
        }));
    }

    /**
     * This method darkens the color of the country on mouse over
     */
    public void darkenPatchesOnMouseOver(){
        // TODO: Implement me!
    }

    /**
     * Returns all continents
     * @return continents
     */
    public Optional<List<Continent>> getContinentList(){
        return continentList;
    }

}