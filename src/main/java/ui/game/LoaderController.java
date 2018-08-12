package ui.game;

import dto.Continent;
import exceptions.IllegalCommandException;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import util.error.ErrorDialog;
import util.properties.PropertiesManager;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.List;

/**
 * The controller class for loading the continents
 */
public class LoaderController {

    /**
     * The reader, which will read the .map file and return the continents
     */
    private List<Continent> continentList;

    /**
     * Creates the game controller with the default map
     */
    public LoaderController(Group root){

        // Try to get the default map
        try { continentList = new SimpleMapReader().readFile(LoaderController.class.getResource("/map/world.map").getPath()); }
        catch (IOException | IllegalCommandException e) {
            ErrorDialog.showErrorDialog(e.getMessage());
            // Exit the program
            // TODO: Maybe end the program otherwise
            throw new RuntimeException(e.getMessage());
        }

        // Get the window's properties file
        PropertiesManager windowManager = new PropertiesManager("properties/window");

        int width = windowManager.getInt("window.size.x");

        // Add lines, which connect the countries with their neighbors
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            // Draw the line between the country and the country's neighbors
            country.getNeighbors().forEach(neighbor -> {
                Line line;
                // Check if the line length would be greater than the half of the window width
                // If this is the case, then just draw the line to 0 or window width
                if(Math.abs((int)(country.getCapital().getX()-neighbor.getCapital().getX())) > width/2){
                    // Check if the start position is on the left or right half of the screen and set the correct end x position
                    if((int)country.getCapital().getX() < width/2)
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), 0, neighbor.getCapital().getY());
                    else
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), width, neighbor.getCapital().getY());
                }
                else
                    line = new Line(country.getCapital().getX(), country.getCapital().getY(), neighbor.getCapital().getX(), neighbor.getCapital().getY());

                root.getChildren().add(line);
            });
        }));

        // Add all patches to the root group
        continentList.forEach(continent -> continent.getCountries().forEach(country -> country.getPatches().forEach(patch -> root.getChildren().add(patch))));
    }

    /**
     * This method sets different colors for the continents
     */
    public void setColors(){

        // If the continentlist is present, set the default colors to the continents
        continentList.forEach(continent -> {
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
        });
    }

    /**
     * This method darkens the color of the country on mouse over
     */
    public void darkenPatchesOnMouseOver(){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            // Get the current active patch, thus the current country
            country.getPatches().forEach(patch -> {
                patch.setOnMouseEntered(e -> country.getPatches().forEach(p -> p.setFill(((Color)p.getFill()).darker())));
                patch.setOnMouseExited(e -> country.getPatches().forEach(p -> p.setFill(((Color)p.getFill()).brighter())));
            });
        }));
    }

    /**
     * Scales all patches with the given factor
     * TODO: Doesn't scale correctly
     * @param factor scale factor
     */
    public void scalePatches(double factor){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(polygon -> {
                polygon.setScaleX(factor);
                polygon.setScaleY(factor);
            });
            country.setCapital(new Point2D(country.getCapital().getX()*factor, country.getCapital().getY()*factor));
        }));
    }

    /**
     * Returns all continents
     * @return continents
     */
    public List<Continent> getContinentList(){
        return continentList;
    }

}