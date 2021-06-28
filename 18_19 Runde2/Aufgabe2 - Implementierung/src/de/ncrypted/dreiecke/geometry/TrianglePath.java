package de.ncrypted.dreiecke.geometry;

import de.ncrypted.dreiecke.Utils;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ncrypted
 */
public class TrianglePath extends Path2D.Double {

    private String name;
    private List<Point2D> corners = new ArrayList<>();

    public double a;
    public double b;
    public double c;
    public double alpha;
    public double beta;
    public double gamma;

    public TrianglePath(String name, Path2D path) {
        super(path);
        this.name = name;

        PathIterator pi = path.getPathIterator(null);
        for (int i = 0; i < 3; i++) {
            double[] coords = new double[6];
            pi.currentSegment(coords);
            corners.add(new Point2D.Double(coords[0], coords[1]));
            pi.next();
        }

        // set side lengths
        a = corners.get(0).distance(corners.get(1));
        b = corners.get(1).distance(corners.get(2));
        c = corners.get(2).distance(corners.get(0));

        // calculate angles
        alpha = lawOfCosines(b, c, a);
        beta = lawOfCosines(a, c, b);
        gamma = lawOfCosines(a, b, c);
    }

    public String getName() {
        return name;
    }

    public List<Point2D> getCorners() {
        return corners;
    }

    public double getSideFromType(SideType type) {
        switch (type) {
            case A:
                return a;
            case B:
                return b;
            case C:
                return c;
        }
        return -1;
    }

    public double getAngleFromType(AngleType type) {
        switch (type) {
            case ALPHA:
                return alpha;
            case BETA:
                return beta;
            case GAMMA:
                return gamma;
        }
        return -1;
    }

    public Point2D getCenter() {
        int sumX = 0;
        int sumY = 0;
        for (Point2D corner : corners) {
            sumX += corner.getX();
            sumY += corner.getY();
        }
        return new Point2D.Double(sumX / corners.size(), sumY / corners.size());
    }

    private static double lawOfCosines(double a, double b, double c) {
        // basic formula: c^2=a^2+b^2-2ab*cosy
        // rearranged to y: y=cos^-1((c^2-a^2-b^2)/(-2ab))
        return Math.toDegrees(Math.acos((c * c - a * a - b * b) / (-2 * a * b)));
    }

    public String toString() {
        return getName() + ":" + cornerToString(0) + cornerToString(1) + cornerToString(2) + " a=" + Utils.round(a, 2) +
                " b=" + Utils.round(b, 2) + " c=" + Utils.round(c, 2) +
                " Alpha=" + Utils.round(alpha, 2) + " Beta=" + Utils.round(beta, 2) + " Gamma=" + Utils.round(gamma, 2);
    }

    private String cornerToString(int id) {
        return " Ecke" + (id + 1) + ": " + Utils.round(corners.get(id).getX(), 2) + "|" +
                Utils.round(corners.get(id).getY(), 2);
    }
}
