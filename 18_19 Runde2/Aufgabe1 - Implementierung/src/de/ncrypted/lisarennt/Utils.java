package de.ncrypted.lisarennt;

import java.util.*;

/**
 * @author ncrypted
 */
public class Utils {

    public static double round(double value, int afterComma) {
        double dividor = Math.pow(10, afterComma);
        return ((int) (value * dividor)) / dividor;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
