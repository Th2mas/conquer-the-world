package service;

import dto.Continent;
import exceptions.IllegalCommandException;

import java.io.IOException;
import java.util.List;

/**
 * The interface for every continent service
 */
public interface ContinentService {

    /**
     * Returns the list of all continents
     * @return the list with all continents
     * @throws IOException will be thrown, if something went wrong during reading
     * @throws IllegalCommandException will be thrown, if an illegal command happened
     */
    List<Continent> getContinents() throws IOException, IllegalCommandException;
}
