package de.ncrypted.dreiecke.geometry;

/**
 * @author ncrypted
 */
public class Angle {

    private Triangle triangle;
    private double degrees;
    private AngleType type;

    public Angle(Triangle triangle, AngleType type) {
        this.triangle = triangle;
        this.degrees = triangle.getAngleFromType(type);
        this.type = type;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public double getDegrees() {
        return degrees;
    }

    public AngleType getType() {
        return type;
    }

    public String toString() {
        return getTriangle().getName() + "-" + getType();
    }
}
