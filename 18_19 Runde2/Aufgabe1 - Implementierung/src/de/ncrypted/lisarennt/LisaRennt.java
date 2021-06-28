package de.ncrypted.lisarennt;

import de.ncrypted.lisarennt.dijkstra.Edge;
import de.ncrypted.lisarennt.dijkstra.Graph;
import de.ncrypted.lisarennt.dijkstra.PointNode;
import de.ncrypted.lisarennt.graphic.PolygonAdapter;
import de.ncrypted.lisarennt.graphic.SVGGenerator;

import java.awt.geom.Line2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ncrypted
 */
public class LisaRennt {

    public static List<PolygonAdapter> polygons = new ArrayList<>();
    public static PointNode lisaPoint;
    public static List<PointNode> resultPoints;
    public static List<Edge> edges;
    public static List<Edge> dijkstraWay = new ArrayList<>();

    public static void main(String[] args) {
        String coordsFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (coordsFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: lisarennt1.txt)");
            try {
                String line = reader.readLine();
                if (LisaRennt.class.getClassLoader().getResource("examples/" + line) != null) {
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
        System.out.println(lisaPoint.toString());
        for (PolygonAdapter polygon : polygons) {
            System.out.println(polygon.toString());
        }
        // execute algorithm
        System.out.println("---ALGORITHMUS---");
        executeAlgorithm();
        SVGGenerator.generateSVG(coordsFileName.substring(0, coordsFileName.length() - 4) + ".svg");
    }

    private static void readFileData(String fileName) {
        try {
            InputStream fileStream = LisaRennt.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int current = 0;
            int polygonCount = 0;
            while (line != null) {
                // check whether it is the first line -> read amount of polygons
                if (current == 0) {
                    polygonCount = asInt(line, 0);
                    // check whether it is the last line -> Lisas house
                } else if (current > polygonCount) {
                    lisaPoint = new PointNode("L", asInt(line, 0), asInt(line, 1));
                } else {
                    int corners = asInt(line, 0);
                    List<PointNode> cornerCoords = new ArrayList<>();
                    for (int i = 0; i < corners; i++) {
                        cornerCoords.add(
                                new PointNode("P" + current + "-" + i, asInt(line, i * 2 + 1), asInt(line, i * 2 + 2)));
                    }
                    polygons.add(new PolygonAdapter("P" + current, cornerCoords));
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

    private static void executeAlgorithm() {
        // intialize all nodes
        List<PointNode> nodes = new ArrayList<>();
        nodes.add(lisaPoint);
        polygons.forEach(polygon -> nodes.addAll(polygon.getCorners()));

        // initialize all edges
        edges = new ArrayList<>();
        Set<PolygonAdapter> unfinished = new HashSet<>(polygons);
        // iterate over all polygons
        for (PolygonAdapter polygon : polygons) {
            unfinished.remove(polygon);
            List<PointNode> corners = polygon.getCorners();
            // iterate over all corners of a polygon
            for (PointNode node : corners) {
                // add all connections to corners of other polygons
                for (PolygonAdapter targetPolygon : unfinished) {
                    for (PointNode target : targetPolygon.getCorners()) {
                        if (hasDirectConnection(node, target)) {
                            edges.add(new Edge(node, target));
                        }
                    }
                }
                // add connection to Lisas home, if one exists
                if (hasDirectConnection(node, lisaPoint)) {
                    edges.add(new Edge(node, lisaPoint));
                }
            }

            // add all connections between the polygon nodes
            int nPoints = polygon.npoints;
            for (int i = 0; i < nPoints - 1; i++) {
                edges.add(new Edge(corners.get(i), corners.get(i + 1)));
            }
            edges.add(new Edge(corners.get(nPoints - 1), corners.get(0)));
        }

        Graph graph = new Graph(nodes, edges);
        graph.execute(lisaPoint);

        for (PointNode node : nodes) {
            PointNode predecessor = node.getPredecessor();
            String name = predecessor == null ? null : predecessor.getName();
            System.out.println(
                    node.getName() + " " + Utils.round(node.getDistanceFromSource(), 2) + " " + name);
        }

        // get point with street connection (optimal not with an angle of 0°) and latest starting time
        PointNode streetConnectionPoint = null;
        PointNode streetPoint = null;
        double bestStreetPointWay = 0;
        double bestStartTime = 0;
        for (PointNode node : nodes) {
            PointNode potentialStreetPoint = new PointNode("S", 0, getStreetY(node));
            if (hasDirectConnection(node, potentialStreetPoint)) {
                // check if point is reachable by Lisa (Graph from Dijkstra)
                if (!node.getName().equals("L") && node.getPredecessor() == null) {
                    continue;
                }
                double streePointWay =
                        node.getDistanceFromSource() + node.distance(potentialStreetPoint.x, potentialStreetPoint.y);
                double startTime = calcStartSec(streePointWay, potentialStreetPoint.y);
                if (startTime > bestStartTime) {
                    streetConnectionPoint = node;
                    streetPoint = potentialStreetPoint;
                    bestStreetPointWay = streePointWay;
                    bestStartTime = startTime;
                }
            }
        }

        try {
            // create walking path
            resultPoints = lisaPoint.getPath(streetConnectionPoint);
            resultPoints.add(streetPoint);

            // bus (30 km/h) starts at 7:30
            // Lisa walks with 15 km/h -> calculate time when she has to leave home and still catches the bus
            // round up -> may walk slower
            int lisaSec = (int) Math.ceil((bestStreetPointWay / (15.0 / 3.6)));
            // round down -> may drive faster
            int busSec = (int) Math.floor((streetPoint.getY() / (30.0 / 3.6)));
            int timeshift = (int) Math.floor(busSec - lisaSec);

            // print out result
            System.out.println("---ERGEBNIS---");
            int startSec = 7 * 3600 + 30 * 60 + timeshift;
            System.out.println("Lisa's Weg: " + Utils.round(bestStreetPointWay, 2) + "m");
            System.out.println("Bei y: " + Utils.round(streetPoint.getY(), 2) + "m");
            System.out.println("Startzeit: " + getTimeFromSec(startSec));
            System.out.println("Laufzeit: " + getTimeFromSec(lisaSec));
            System.out.println("Ankunft: " + getTimeFromSec(startSec + lisaSec));
            String path = "";
            for (int i = 0; i < resultPoints.size(); i++) {
                path += resultPoints.get(i).getName() + " ";
            }
            System.out.println("Pfad: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static double calcStartSec(double lisaWay, double busWay) {
        // bus (30 km/h) starts at 7:30
        // Lisa walks with 15 km/h -> calculate time when she has to leave home and still catches the bus
        double lisaSec = (lisaWay / (15.0 / 3.6));
        double busSec = (busWay / (30.0 / 3.6));
        double timeshift = busSec - lisaSec;

        // print out result
        double startSec = 7 * 3600 + 30 * 60 + timeshift;
        return startSec;
    }

    private static double getStreetY(PointNode start) {
        double x = start.getX();
        double y = start.getY();
        double n = (2 * (Math.sqrt(Math.pow(y, 2) - 3 * Math.pow(x, 2)) + 2 * y)) / 3;
        if (n > start.getY()) {
            return n;
        } else {
            return start.getY();
        }
    }

    private static boolean hasDirectConnection(PointNode start, PointNode end) {
        Line2D line = new Line2D.Double(start, end);
        for (PolygonAdapter polygon : polygons) {
            for (Line2D polyLine : polygon.getLines()) {
                // check if line shares an end or starting point with polygon-line
                if (line.getP1().equals(polyLine.getP1()) || line.getP1().equals(polyLine.getP2()) ||
                        line.getP2().equals(polyLine.getP1()) || line.getP2().equals(polyLine.getP2())) {
                    continue;
                }
                if (line.intersectsLine(polyLine)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Integer asInt(String str, int index) {
        return Integer.parseInt(str.split(" ")[index]);
    }

    private static String getTimeFromSec(int seconds) {
        int h = seconds / 3600;
        int m = seconds / 60 - h * 60;
        int s = seconds - h * 3600 - m * 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
