package ui.game;

import dto.Continent;
import dto.Country;
import exceptions.IllegalCommandException;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import util.error.ErrorDialog;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The controller class for loading the continents
 */
public class LoaderController {

    /**
     * The reader, which will read the .map file and return the continents
     * TODO: Let the constructor throw an exception, if something failed -> omit the Optional!
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

        // Add lines, which connect the countries with their neighbors
        continentList.ifPresent(continents -> continents.forEach(continent -> continent.getCountries().forEach(country -> {
            List<Country> neighbors = new ArrayList<>();

            // Get the country's neighbors
            country.getNeighbors().forEach(neighbor -> continents.forEach(continent1 -> continent1.getCountries().forEach(country1 -> {
                if(country1.getName().equalsIgnoreCase(neighbor)) neighbors.add(country1);
            })));

            // Draw the line between the country and the country's neighbors
            neighbors.forEach(neighbor -> root.getChildren().add(new Line(country.getCapital().getX(), country.getCapital().getY(), neighbor.getCapital().getX(), neighbor.getCapital().getY())));
        })));

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
                    color = new Color(1,0.5,0,1);
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
        continentList.ifPresent(continents -> continents.forEach(continent -> {
            continent.getCountries().forEach(country -> {
                // Get the current active patch, thus the current country
                country.getPatches().forEach(patch -> {
                    patch.setOnMouseEntered(e -> country.getPatches().forEach(p -> p.setFill(((Color)p.getFill()).darker())));
                    patch.setOnMouseExited(e -> country.getPatches().forEach(p -> p.setFill(((Color)p.getFill()).brighter())));
                });
            });
        }));
    }

    /**
     * Scales all patches with the given factor
     * @param factor scale factor
     */
    public void scalePatches(double factor){
        continentList.ifPresent(continents -> continents.forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(polygon -> {
                polygon.setScaleX(factor);
                polygon.setScaleY(factor);
            });
            country.setCapital(new Point2D(country.getCapital().getX()*factor, country.getCapital().getY()*factor));
        })));
    }

    /**
     * Returns all continents
     * @return continents
     */
    public Optional<List<Continent>> getContinentList(){
        return continentList;
    }

}