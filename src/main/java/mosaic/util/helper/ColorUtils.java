package mosaic.util.helper;

import java.awt.*;

public final class ColorUtils {
    private ColorUtils() {}

    /**
     * Computes an average color from an array of colors
     * @param colors The array of colors to compute an average of
     * @return The average color of the array
     */
    public static Color getAverageColor(int[] colors) {
        int r = 0, g = 0, b = 0;
        for (int i : colors) {
            Color col = new Color(i);
            r += col.getRed();
            g += col.getGreen();
            b += col.getBlue();
        }

        return new Color(r / colors.length, g / colors.length, b / colors.length);
    }

    /**
     * Computes the distance of two colors
     * @param a The first color to compare
     * @param b The second color to compare
     * @return The distance between the two colors
     */
    public static int getDistance(Color a, Color b) {
        return HelperUtils.distance(a.getRed(), a.getGreen(), a.getBlue(), b.getRed(), b.getGreen(), b.getBlue());
    }
}
