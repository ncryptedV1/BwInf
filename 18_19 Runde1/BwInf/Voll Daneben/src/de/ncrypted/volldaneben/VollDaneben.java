package de.ncrypted.volldaneben;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ncrypted
 */
public class VollDaneben {

    private static List<Integer> luckNumbers = new ArrayList<>();
    private static List<Integer> alNumbers = new ArrayList<>();

    public static void main(String[] args) {
        String numbersFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (numbersFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: beispiel1.txt)");
            try {
                String line = reader.readLine();
                if (VollDaneben.class.getClassLoader().getResource("examples/" + line) != null) {
                    numbersFileName = line;
                }
            } catch (IOException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        readLuckNumbers(numbersFileName);
        getAlNumbers();
        System.out.println("---ERGEBNIS---");
        int deposit = luckNumbers.size() * 25;
        int payout = 0;
        for (Integer luckNumber : luckNumbers) {
            payout += getPayout(luckNumber);
        }
        int saldo = deposit - payout;
        System.out.println("Glückszahlen: " + luckNumbers.size());
        System.out.println("Einzahlung: " + deposit + "$");
        System.out.println("Al's Zahlen:");
        alNumbers.forEach(number -> System.out.println(number));
        System.out.println("Auszahlung: " + payout + "$");
        System.out.println("Saldo: " + saldo + "$");
        System.out.println("Al Capone: " + (saldo > 0 ? "Gotcha!" : saldo == 0 ? "Grmpf..." : "RAGE!"));
    }

    private static void getAlNumbers() {
        alNumbers.clear();
        List<List<Integer>> luckNumberGroups = new ArrayList<>();

        // Erstelle die Glueckszahlen-Gruppen
        // 0: 1-100 | 1: 101-200 | 2: 201-300 ...
        for (int i = 0; i < 10; i++) {
            luckNumberGroups.add(i, new ArrayList<>());
            for (Integer luckNumber : luckNumbers) {
                if (luckNumber > i * 100 && luckNumber <= i * 100 + 100) {
                    luckNumberGroups.get(i).add(luckNumber);
                }
            }
        }

        // Ermittle Wichtung je Bereich (100 Zahlen), aufgrund vieler Glueckszahlen
        // 0: 1-100 | 1: 101-200 | 2: 201-300 ... || Konzentration verglichen zum Durchschnitt aller Zahlen
        Map<Integer, Float> concentration = new HashMap<>();
        int total = luckNumbers.size();
        float groupSize = (float) total / 10;
        for (int i = 0; i < 10; i++) {
            int count = luckNumberGroups.get(i).size();
            concentration.put(i, count / groupSize);
        }

        // Bilde den Durchschnitt der Glueckszahl-Gruppen,
        // verschiebe Sie in Richtung höher konzentrierterer benachbarter Bereiche,
        // und setze sie als Al's Zahl
        for (int i = 0; i < luckNumberGroups.size(); i++) {
            List<Integer> luckNumberGroup = luckNumberGroups.get(i);
            BigInteger sum = BigInteger.ZERO;
            for (Integer luckNumber : luckNumberGroup) {
                sum = sum.add(BigInteger.valueOf(luckNumber));
            }
            int ratio = Integer.parseInt(sum.divide(BigInteger.valueOf(luckNumberGroup.size())).toString());

            // Ermittle "Gravitation"
            float conc = concentration.get(i);
            float gravPos = 0;
            if (i != luckNumberGroups.size() - 1) {
                gravPos = concentration.get(i + 1) - conc;
            }
            gravPos = gravPos < 0 ? 0 : gravPos;
            float gravNeg = 0;
            if (i != 0) {
                gravNeg = concentration.get(i - 1) - conc;
            }
            gravNeg = gravNeg < 0 ? 0 : gravNeg;
            float grav = gravPos - gravNeg;
            int gravSteps = (int) Math.floor(luckNumberGroup.size() / 2F * grav);
            ratio += gravSteps;

            alNumbers.add(ratio);
        }
    }

    private static void readLuckNumbers(String fileName) {
        try {
            InputStream fileStream = VollDaneben.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                try {
                    luckNumbers.add(Integer.parseInt(line));
                } catch (NumberFormatException ex) {
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getPayout(int luckNumber) {
        int distance = -1;
        for (Integer alNumber : alNumbers) {
            int currentDistance = Math.abs(alNumber - luckNumber);
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
            }
        }
        return distance;
    }
}
