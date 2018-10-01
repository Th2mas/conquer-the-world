package service;

import dto.Continent;
import dto.Country;
import exceptions.IllegalCommandException;
import javafx.scene.Group;

import java.io.IOException;
import java.util.List;

/**
 * The interface for every continent service
 */
public interface ContinentService {

    /**
     * Returns the list of all continents (default map)
     * @return the list with all continents
     * @throws IOException will be thrown, if something went wrong during reading
     * @throws IllegalCommandException will be thrown, if an illegal command happened
     */
    List<Continent> getContinents() throws IOException, IllegalCommandException;

    /**
     * Returns the list of all continents of the given map
     * @param map the map, which contains the continents
     * @return the list with all continents
     * @throws IOException will be thrown, if something went wrong during reading
     * @throws IllegalCommandException will be thrown, if an illegal command happened
     */
    List<Continent> getContinents(String map) throws IOException, IllegalCommandException;

    /**
     * Scales all patches with the given factor
     * @param factorX scale factor in x direction
     * @param factorY scale factor in y direction
     */
    void resizePatches(double factorX, double factorY);

    /**
     * Resize the country
     * @param country the country, which polygons need to be resized
     * @param factorX the factor in x-direction (should be in relation to the base width)
     * @param factorY the factor in y-direction (should be in relation to the base height)
     */
    void resizeCountry(Country country, double factorX, double factorY);

    /**
     * Sets up all relevant information for the continent service
     * @param root the root element
     */
    void setupContinentService(Group root);
}
