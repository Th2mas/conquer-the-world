package ui.game;

import dto.Continent;
import exceptions.IllegalCommandException;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.dialog.DialogHelper;
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
    LoaderController(Group root){

        // Try to get the default map
        try { continentList = new SimpleMapReader().readFile(LoaderController.class.getResource("/map/world.map").getPath()); }
        catch (IOException | IllegalCommandException e) {
            DialogHelper.createErrorDialog(e.getMessage());
            // Exit the program
            // TODO: Maybe end the program otherwise
            throw new RuntimeException(e.getMessage());
        }

        int width = PropertiesManager.getInt("window.size.x", "window");

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
    void setColors(){

        // If the continentList is present, set the default colors to the continents
        continentList.forEach(continent -> {
            Color color = Color.WHITE;
            switch (continent.getName()){
                case "North America":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.NorthAmerica", "custom"));
                    break;
                case "South America":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.SouthAmerica", "custom"));
                    break;
                case "Europe":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.Europe", "custom"));
                    break;
                case "Africa":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.Africa", "custom"));
                    break;
                case "Asia":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.Asia", "custom"));
                    break;
                case "Australia":
                    color = Color.valueOf(PropertiesManager.getString("Color.Continent.Australia", "custom"));
                    break;
            }
            continent.setColor(color);
            continent.getCountries().forEach(country -> country.getPatches().forEach(polygon -> { polygon.setFill(continent.getColor()); }));
        });
    }

    /**
     * This method darkens the color of the country on mouse over
     */
    void darkenPatchesOnMouseOver(){
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
     * TODO: Doesn't scale correctly. So implement the correct scaling please!
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
    List<Continent> getContinentList(){
        return continentList;
    }

}