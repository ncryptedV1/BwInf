package de.ncrypted.abbiegen.geometry;

/**
 * @author ncrypted
 */
public class PointInt {

    private int x;
    private int y;

    public PointInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public double getAngleTo(PointInt point2) {
        int x2 = point2.x;
        int y2 = point2.y;
        return Math.abs((y2 - y) / (x2 - x + 0D));
    }
}
