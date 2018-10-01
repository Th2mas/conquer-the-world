package service.impl;

import dto.Continent;
import dto.Country;
import exceptions.IllegalCommandException;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import service.ContinentService;
import util.properties.PropertiesManager;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default continent service, which reads the .map file for the continents and all its countries
 */
public class SimpleContinentService implements ContinentService {

    private static ContinentService continentService;

    /**
     * The service's continent list
     */
    private List<Continent> continentList;

    /**
     * The map with the original polygons, belonging to the country
     * TODO: Optimize me!
     */
    private static Map<String, Country> originalCountriesMap;

    // private constructor
    private SimpleContinentService() {}

    /**
     * Returns the continent service, which can be used for performing actions on the continent
     * @return the continent service
     */
    public static ContinentService getContinentService() {
        if(continentService == null) {
            continentService = new SimpleContinentService();
            originalCountriesMap = new HashMap<>();
        }
        return continentService;
    }

    @Override
    public List<Continent> getContinents() throws IllegalCommandException, IOException {
        return getContinents("/map/world.map");
    }

    @Override
    public List<Continent> getContinents(String map) throws IOException, IllegalCommandException {
        if(continentList == null) continentList = new SimpleMapReader().readFile(map);
        return continentList;
    }

    @Override
    public void resizePatches(double factorX, double factorY){
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            resizeCountry(country, factorX, factorY);
        }));
    }

    @Override
    public void resizeCountry(Country country, double factorX, double factorY) {
        // Get the original country and it's polygons
        Country originalCountry = originalCountriesMap.get(country.getBaseName());

        List<Polygon> originalPatches = originalCountry.getPatches();
        List<Polygon> newPatches = country.getPatches();

        Translate translate = new Translate(factorX, factorY);
        Scale scale = new Scale(factorX, factorY);

        if(originalPatches.size() != newPatches.size()) throw new IllegalStateException("originalPatches and newPatches must have the same length!");

        Polygon originalPatch, newPatch;
        for(int i = 0; i < originalPatches.size(); i++) {
            originalPatch = originalPatches.get(i);
            newPatch = newPatches.get(i);

            newPatch.getPoints().clear();
            newPatch.getPoints().addAll(originalPatch.getPoints());

            // Get the current points and transform them into Point2D
            List<Double> doubles = newPatch.getPoints();
            List<Point2D> points = new ArrayList<>();
            for(int j = 0; j < doubles.size(); j += 2) points.add(new Point2D(doubles.get(j), doubles.get(j+1)));

            // Create two new lists, which will be used for storing the new values
            List<Point2D> outPoints = new ArrayList<>();
            List<Double> outDoubles = new ArrayList<>();

            // Now we can transform our new patch, as it now has the standard values
            points.forEach(point2D -> {
                Point2D point = point2D;

                // Translate the point
                point = translate.transform(point);

                // Scale the point
                point = scale.transform(point);

                outPoints.add(point);
            });

            // Store the calculated points in the doubles list
            outPoints.forEach(point2D -> {
                outDoubles.add(point2D.getX());
                outDoubles.add(point2D.getY());
            });

            newPatch.getPoints().clear();
            newPatch.getPoints().addAll(outDoubles);

            country.getPatches().set(i, newPatch);
        }

        // Resize the position of the capital
        // Get current position
        Point2D capital = originalCountry.getCapital();
        capital = translate.transform(capital);
        capital = scale.transform(capital);

        // Translate the position
        country.setCapital(capital);
    }

    @Override
    public void setupContinentService(Group root) {
        // Add all patches to the root group
        continentList.forEach(continent -> continent.getCountries().forEach(country -> {
            country.getPatches().forEach(patch -> root.getChildren().add(patch));

            // Clone the country and put it into the map with its polygon list
            Country clonedCountry = country.clone();

            // Put the country inside the map with it's default name (with removed whitespaces
            originalCountriesMap.put(clonedCountry.getBaseName(), clonedCountry);

            // Set the continent's locale name
            continent.setName(PropertiesManager.getString("Continent."+continent.getBaseName().replaceAll("\\s+",""),"lang"));

            // Set the country's locale name
            country.setName(PropertiesManager.getString("Country."+country.getBaseName().replaceAll("\\s+",""), "lang"));

        }));
    }

}