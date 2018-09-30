package ui.game;

import dto.Continent;
import dto.Country;
import exceptions.IllegalCommandException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import util.dialog.DialogHelper;
import util.properties.PropertiesManager;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The controller class for loading the continents
 */
class LoaderController {

    /**
     * The reader, which will read the .map file and return the continents
     */
    private List<Continent> continentList;

    /**
     * The root element, in which all elements will be stored
     */
    private Group root;

    /**
     * The map with the original polygons, belonging to the country
     * TODO: Optimize me!
     */
    private final Map<String, Country> originalCountriesMap;

    /**
     * Creates the game controller with the default map
     */
    LoaderController(Group root, String map){

        // Try to get the default map
        try { continentList = new SimpleMapReader().readFile(map); }
        catch (IOException | IllegalCommandException e) {
            DialogHelper.createErrorDialog(e.getMessage());
            // Exit the program
            System.exit(-1);
        }

        this.root = root;
        originalCountriesMap = new HashMap<>();

        // Draws the lines connecting the capitals
        // Default width: the settings width
        redrawLines(PropertiesManager.getInt("window.size.x", "window"));

        // Add all patches to the root group
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(patch -> root.getChildren().add(patch));

            // Clone the country and put it into the map with its polygon list
            Country clonedCountry = country.clone();

            // Put the country inside the map with it's default name (with removed whitespaces
            originalCountriesMap.put(clonedCountry.getBaseName(), clonedCountry);
        }));
    }

    /**
     * This method sets different colors for the continents
     */
    void setColors(){

        // If the continentList is present, set the default colors to the continents
        continentList.forEach(continent -> {
            Color color = Color.WHITE;
            switch (continent.getBaseName()){
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
     * TODO: Move to continent service?
     * @param factorX scale factor in x direction
     * @param factorY scale factor in y direction
     */
    void resizePatches(double factorX, double factorY){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            Translate translate = new Translate(factorX, factorY);
            Scale scale = new Scale(factorX, factorY);

            resizeAllPolygons(country, factorX, factorY);

            // Resize the position of the capital
            // Get current position
            // TODO: Change that as well...
            //Point2D capital = country.getCapital();
            //capital = translate.transform(capital);
            //capital = scale.transform(capital);

            // Translate the position
            //country.setCapital(capital);
        }));
    }

    /**
     * Resize the polygon
     * @param country the country, which polygons need to be resized    -> TODO Move to continent service?
     * @param factorX the factor in x-direction (should be in relation to the base width)   // TODO
     * @param factorY the factor in y-direction (should be in relation to the base height)  // TODO
     */
    private void resizeAllPolygons(Country country, double factorX, double factorY) {
        // TODO: Implement me!
    }

    void redrawLines(double width) {
        // Remove all lines, which might be present due to already drawn once
        root.getChildren().removeAll(root.getChildren().stream()
                .filter(n -> n instanceof Line)
                .collect(Collectors.toList()));

        // Add lines, which connect the countries with their neighbors
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            // Draw the line between the country and the country's neighbors
            country.getNeighbors().forEach(neighbor -> {
                Line line;
                // Check if the line length would be greater than the half of the window width
                // If this is the case, then just draw the line to 0 or window width
                if(Math.abs((country.getCapital().getX()-neighbor.getCapital().getX())) > width/2){
                    // Check if the start position is on the left or right half of the screen and set the correct end x position
                    if(country.getCapital().getX() < width/2)
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), 0, neighbor.getCapital().getY());
                    else
                        line = new Line(country.getCapital().getX(), country.getCapital().getY(), width, neighbor.getCapital().getY());
                }
                else
                    line = new Line(country.getCapital().getX(), country.getCapital().getY(), neighbor.getCapital().getX(), neighbor.getCapital().getY());

                // Add the lines at index 0, so that all other elements will be drawn over it
                root.getChildren().add(0, line);
            });
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