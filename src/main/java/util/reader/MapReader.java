package util.reader;

import dto.Continent;

import java.util.List;

/**
 * A .map reader
 */
public interface MapReader {

    /**
     * Reads a given .map file and tries to create a list of continents
     * @param relativePath the relative path to the .map file
     * @return a list of continents
     */
    List<Continent> readFile(String relativePath) throws Exception;
}
