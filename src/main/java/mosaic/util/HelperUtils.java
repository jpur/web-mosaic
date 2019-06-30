package mosaic.util;

import java.util.List;
import java.util.Random;

public final class HelperUtils {
    private HelperUtils() {}

    private static Random rand = new Random();

    public static int distance(int ax, int ay, int az, int bx, int by, int bz) {
        return Math.abs(ax - bx) + Math.abs(ay - by) + Math.abs(az - bz);
    }

    public static <T> int distance(Vector3i a, Vector3i b) {
        return distance(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }

    public static <T> T getRandom(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(rand.nextInt(list.size())) : null;
    }
}
