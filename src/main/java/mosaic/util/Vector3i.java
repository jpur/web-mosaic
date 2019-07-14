package mosaic.util;

import mosaic.util.helper.HelperUtils;

/**
 * Defines a point in 3D space
 */
public class Vector3i {
    private int x, y, z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public static int distance(Vector3i a, Vector3i b) {
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

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!Vector3i.class.isAssignableFrom(other.getClass())) return false;

        final Vector3i vec = (Vector3i)other;
        return vec.getX() == x && vec.getY() == y && vec.getZ() == z;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d)", x, y, z);
    }

    @Override
    public int hashCode() {
        int hash = 23;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        return hash;
    }
}
