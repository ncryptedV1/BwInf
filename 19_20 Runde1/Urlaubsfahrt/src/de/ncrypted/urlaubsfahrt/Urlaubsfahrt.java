package de.ncrypted.urlaubsfahrt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ncrypted
 */
public class Urlaubsfahrt {

    // consumption in l per 100 km - double to max later calculations easier
    private static double consumption;
    // tank size in l
    private static int tankSize;
    // initial tank level in l
    private static int tankLevel;
    // length of the way
    private static int wayLength;
    // amount of gas stations
    private static int stations;
    // gas station distances in km
    private static List<Integer> dists = new ArrayList<>();
    // gas station costs in cents/l (between 100 and 199)
    private static List<Integer> prices = new ArrayList<>();

    private static ArrayList<ArrayList<Double>> graph;
    private static ArrayList<ArrayList<Integer>> parents;

    private static double minPrice = Integer.MAX_VALUE;
    private static List<Integer> minPath;

    public static void main(String[] args) {
        String fileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (fileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: fahrt1.txt)");
            try {
                String line = reader.readLine();
                if (Urlaubsfahrt.class.getClassLoader().getResource("examples/" + line) != null) {
                    fileName = line;
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
        readFileData(fileName);
        System.out.printf("Verbrauch: %.2f l/100 km\n", consumption);
        System.out.println("Tankfüllung: " + tankLevel + "" + "/" + tankSize + " l");
        System.out.println("Streckenlänge: " + wayLength + " km");
        System.out.println("Tankstellen-Anzahl: " + (stations - 2));
        for (int i = 1; i < dists.size() - 1; i++) {
            System.out.println(dists.get(i) + " km - " + prices.get(i) + " cts/l");
        }
        // execute algorithm
        System.out.println("---ALGORITHMUS---");
        executeAlgorithm();
    }

    private static void calcPathPrice(int curNode, double curPrice, List<Integer> path) {
        path.add(curNode);
        if (curNode == 0) {
            if (curPrice < minPrice) {
                minPrice = curPrice;
                minPath = path;
            }
            return;
        }
        for (int parent : parents.get(curNode)) {
            calcPathPrice(parent, curPrice + graph.get(parent).get(curNode), new ArrayList<>(path));
        }
    }

    private static void executeAlgorithm() {
        double initialReach = tankLevel / consumption * 100;
        double maxReach = tankSize / consumption * 100;
        System.out.printf("Reichweite (Start): %.2f km\n", initialReach);
        System.out.printf("Reichweite (Voll): %.2f km\n", maxReach);

        graph = new ArrayList<>(stations);
        ArrayList<Integer> minDepth = new ArrayList<>(stations);
        parents = new ArrayList<>(stations);
        for (int i = 0; i < stations; i++) {
            graph.add(new ArrayList<>());
            for (int j = 0; j < stations; j++) {
                double price = (i == j ? 0 : -1);
                graph.get(i).add(price);
            }
            minDepth.add(Integer.MAX_VALUE);
            parents.add(new ArrayList<>());
        }
        minDepth.set(0, 0);

        for (int i = 0; i < stations; i++) {
            int curDepth = minDepth.get(i);
            int curDist = dists.get(i);
            double curReach = (i == 0 ? initialReach : maxReach);
            for (int j = i + 1; j < stations; j++) {
                int posDist = dists.get(j);
                int z = 1;
                if (posDist > curDist + curReach) {
                    break;
                }
                if (curDepth + 1 < minDepth.get(j)) {
                    minDepth.set(j, curDepth + 1);
                    parents.get(j).clear();
                    parents.get(j).add(i);
                } else if (curDepth + 1 == minDepth.get(j)) {
                    parents.get(j).add(i);
                } else {
                    // edge won`t be needed later on -> no calculations required
                    continue;
                }
                double neededFuel = ((posDist - curDist) / 100D) * consumption;
                double price = neededFuel * prices.get(i);
                graph.get(i).set(j, price);
            }
        }

        calcPathPrice(stations - 1, 0, new ArrayList<>(stations - 1));
        if(minPath == null) {
            System.out.println("Keine Reise möglich!");
            return;
        }
        minPrice = 0;
        System.out.printf("Tankvorgänge: %d\n", minPath.size() - 2);
        Collections.reverse(minPath);
        double leftFuel = tankLevel - (dists.get(minPath.get(1)) / 100D) * consumption;
        for (int i = 1; i < minPath.size() - 1; i++) {
            int node = minPath.get(i);
            double price = graph.get(node).get(minPath.get(i + 1));
            double fuel = price / prices.get(node);
            if (leftFuel > fuel) {
                fuel = 0;
                price = 0;
                leftFuel -= fuel;
            } else if(leftFuel != 0) {
                fuel -= leftFuel;
                price = fuel * prices.get(node);
                leftFuel = 0;
            }
            System.out.printf(dists.get(node) + ": %.2f l" + " -> %.2f €\n", fuel, price / 100);
            minPrice += price;
        }
        System.out.printf("Preis: %.2f €\n", minPrice / 100);
    }

    private static void readFileData(String fileName) {
        dists.add(0);
        prices.add(0);
        try {
            InputStream fileStream = Urlaubsfahrt.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int current = 0;
            while (line != null) {
                if (current == 0) {
                    consumption = asInt(line);
                } else if (current == 1) {
                    tankSize = asInt(line);
                } else if (current == 2) {
                    tankLevel = asInt(line);
                } else if (current == 3) {
                    wayLength = asInt(line);
                } else if (current == 4) {
                    stations = asInt(line);
                } else {
                    line = line.replaceAll("\\s+", " ");
                    dists.add(asInt(line, 0));
                    prices.add(asInt(line, 1));
                }
                current++;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dists.add(wayLength);
        prices.add(0);
        stations += 2;
    }

    private static Integer asInt(String str, int idx) {
        return asInt(str.split(" ")[idx]);
    }

    private static Integer asInt(String str) {
        return Integer.parseInt(str);
    }
}
