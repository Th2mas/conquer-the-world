package ui.game;

import dto.Continent;
import exceptions.IllegalCommandException;
import javafx.scene.paint.Color;
import service.impl.SimpleContinentService;
import util.dialog.DialogHelper;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.List;

/**
 * The controller class for loading the continents
 * It should only be used for loading and setting stuff, not changing any other properties
 */
class LoaderController {

    /**
     * The reader, which will read the .map file and return the continents
     */
    private List<Continent> continentList;

    /**
     * Creates the game controller with the default map
     */
    LoaderController(String map){

        // Try to get the default map
        try { continentList = SimpleContinentService.getContinentService().getContinents(map); }
        catch (IOException | IllegalCommandException e) {
            DialogHelper.createErrorDialog(e.getMessage());
            // Exit the program
            System.exit(-1);
        }
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
     * Returns all continents
     * @return continents
     */
    List<Continent> getContinentList(){
        return continentList;
    }
}