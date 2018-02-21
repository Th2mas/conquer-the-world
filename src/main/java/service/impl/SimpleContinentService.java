package service.impl;

import dto.Continent;
import exceptions.IllegalCommandException;
import service.ContinentService;
import util.reader.impl.SimpleMapReader;

import java.io.IOException;
import java.util.List;

/**
 * The default continent service, which reads the .map file for the continents and all its countries
 */
public class SimpleContinentService implements ContinentService {
    @Override
    public List<Continent> getContinents() throws IllegalCommandException, IOException {
        return new SimpleMapReader().readFile(SimpleContinentService.class.getResource("/map/world.map").getPath());
    }
}
