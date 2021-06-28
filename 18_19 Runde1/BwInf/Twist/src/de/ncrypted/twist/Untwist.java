package de.ncrypted.twist;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ncrypted
 */
public class Untwist {

    private static Set<String> wordlist = new HashSet<>();
    private static List<String> text = new ArrayList<>();

    public static void main(String[] args) {
        String textFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (textFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: twist1.txt)");
            try {
                String line = reader.readLine();
                if (Untwist.class.getClassLoader().getResource("examples/" + line) != null) {
                    textFileName = line;
                }
            } catch (IOException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        // Lese Wortliste und getwisteten Text ein
        readWordList();
        readText(textFileName);
        System.out.println("---TWISTED---");
        for (String line : text) {
            System.out.println(line);
        }
        untwist();
        System.out.println("---UNTWISTED---");
        for (String line : text) {
            System.out.println(line);
        }
    }

    private static void untwist() {
        // Iteriere ueber jede Zeile des Textes
        for (int i = 0; i < text.size(); i++) {
            String line = text.get(i);
            String newLine = "";
            // Iteriere ueber jedes Wort der momentanen Zeile
            for (String lineWord : line.split(" ")) {
                // Filtere alle anderen Zeichen, die keine Buchstaben sind und speichere sie mit entsprechendem Index
                LinkedHashMap<Integer, String> punctuation = new LinkedHashMap<>();
                String[] characters = lineWord.split("");
                for (int j = 0; j < characters.length; j++) {
                    String character = characters[j];
                    if (character.matches("[^a-zA-ZäÄöÖüÜß]")) {
                        punctuation.put(j, character);
                    }
                }
                // Entferne alle anderen Zeichen, die keine Buchstaben sind, des momentanen Wortes
                lineWord = lineWord.replaceAll("[^a-zA-ZäÄöÖüÜß]", "");

                // Filtere Wortliste nach Möglichkeiten, entsprechend zur Laenge, Anfangs- & Endbuchstaben des Ausgangswortes
                Set<String> possibles = new HashSet<>();
                for (String word : wordlist) {
                    if (word.length() > 3
                            && word.length() == lineWord.length()
                            && word.substring(0, 1).equalsIgnoreCase(lineWord.substring(0, 1))
                            && word.substring(word.length() - 1)
                            .equalsIgnoreCase(lineWord.substring(lineWord.length() - 1))) {
                        possibles.add(word);
                    }
                }

                // Pruefe, ob ein Wort aus dem Woerterbuch die exakt selben Buchstaben wie das Ausgangswort hat
                String chosen = null;
                for (String possible : possibles) {
                    // Notiz: Die Sortier-Methode behandelt jeden Buchstaben in Kleinschreibung
                    if (sortAlphabetically(possible).equalsIgnoreCase(sortAlphabetically(lineWord))) {
                        chosen = possible;
                    }
                }
                // Falls kein Wort gefunden wurde, wird das Original-Wort wieder eingesetzt
                if (chosen == null) {
                    chosen = lineWord;
                }

                // Fuege die gefilterten Zeichen wieder zum Ergebnis hinzu
                StringBuilder builder = new StringBuilder(chosen);
                for (Map.Entry<Integer, String> entry : punctuation.entrySet()) {
                    builder.insert(entry.getKey(), entry.getValue());
                }
                chosen = builder.toString();
                // Fuege das Ergebnis zur neuen Zeile hinzu
                newLine += chosen + " ";
            }
            // Entferne ueberstehendes Leerzeichen am Ende der neuen Zeile
            newLine = newLine.substring(0, newLine.length() - 1);
            // Aktualisiere die Originalzeile
            text.set(i, newLine);
        }
    }

    private static void readWordList() {
        try {
            InputStream fileStream = Untwist.class.getClassLoader().getResourceAsStream("woerterliste.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                wordlist.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readText(String fileName) {
        try {
            InputStream fileStream = Untwist.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                text.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sortAlphabetically(String string) {
        return string.toLowerCase().chars().sorted()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
