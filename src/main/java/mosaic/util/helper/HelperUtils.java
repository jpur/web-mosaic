package mosaic.util.helper;

import java.util.List;
import java.util.Random;

public final class HelperUtils {
    private HelperUtils() {}

    private static Random rand = new Random();

    /**
     * Returns the manhattan distance between two 3D vectors
     * @param ax The x position of the first vector
     * @param ay The y position of the first vector
     * @param az The z position of the first vector
     * @param bx The x position of the second vector
     * @param by The y position of the second vector
     * @param bz The z position of the second vector
     * @return The distance between both vectors
     */
    public static int distance(int ax, int ay, int az, int bx, int by, int bz) {
        return Math.abs(ax - bx) + Math.abs(ay - by) + Math.abs(az - bz);
    }


    /**
     * Returns a random item from the given list
     * @param list The list to return a random item from
     * @return A random item from the given list
     */
    public static <T> T getRandom(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(rand.nextInt(list.size())) : null;
    }

    /**
     * Constrains a value between a range
     * @param value The value to constrain
     * @param min The lower bound of the range
     * @param max The upper bound of the range (inclusive)
     * @return The value constrained between the given min and max range
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
