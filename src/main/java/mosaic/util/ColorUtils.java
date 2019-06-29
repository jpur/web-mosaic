package mosaic.util;

import java.awt.*;

public final class ColorUtils {
    private ColorUtils() {}

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

    public static int getDistance(Color a, Color b) {
        return Math.abs(a.getRed() - b.getRed()) + Math.abs(a.getGreen() - b.getGreen()) + Math.abs(a.getBlue() - b.getBlue());
    }
}
