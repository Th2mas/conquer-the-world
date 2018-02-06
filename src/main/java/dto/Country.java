package dto;

import javafx.geometry.Point2D;

import java.util.List;
import java.util.Objects;

public class Country {
    private String name;
    private List<Point2D> points;

    public Country(String name, List<Point2D> points) {
        this.name = name;
        this.points = points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(List<Point2D> points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public List<Point2D> getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(name, country.name) &&
                Objects.equals(points, country.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, points);
    }
}
