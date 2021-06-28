package de.ncrypted.rominos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ncrypted
 */
public class Rominos {

    private static int n = -1;
    private static List<Romino> combos = new ArrayList<>();

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (n == -1) {
            System.out.println("Bitte geben Sie n ein:");
            try {
                String line = reader.readLine();
                int temp = Integer.parseInt(line);
                if (temp >= 2) {
                    n = temp;
                }
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
        System.out.println("Kombinationen: " + combos.size());
        for (int i = 0; i < combos.size(); i++) {
            Romino combo = combos.get(i);
            combo.normalize();
            System.out.println((i + 1) + ":");
            System.out.println(combo);
        }
    }

    private static void executeAlgorithm() {
        List<Set<Integer>> blocked = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            blocked.add(new HashSet<>());
        }
        for (int i = 2; i <= n; i++) {
            // dynamical programming approach - start case n=2 -> diagonal
            if (i == 2) {
                combos.add(new Romino().add(0, 0).add(1, 1));
                // block square that`d destroy the diagonal
                blocked.get(0).add(1);
                blocked.get(1).add(0);
                continue;
            }
            List<Romino> newCombos = new ArrayList<>();
            for (int j = 0; j < combos.size(); j++) {
                Romino romino = combos.get(j);
                for (int columnShift = -1; columnShift <= 1; columnShift++) {
                    int column = romino.lastColumn + columnShift;
                    for (int rowShift = -1; rowShift <= 1; rowShift++) {
                        int row = romino.lastRow + rowShift;
                        if(row < 0 || row > n-1
                                // prevent mirrored duplicate rominos
                                || (column > row && !romino.getMatrix().get(column).contains(row))
                                || blocked.get(row).contains(column)
                                || romino.getMatrix().get(row).contains(column)) {
                            continue;
                        }
                        newCombos.add(romino.copy().add(row, column));
                    }
                }
                blocked.get(romino.lastRow).add(romino.lastColumn);
            }
            combos = newCombos;
        }

    }

    private static class Romino {
        private List<Set<Integer>> pos;
        public int lastRow;
        public int lastColumn;

        public Romino() {
            pos = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                pos.add(new HashSet<>());
            }
        }

        public Romino copy() {
            Romino romino = new Romino();
            for (int i = 0; i < pos.size(); i++) {
                Set<Integer> row = pos.get(i);
                for (Integer x : row) {
                    romino.pos.get(i).add(x);
                }
            }
            romino.lastColumn = lastColumn;
            romino.lastRow = lastRow;
            return romino;
        }

        public Romino add(int row, int column) {
            pos.get(row).add(column);
            lastRow = row;
            lastColumn = column;
            return this;
        }

        public List<Set<Integer>> getMatrix() {
            return pos;
        }

        public void normalize() {
            int min = 0;
            for (Set<Integer> row : pos) {
                for (Integer column : row) {
                    min = Math.min(min, column);
                }
            }
            for (int j = 0; j < pos.size(); j++) {
                Set<Integer> row = pos.get(j);
                Set<Integer> shifted = new HashSet<>(row.size());
                for (Integer x : row) {
                    shifted.add(x - min);
                }
                pos.set(j, shifted);
            }
        }

        public String toString() {
            String str = "";
            for (int i = 0; i < n; i++) {
                Set<Integer> row = pos.get(i);
                for (int j = 0; j < n; j++) {
                    if (row.contains(j)) {
                        str += "■";
                    } else {
                        str += "□";
                    }
                }
                str += "\n";
            }
            return str;
        }
    }
}
