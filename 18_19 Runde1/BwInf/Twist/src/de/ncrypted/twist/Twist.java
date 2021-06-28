package de.ncrypted.twist;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ncrypted
 */
public class Twist {

    private static final Pattern PATTERN = Pattern.compile("([a-zA-ZäÄöÖüÜßẞ])([a-zA-ZäÄöÖüÜßẞ]*)([a-zA-ZäÄöÖüÜßẞ])");
    private static String text = "";

    public static void main(String[] args) {
        String textFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.println("1: Twist | 2: Untwist");
                String line = reader.readLine();
                if(line.equals("1")) {
                    break;
                } else if(line.equals("2")) {
                    Untwist.main(null);
                    return;
                }
            } catch (IOException e) {
            }
        }
        while (textFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: twist1.txt)");
            try {
                String line = reader.readLine();
                if (Twist.class.getClassLoader().getResource("examples/" + line) != null) {
                    textFileName = line;
                }
            } catch (IOException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        readText(textFileName);
        System.out.println("---UNTWISTED---");
        System.out.println(text);
        System.out.println("---TWISTED---");
        System.out.println(twist(text));
    }

    private static void readText(String fileName) {
        try {
            InputStream fileStream = Twist.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                text += line + "\n";
                line = reader.readLine();
            }
            text = text.substring(0, text.length() - 2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String twist(String input) {
        final StringBuilder builder = new StringBuilder();
        final Matcher matcher = PATTERN.matcher(input);

        // Filtere alle anderen Zeichen, die keine Buchstaben sind und speichere sie mit entsprechendem Index
        // Notiz: Die TreeMap sorgt dafür, dass die gefilterten Zeichen in der aufsteigenden Reihenfolge gespeichert werden
        Map<Integer, String> filtered = new TreeMap<>();
        Matcher matcherPunctuation = Pattern.compile("[^a-zA-ZäÄöÖüÜßẞ]").matcher(input);
        while (matcherPunctuation.find()) {
            filtered.put(matcherPunctuation.start(), matcherPunctuation.group());
        }

        // Filtere alle einzelnen Buchstaben und speichere sie mit entsprechendem Index
        Matcher matcherSingleLetters = Pattern.compile("(?<![a-zA-ZäÄöÖüÜßẞ])([a-zA-ZäÄöÖüÜßẞ])(?![a-zA-ZäÄöÖüÜßẞ])")
                .matcher(input);
        while (matcherSingleLetters.find()) {
            filtered.put(matcherSingleLetters.start(), matcherSingleLetters.group());
        }

        while (matcher.find()) {
            builder
                    // Behalte 1. Buchstaben des Wortes bei
                    .append(matcher.group(1))
                    // Mische Buchstaben in der Mitte des Wortes
                    .append(shuffleString(matcher.group(2)))
                    // Behalte letzten Buchstaben des Wortes bei
                    .append(matcher.group(3));
        }

        // Fuege die gefilterten Zeichen & einzelnen Buchstaben wieder zum Ergebnis hinzu
        for (Map.Entry<Integer, String> entry : filtered.entrySet()) {
            builder.insert(entry.getKey(), entry.getValue());
        }

        return builder.toString().trim();
    }

    private static String shuffleString(String input) {
        final List<String> letters = Arrays.asList(input.split(""));
        Collections.shuffle(letters);
        return String.join("", letters);
    }
}
