package util.reader.impl;

import dto.Continent;
import dto.Country;
import exceptions.IllegalCommandException;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.reader.MapReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

/**
 * A simple map reader, which reads a map file with the following format:
 * patch­of &lt;Country&gt; &lt;x0&gt; &lt;y0&gt; &lt;x1&gt; &lt;y1&gt; ... &lt;xn&gt; &lt;yn&gt;
 * capital­of &lt;Country&gt; &lt;x&gt; &lt;y&gt;
 * neighbors­of &lt;Country&gt; : &lt;T1&gt; ­ &lt;T2&gt; ­ ... &lt;Tn&gt;
 * continent &lt;Cont&gt; &lt;N&gt; : &lt;T1&gt; ­ &lt;T2&gt; ­ ... &lt;Tn&gt;
 */
public class SimpleMapReader implements MapReader {

    /**
     * The {@link SimpleMapReader} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMapReader.class);

    @Override
    public List<Continent> readFile(String path) throws IOException, IllegalCommandException {
        LOGGER.info("Try to read " + path);

        // The list, which will contain all continents
        List<Continent> continentsList = new ArrayList<>();

        // The map, which will contain all patches
        Map<String, List<Polygon>> patchesMap = new HashMap<>();

        // The list, which will contain all capitals
        Map<String, Point2D> capitalsMap = new HashMap<>();

        // The list, which will contain every country's name
        List<String> names = new ArrayList<>();

        // The list, which will contain all countries
        Map<String, Country> countries = new HashMap<>();

        List<String> neighborsStringList = new ArrayList<>();
        List<String> continentsStringList = new ArrayList<>();
        List<String> patchesStringList = new ArrayList<>();
        List<String> capitalStringList = new ArrayList<>();

        // Run through every line in the file
        // (Using a simple for loop for throwing an exception)
        for(String line : Files.readAllLines(Paths.get(path))){

            // Get the command
            String[] sp = line.split(" ");

            // Substring the line, so you can work better with it
            line = line.substring(sp[0].length()+1);

            // Delegate the command
            switch (sp[0]){
                case "patch-of":
                    patchesStringList.add(line);
                    break;
                case "capital-of":
                    capitalStringList.add(line);
                    break;
                case "neighbors-of":
                    neighborsStringList.add(line);
                    break;
                case "continent":
                    continentsStringList.add(line);
                    break;
                default: throw new IllegalCommandException("Command " + sp[0] + " unknown!");
            }
        }

        // Add the patches (polygons)
        patchesStringList.forEach(line -> {
            // Split the line again, so you can get the name and the (x,y) coordinates of the patch
            String[] sp = line.split(" ");
            StringBuilder nameBuilder = new StringBuilder();
            List<Integer> border = new ArrayList<>();

            for(String s : sp){
                // Check for the name
                if(isNonNumeric(s)) nameBuilder.append(s).append(" ");
                    // else just add the integer to the border list
                else border.add(Integer.parseInt(s));
            }

            // Remove the last space character
            nameBuilder.deleteCharAt(nameBuilder.length()-1);

            // Create the new polygon from the border list
            Polygon polygon = new Polygon(border.stream().mapToDouble(i->i).toArray());

            // Add the polygon to the patchesMap
            List<Polygon> polygons = patchesMap.get(nameBuilder.toString());
            if(polygons == null) polygons = new ArrayList<>();
            polygons.add(polygon);
            patchesMap.put(nameBuilder.toString(), polygons);
        });

        // Add the capitals
        capitalStringList.forEach(line -> {
            // Check if the length is min. 3 (name, x, y)
            if(line.length() < 3) throw new IllegalArgumentException("Line must have a length of 3 or more in 'capital-of'!");

            // Split the line again, so you can get the name and the (x,y) coordinate of the capital
            String[] sp = line.split(" ");
            StringBuilder nameBuilder = new StringBuilder();
            List<Integer> coords = new ArrayList<>();

            for(String s : sp){
                // Check for the name
                if(isNonNumeric(s)) nameBuilder.append(s).append(" ");
                else coords.add(Integer.parseInt(s));
            }

            // Remove the last space character
            nameBuilder.deleteCharAt(nameBuilder.length()-1);

            // Add the names to the 'names' list
            String name = nameBuilder.toString();
            names.add(name);

            capitalsMap.put(name, new Point2D(coords.get(0), coords.get(1)));
        });

        // Create the countries
        names.forEach(name -> countries.put(name, new Country(name, patchesMap.get(name), capitalsMap.get(name))));

        // Add the countries to their respective continent
        continentsStringList.forEach(line -> {

            // Split the continent with its points and the countries
            String[] sp = line.split(" : ");

            // Split for continent name and points
            String[] sp2 = sp[0].split(" ");

            // Get the name
            StringBuilder nameBuilder = new StringBuilder();
            for(int i=0; i<sp2.length-1; i++)
                nameBuilder.append(sp2[i]).append(" ");

            // Remove the last space character
            nameBuilder.deleteCharAt(nameBuilder.length()-1);

            // Get the points
            int points = Integer.parseInt(sp2[sp2.length-1]);

            // Split the countries
            List<String> c = new ArrayList<>(Arrays.asList(sp[1].split(" - ")));

            // Create the countries list
            Map<String, Country> copyCountries = new HashMap<>(countries);

            Predicate<Country> notContainsCountry = p -> !c.contains(p.getName());
            copyCountries.values().removeIf(notContainsCountry);

            // Add the countries to the continent
            continentsList.add(new Continent(new ArrayList<>(copyCountries.values()), points, nameBuilder.toString()));
        });

        // Add the neighbors
        neighborsStringList.forEach(line -> {
            // Split the actual country from the neighbors
            String[] sp = line.split(" : ");

            // Split the neighbors
            List<String> neighs;
            if(sp[1].contains("-")) neighs = new ArrayList<>(Arrays.asList(sp[1].split(" - ")));
            else neighs = new ArrayList<>(Collections.singletonList(sp[1]));

            // Get the actual neighbors
            List<Country> neighbors = new ArrayList<>();

            continentsList.forEach(continent -> continent.getCountries().forEach(country -> {
                neighs.forEach(neigh -> {
                    if(country.getName().equalsIgnoreCase(neigh)) neighbors.add(country);
                });
            }));
            Country c = countries.get(sp[0]);
            c.setNeighbors(neighbors);
            countries.put(sp[0], c);

        });

        // Complete the missing neighbors (the .map file does not always have all correct neighbors)
        continentsList.forEach(continent -> continent.getCountries().forEach(country -> country.getNeighbors().forEach(neighbor -> {
            if(!neighbor.getNeighbors().contains(country)) neighbor.getNeighbors().add(country);
        })));

        return continentsList;
    }

    /**
     * Checks if the given string is a number
     * @param s the string to be checked
     * @return true, if the string is a number; otherwise false
     */
    private static boolean isNonNumeric(String s){
        LOGGER.debug("isNonNumeric(" + s + ")");
        try {
            Double.parseDouble(s);
            return false;
        } catch(NumberFormatException e){
            return true;
        }
    }
}
