package de.ncrypted.dreiecke;

/**
 * @author ncrypted
 */
public class Utils {

    public static double round(double value, int afterComma) {
        double dividor = Math.pow(10, afterComma);
        return ((int) (value * dividor)) / dividor;
    }
}
