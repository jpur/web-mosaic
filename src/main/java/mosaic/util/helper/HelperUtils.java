package mosaic.util.helper;

import java.util.List;
import java.util.Random;

public final class HelperUtils {
    private HelperUtils() {}

    private static Random rand = new Random();

    /**
     * Manhattan distance function
     */
    public static int distance(int ax, int ay, int az, int bx, int by, int bz) {
        return Math.abs(ax - bx) + Math.abs(ay - by) + Math.abs(az - bz);
    }

    /**
     * Returns a random item from the given list
     */
    public static <T> T getRandom(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(rand.nextInt(list.size())) : null;
    }
}
