package dto;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable transfer object for carrying information about a country
 */
public class Country {

    /**
     * The country's unique name
     */
    private final String name;

    /**
     * The list of (x,y) coordinates of the country's border
     */
    private final List<Polygon> patches;

    /**
     * The (x,y) coordinate of the country's capital
     */
    private final Point2D capital;

    /**
     * The country's neighbors (in string format)
     */
    private List<String> neighbors;

    /**
     * Creates a new country without any neighbors
     * @param name the country's name
     * @param patches the country's polygons
     * @param capital the country's capital in (x,y) coordinates
     */
    public Country(String name, List<Polygon> patches, Point2D capital){
        this(name, patches, capital, new ArrayList<>());
    }

    /**
     * Creates a new country
     * Parameters cannot be null
     * @param name the country's name
     * @param patches the country's polygons
     * @param capital the country's capital in (x,y) coordinates
     * @param neighbors the country's neighbors
     */
    public Country(String name, List<Polygon> patches, Point2D capital, List<String> neighbors) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(patches);
        Objects.requireNonNull(capital);
        Objects.requireNonNull(neighbors);

        this.name = name;
        this.patches = patches;
        this.capital = capital;
        this.neighbors = neighbors;
    }

    /**
     * Gets the country's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the country's patches
     * @return patches
     */
    public List<Polygon> getPatches() {
        return patches;
    }

    /**
     * Gets the country's capital
     * @return capital
     */
    public Point2D getCapital() {
        return capital;
    }

    /**
     * Sets the country's neighbors
     * @param neighbors the neighbors to be set
     */
    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Gets the country's neighbors
     * @return neighbors
     */
    public List<String> getNeighbors() {
        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(name, country.name) &&
                Objects.equals(patches, country.patches) &&
                Objects.equals(capital, country.capital) &&
                Objects.equals(neighbors, country.neighbors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, patches, capital, neighbors);
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", patches=" + patches +
                ", capital=" + capital +
                ", neighbors=" + neighbors +
                '}';
    }
}
