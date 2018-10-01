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
    private String name;

    /**
     * The country's base name (in English)
     */
    private final String baseName;

    /**
     * The list of (x,y) coordinates of the country's border
     */
    private List<Polygon> patches;

    /**
     * The (x,y) coordinate of the country's capital
     */
    private Point2D capital;

    /**
     * The country's neighbors (in string format)
     */
    private List<Country> neighbors;

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
    private Country(String name, List<Polygon> patches, Point2D capital, List<Country> neighbors) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(patches);
        Objects.requireNonNull(capital);
        Objects.requireNonNull(neighbors);

        this.name = name;
        this.baseName = name;
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
     * Sets the country's name
     * @param name the country's name
     */
    public void setName(String name) {this.name = name;}

    /**
     * Gets the country's base name
     * @return baseName
     */
    public String getBaseName() {
        return baseName;
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
     * Sets the coordinates of the country's capital
     * @param capital the new position of the capital
     */
    public void setCapital(Point2D capital) {
        this.capital = capital;
    }


    /**
     * Gets the country's neighbors
     * @return neighbors
     */
    public List<Country> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets the country's neighbors
     * @param neighbors the neighbors to be set
     */
    public void setNeighbors(List<Country> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Checks if the given country is a neighbor
     * @param c country to be checked, if it is a neighbor
     * @return true, if it is a neighbor; otherwise false
     */
    public boolean hasNeighbor(Country c){
        return neighbors.contains(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(baseName, country.baseName) &&
                Objects.equals(patches, country.patches) &&
                Objects.equals(capital, country.capital);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseName);//, neighbors); -> recursive call...
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + baseName + '\'' +
                ", patches=" + patches +
                ", capital=" + capital +
                '}';
    }

    /**
     * Clones a country
     * @return the cloned country
     */
    @Override
    public Country clone() {
        List<Polygon> clonedPatches = new ArrayList<>();

        patches.forEach(patch -> {
            Polygon clonedPatch = new Polygon();
            clonedPatch.getPoints().addAll(new ArrayList<>(patch.getPoints()));
            clonedPatches.add(clonedPatch);
        });

        return new Country(name, clonedPatches, capital);
    }
}
