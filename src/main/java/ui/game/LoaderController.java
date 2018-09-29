package ui.game;

import dto.Continent;
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
import java.util.List;
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
     * Creates the game controller with the default map
     */
    LoaderController(Group root, String map){

        // Try to get the default map
        try { continentList = new SimpleMapReader().readFile(LoaderController.class.getResource(map).getPath()); }
        catch (IOException | IllegalCommandException e) {
            DialogHelper.createErrorDialog(e.getMessage());
            // Exit the program
            System.exit(-1);
        }

        this.root = root;

        // Draws the lines connecting the capitals
        // Default width: the settings width
        redrawLines(PropertiesManager.getInt("window.size.x", "window"));

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
     * @param factorX scale factor in x direction
     * @param factorY scale factor in y direction
     */
    void resizePatches(double factorX, double factorY){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {

            Translate translate = new Translate(factorX, factorY);
            Scale scale = new Scale(factorX, factorY);

            country.getPatches().forEach(polygon -> {

                List<Double> doubleList = resizePolygon(polygon, factorX, factorY);

                // Clear the list of polygons and add the newly calculated polygons
                polygon.getPoints().clear();

                // Add the newly scaled points
                polygon.getPoints().addAll(doubleList);
            });

            // Resize the position of the capital
            // Get current position
            Point2D capital = country.getCapital();
            capital = translate.transform(capital);
            capital = scale.transform(capital);

            // Translate the position
            country.setCapital(capital);
        }));
    }

    public List<Double> resizePolygon(Polygon polygon, double factorX, double factorY) {

        Translate translate = new Translate(factorX, factorY);
        Scale scale = new Scale(factorX, factorY);

        // Create a list of Point2D
        List<Point2D> points = new ArrayList<>();
        List<Point2D> outPoints = new ArrayList<>();
        ObservableList<Double> doubles = polygon.getPoints();
        ObservableList<Double> outDoubles = FXCollections.observableArrayList();

        // Set the points
        for(int i=0; i<doubles.size(); i+=2) points.add(new Point2D(doubles.get(i), doubles.get(i+1)));

        points.forEach(point2D -> {

            // Translate every point to its new position
            Point2D newPoint = translate.transform(point2D);

            // Scale every point
            newPoint = scale.transform(newPoint);

            // Save the points in the polygon
            outPoints.add(newPoint);
        });

        outPoints.forEach(point2D -> {
            outDoubles.add(point2D.getX());
            outDoubles.add(point2D.getY());
        });

        return outDoubles;
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