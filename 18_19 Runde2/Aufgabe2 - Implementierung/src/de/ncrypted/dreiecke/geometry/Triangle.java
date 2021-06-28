package de.ncrypted.dreiecke.geometry;

import de.ncrypted.dreiecke.Dreiecke;
import de.ncrypted.dreiecke.Utils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author ncrypted
 */
public class Triangle extends java.awt.Polygon {

    private String name;
    private List<Point> corners;

    public double a;
    public double b;
    public double c;
    public double alpha;
    public double beta;
    public double gamma;
    public double area;

    public Triangle(String name, List<Point> corners) {
        if (corners.size() != 3) {
            throw new IllegalArgumentException("You need exactly 3 Corners");
        }
        this.name = name;
        this.corners = corners;

        for (Point corner : corners) {
            addPoint((int) corner.getX(), (int) corner.getY());
        }

        // set side lengths
        a = corners.get(0).distance(corners.get(1));
        b = corners.get(1).distance(corners.get(2));
        c = corners.get(2).distance(corners.get(0));

        // calculate angles
        alpha = lawOfCosines(b, c, a);
        beta = lawOfCosines(a, c, b);
        gamma = lawOfCosines(a, b, c);

        // calculate area
        double u = a + b + c;
        double s = u / 2;
        // herons formula
        area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    public String getName() {
        return name;
    }

    public List<Point> getCorners() {
        return corners;
    }

    public double getArea() {
        return area;
    }

    public SideType getLongestSide() {
        if (a > b && a > c) {
            return SideType.A;
        } else if (b > a && b > c) {
            return SideType.B;
        } else {
            return SideType.C;
        }
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

    public AnglePair getTwoSmallestAngles() {
        if (alpha > beta && alpha > gamma) {
            return new AnglePair(name, name, AngleType.BETA, AngleType.GAMMA);
        } else if (beta > alpha && beta > gamma) {
            return new AnglePair(name, name, AngleType.ALPHA, AngleType.GAMMA);
        } else {
            return new AnglePair(name, name, AngleType.ALPHA, AngleType.BETA);
        }
    }

    private static double lawOfCosines(double a, double b, double c) {
        // basic formula: c^2=a^2+b^2-2ab*cosy
        // rearranged to y: y=cos^-1((c^2-a^2-b^2)/(-2ab))
        return Math.toDegrees(Math.acos((c * c - a * a - b * b) / (-2 * a * b)));
    }

    public static List<SideType> getAdjacentSides(AngleType type) {
        switch (type) {
            case ALPHA:
                return Arrays.asList(SideType.B, SideType.C);
            case BETA:
                return Arrays.asList(SideType.A, SideType.C);
            case GAMMA:
                return Arrays.asList(SideType.A, SideType.B);
        }
        return null;
    }

    public static SideType getShortAdjacentSide(Angle angle) {
        for (SideType adjacentSide : getAdjacentSides(angle.getType())) {
            if (adjacentSide != angle.getTriangle().getLongestSide()) {
                return adjacentSide;
            }
        }
        return null;
    }

    public static class AnglePair {

        private Angle angle1;
        private Angle angle2;

        public AnglePair(Triangle triangle1, Triangle triangle2, AngleType type1, AngleType type2) {
            this.angle1 = new Angle(triangle1, type1);
            this.angle2 = new Angle(triangle2, type2);
        }

        public AnglePair(String triangle1, String triangle2, AngleType type1, AngleType type2) {
            this(Dreiecke.getTriangle(triangle1), Dreiecke.getTriangle(triangle2), type1, type2);
        }

        public Angle getAngle1() {
            return angle1;
        }

        public Angle getAngle2() {
            return angle2;
        }
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
