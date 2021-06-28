package de.ncrypted.dreiecke.geometry;

/**
 * @author ncrypted
 */
public class Side {

    private double length;
    private SideType type;

    public Side(double length, SideType type) {
        this.length = length;
        this.type = type;
    }

    public double getLength() {
        return length;
    }

    public SideType getType() {
        return type;
    }
}
