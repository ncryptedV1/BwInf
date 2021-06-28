package de.ncrypted.merker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ncrypted
 */
public class Merker {

    private static String number;
    private static String numberSplit;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (number == null) {
            System.out.println("Bitte geben Sie die Nummer ein:");
            try {
                String line = reader.readLine();
                new BigInteger(line);
                number = line;
            } catch (IOException e) {
            } catch (NumberFormatException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }


        executeAlgorithm();

        System.out.println("---Ergebnis---");
        System.out.println(numberSplit);
    }

    private static void executeAlgorithm() {
        int idx = number.length();
        List<Integer> splitPoints = new ArrayList<>();
        while (idx > 4) {
            for (int i = 4; i >= 2; i--) {
                int splitIdx = idx - i;
                if (number.charAt(splitIdx) == '0') {
                    if (i != 2) {
                        continue;
                    }
                    splitIdx -= 2;
                }
                if(splitIdx < 2) {
                    splitIdx = 2;
                }
                splitPoints.add(splitIdx);
                idx = splitIdx;
                break;
            }
        }
        StringBuilder builder = new StringBuilder(number);
        for (int i = 0; i < splitPoints.size(); i++) {
            builder.insert(splitPoints.get(i), "-");
        }
        numberSplit = builder.toString();
    }
}
