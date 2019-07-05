package mosaic.util;

import mosaic.util.helper.HelperUtils;

public class Vector3i {
    private int x, y, z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static <T> int distance(Vector3i a, Vector3i b) {
        return HelperUtils.distance(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
