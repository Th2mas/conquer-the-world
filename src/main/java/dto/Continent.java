package dto;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

/**
 * An immutable transfer object for representing a continent
 */
public class Continent {

    /**
     * The list of the continent's countries
     */
    private final List<Country> countries;

    /**
     * The continent's value
     */
    private final int points;

    /**
     * The continent's color
     */
    private Color color;

    /**
     * Creates a new continent with the default color white
     * Parameters cannot be null
     * @param countries the continent's countries
     * @param points the continent's points
     */
    public Continent(List<Country> countries, int points) {
        this(countries, points, Color.WHITE);
    }

    /**
     * Creates a new continent
     * @param countries the continent's countries
     * @param points the continent's points
     * @param color the continent's color
     */
    public Continent(List<Country> countries, int points, Color color){
        Objects.requireNonNull(countries);
        Objects.requireNonNull(color);

        // 'points' must be greater than 0
        if(points <= 0) throw new IllegalArgumentException("points cannot be less or equal than 0!");

        this.countries = countries;
        this.points = points;
        this.color = color;
    }

    /**
     * Gets the continent's countries
     * @return countries
     */
    public List<Country> getCountries() {
        return countries;
    }

    /**
     * Gets the continent's points
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Gets the continent's color
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the continent's color
     * @param color the continent's color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Continent continent = (Continent) o;
        return points == continent.points &&
                Objects.equals(countries, continent.countries) &&
                Objects.equals(color, continent.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countries, points, color);
    }

    @Override
    public String toString() {
        return "Continent{" +
                "countries=" + countries +
                ", points=" + points +
                ", color=" + color +
                '}';
    }
}
