package mosaic.util;

public final class HelperUtils {
    private HelperUtils() {}

    public static int distance(int ax, int ay, int az, int bx, int by, int bz) {
        return Math.abs(ax - bx) + Math.abs(ay - by) + Math.abs(az - bz);
    }
}
