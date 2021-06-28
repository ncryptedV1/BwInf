package de.ncrypted.dreiecke;

import de.ncrypted.dreiecke.geometry.Angle;
import de.ncrypted.dreiecke.geometry.Triangle;
import de.ncrypted.dreiecke.geometry.TrianglePath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author ncrypted
 */
public class Dreiecke {

    public static List<Triangle> triangles = new ArrayList<>();
    public static List<Triangle.AnglePair> algorithmPairs = new ArrayList<>();
    public static List<TrianglePath> finalTriangles = new ArrayList<>();

    public static void main(String[] args) {
        String coordsFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (coordsFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: dreiecke2.txt)");
            try {
                String line = reader.readLine();
                if (Dreiecke.class.getClassLoader().getResource("examples/" + line) != null) {
                    coordsFileName = line;
                }
            } catch (IOException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        System.out.println("---INFOS---");
        // read information from file
        readFileData(coordsFileName);
        for (Triangle triangle : triangles) {
            System.out.println(triangle.toString());
        }
        // execute algorithm
        System.out.println("---ALGORITHMUS---");
        executeAlgorithm();
        System.out.println("Algorithmus-Paare:");
        for (Triangle.AnglePair algorithmPair : algorithmPairs) {
            System.out.println(
                    algorithmPair.getAngle1().toString() + " + " + algorithmPair.getAngle2().toString());
        }
        SVGGenerator.generateSVG(coordsFileName.substring(0, coordsFileName.length() - 4) + ".svg");
        // print out result
        System.out.println("---ERGEBNIS---");
        for (TrianglePath finalTriangle : finalTriangles) {
            System.out.println(finalTriangle.toString());
        }
        double firstX = 0;
        double lastX = Double.POSITIVE_INFINITY;
        for (Point2D corner : finalTriangles.get(0).getCorners()) {
            if (corner.getY() == 0 && corner.getX() > firstX) {
                firstX = corner.getX();
            }
        }
        for (Point2D corner : finalTriangles.get(finalTriangles.size() - 1).getCorners()) {
            if (corner.getY() == 0 && corner.getX() < lastX) {
                lastX = corner.getX();
            }
        }
        double distance = lastX - firstX;
        System.out.println("Distanz: " + Utils.round(distance, 2));
    }

    private static void executeAlgorithm() {
        // sort by area asc
        triangles.sort(Comparator.comparing(Triangle::getArea));

        // create pairs of almost 90 degree (only angles at longest side of a triangle) to form rectangles
        List<Triangle.AnglePair> anglePairs = new ArrayList<>();
        for (Triangle triangle : triangles) {
            anglePairs.add(triangle.getTwoSmallestAngles());
        }

        List<Angle> angles1 = new ArrayList<>();
        List<Angle> angles2 = new ArrayList<>();
        for (Triangle.AnglePair anglePair : anglePairs) {
            angles1.add(anglePair.getAngle1());
            angles2.add(anglePair.getAngle2());
        }

        angles1.sort(Comparator.comparingDouble(Angle::getDegrees));
        angles2.sort(Comparator.comparingDouble(Angle::getDegrees));

        int n = triangles.size();
        for (int i = 0; i < n / 2; i++) {
            Angle start1 = angles1.get(0);
            Angle start2 = getPairAngle(start1, angles2);

            // get best angle to create a sum near to 90° for first angle of current AnglePair
            Angle partner1 = bestPair(start1, angles1, angles2);
            // get best angle to create a sum near to 90° for second angle of current AnglePair
            Angle partner2 = bestPair(start2, angles1, angles2);
            // get best angle to create sum near to 90° from both angles of current AnglePair
            Angle partner = nearerTo90(start1, partner1, start2, partner2);
            boolean second = partner == partner2 ? true : false;
            Angle start = second ? start2 : start1;

            // save AnglePair - triangle with shorter angle adjacent side first
            if (start.getTriangle().getSideFromType(Triangle.getShortAdjacentSide(start)) <
                    partner.getTriangle().getSideFromType(Triangle.getShortAdjacentSide(partner))) {
                algorithmPairs.add(new Triangle.AnglePair(start.getTriangle(), partner.getTriangle(),
                        start.getType(), partner.getType()));
            } else {
                algorithmPairs.add(new Triangle.AnglePair(partner.getTriangle(), start.getTriangle(),
                        partner.getType(), start.getType()));
            }

            // remove angles in this pair from available angles
            for (int p = 0; p < angles1.size(); p++) {
                if (sameTriangle(angles1.get(p), start)) {
                    angles1.remove(p);
                } else if (sameTriangle(angles1.get(p), partner)) {
                    angles1.remove(p);
                }
                if (sameTriangle(angles2.get(p), start)) {
                    angles2.remove(p);
                } else if (sameTriangle(angles2.get(p), partner)) {
                    angles2.remove(p);
                }
            }
        }

        // check if there's an odd number of triangles -> put biggest at the end
        if (n % 2 != 0) {
            Triangle spareTriangle = triangles.get(triangles.size() - 1);
            algorithmPairs.add(new Triangle.AnglePair(spareTriangle, spareTriangle,
                    spareTriangle.getTwoSmallestAngles().getAngle1().getType(),
                    spareTriangle.getTwoSmallestAngles().getAngle1().getType()));
        }
    }

    private static Angle getPairAngle(Angle first, List<Angle> angles) {
        for (Angle angle : angles) {
            if (sameTriangle(first, angle)) {
                return angle;
            }
        }
        return null;
    }

    private static boolean sameTriangle(Angle angle1, Angle angle2) {
        return angle1.getTriangle().getName().equals(angle2.getTriangle().getName());
    }

    private static Angle bestPair(Angle angleStart, List<Angle> angles1, List<Angle> angles2) {
        Angle partner1 = null;
        Angle partner2 = null;
        // create sums with all AnglePairs following in the array
        for (int j = 0; j < angles1.size(); j++) {
            Angle check1 = angles1.get(j);
            Angle check2 = angles2.get(j);

            if (check1 != angleStart && !sameTriangle(check1, angleStart)) {
                // create sum of first angle of the AnglePair
                partner1 = partner1 == null ? check1 : nearerTo90(angleStart, partner1, check1);
            }

            if (check2 != angleStart && !sameTriangle(check2, angleStart)) {
                // create sum of second angle of the AnglePair
                partner2 = partner2 == null ? check2 : nearerTo90(angleStart, partner2, check2);
            }
        }
        return nearerTo90(angleStart, partner1, partner2);
    }

    private static Angle nearerTo90(Angle start, Angle check1, Angle check2) {
        double diff1 = Math.abs(90 - start.getDegrees() - check1.getDegrees());
        double diff2 = Math.abs(90 - start.getDegrees() - check2.getDegrees());
        if (diff1 < diff2) {
            return check1;
        } else {
            return check2;
        }
    }

    private static Angle nearerTo90(Angle start1, Angle partner1, Angle start2, Angle partner2) {
        double diff1 = Math.abs(90 - start1.getDegrees() - partner1.getDegrees());
        double diff2 = Math.abs(90 - start2.getDegrees() - partner2.getDegrees());
        if (diff1 < diff2) {
            return partner1;
        } else {
            return partner2;
        }
    }

    public static Triangle getTriangle(String name) {
        for (Triangle triangle : triangles) {
            if (triangle.getName().equals(name)) {
                return triangle;
            }
        }
        return null;
    }

    private static void readFileData(String fileName) {
        try {
            InputStream fileStream = Dreiecke.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int current = 0;
            while (line != null) {
                // check whether it is the first line -> skip
                if (current > 0) {
                    int corners = asInt(line, 0);
                    List<Point> cornerCoords = new ArrayList<>();
                    for (int i = 0; i < corners; i++) {
                        cornerCoords.add(new Point(asInt(line, i * 2 + 1), asInt(line, i * 2 + 2)));
                    }
                    triangles.add(new Triangle("D" + current, cornerCoords));
                }
                current++;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer asInt(String str, int index) {
        return Integer.parseInt(str.split(" ")[index]);
    }
}
