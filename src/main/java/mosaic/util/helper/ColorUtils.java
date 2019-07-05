package mosaic.util.helper;

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
        return HelperUtils.distance(a.getRed(), a.getGreen(), a.getBlue(), b.getRed(), b.getGreen(), b.getBlue());
    }
}
