package de.ncrypted.abbiegen;

import de.ncrypted.abbiegen.geometry.PointInt;
import javafx.util.Pair;
import org.apache.batik.svggen.SVGGraphics2D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ncrypted
 */
public class Abbiegen {

    // General
    public static double extension = 100;
    public static int startNode;
    public static int endNode;
    public static List<PointInt> nodes = new ArrayList<>();
    private static Map<String, Integer> coordsToNodeIdx = new HashMap<>();
    // node id -> node id, length
    public static List<List<Pair<Integer, Double>>> streets = new ArrayList<>();
    // Dijkstra
    public static List<Integer> shortestPath;
    public static double shortestPathLen;
    public static double maxPathLen;
    // DFS
    public static int minTurns = Integer.MAX_VALUE;
    public static double minTurnDist = Double.MAX_VALUE;
    public static ArrayList<Integer> turnPath;

    public static void main(String[] args) {
        String coordsFileName = "abbiegen3.txt";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (coordsFileName == null) {
            System.out.println("Namen der Beispieldatei (Bsp.: abbiegen1.txt):");
            try {
                String line = reader.readLine();
                if (Abbiegen.class.getClassLoader().getResource("examples/" + line) != null) {
                    coordsFileName = line;
                }
            } catch (IOException e) {
            }
        }
        while (extension == -1) {
            System.out.println("Maximale Verlängerung (%):");
            try {
                String line = reader.readLine();
                double lineDouble = Double.parseDouble(line);
                if (lineDouble >= 0) {
                    extension = lineDouble;
                }
            } catch (IOException e) {
            } catch (NumberFormatException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        System.out.println("---INFOS---");
        readFileData(coordsFileName);
        System.out.println("Kanten:");
        for (int u = 0; u < nodes.size(); u++) {
            for (Pair<Integer, Double> street : streets.get(u)) {
                int v = street.getKey();
                double w = street.getValue();
                System.out.println(nodes.get(u) + "-" + nodes.get(v) + " " + w);
            }
        }
        System.out.println("Maximale Verlängerung: " + extension + "%");
        long pos = 1;
        for (int i = 0; i < streets.size(); i++) {
            if (streets.get(i).size() <= 2) {
                continue;
            }
            pos *= streets.get(i).size() - 1;
        }
        // execute algorithm
        System.out.println("---DIJKSTRA---");
        executeAlgorithm();
        System.out.println("Länge: " + round(shortestPathLen, +4));
        System.out.println("Maximale Weglänge: " + round(maxPathLen, 4) + " (+" + extension + "%)");
        String pathStr = "";
        for (int i : shortestPath) {
            pathStr += i + " ";
        }
        System.out.println("Pfad: " + pathStr);
        System.out.println("---TIEFENSUCHE---");
        pathStr = "";
        for (int i : turnPath) {
            pathStr += i + " ";
        }
        double overPercentage = round(minTurnDist * 100 / shortestPathLen - 100, 2);
        System.out.println("Abbiegevorgänge: " + minTurns);
        System.out.println("Weglänge: " + round(minTurnDist, 4) + " (+" + overPercentage + "%)");
        System.out.println("Pfad: " + pathStr);
        // Generate graphic
        SVGGenerator.maxX*=100;
        SVGGenerator.maxY*=100;
        SVGGenerator.generateSVG(coordsFileName.substring(0, coordsFileName.length() - 4) + ".svg");
    }

    private static void executeAlgorithm() {
        // Dijkstra
        Pair<Double, List<Integer>> shortestPathPair = Dijkstra.dijkstra(nodes, streets, 0, 1);
        shortestPathLen = shortestPathPair.getKey();
        shortestPath = shortestPathPair.getValue();
        maxPathLen = shortestPathLen * (100 + extension) / 100;
        // DFS
        DFS.setup(streets);
        DFS.dfs(streets, 0, 1, 0, 0, -1);
    }

    private static void readFileData(String fileName) {
        try {
            InputStream fileStream = Abbiegen.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int current = 0;
            while (line != null) {
                // second line - starting point
                if (current == 1) {
                    startNode = getNode(line);
                    // third line - end point
                } else if (current == 2) {
                    endNode = getNode(line);
                    // streets
                } else if (current > 2) {
                    int u = getNode(line.split(" ")[0]);
                    int v = getNode(line.split(" ")[1]);
                    while (nodes.size() != streets.size()) {
                        streets.add(new ArrayList<>());
                    }
                    int xDif = Math.abs(nodes.get(u).getX() - nodes.get(v).getX());
                    int yDif = Math.abs(nodes.get(u).getY() - nodes.get(v).getY());
                    double len = Math.sqrt(Math.pow(xDif, 2.0D) + Math.pow(yDif, 2.0D));
                    streets.get(u).add(new Pair<>(v, len));
                    streets.get(v).add(new Pair<>(u, len));
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

    private static int getNode(String coords) {
        if (coordsToNodeIdx.get(coords) != null) {
            return coordsToNodeIdx.get(coords);
        }
        String[] commaSplit = coords.split(",");
        int x = asInt(commaSplit[0].substring(1), 0);
        int y = asInt(commaSplit[1].substring(0, commaSplit[1].length() - 1), 0);
        SVGGenerator.maxX = Math.max(SVGGenerator.maxX, x);
        SVGGenerator.maxY = Math.max(SVGGenerator.maxY, y);
        nodes.add(new PointInt(x, y));
        coordsToNodeIdx.put(coords, nodes.size() - 1);
        return nodes.size() - 1;
    }

    private static Integer asInt(String str, int index) {
        return Integer.parseInt(str.split(" ")[index]);
    }

    private static double round(double d, int decimals) {
        return ((int) (d * Math.pow(10, decimals))) / Math.pow(10, decimals);
    }
}
