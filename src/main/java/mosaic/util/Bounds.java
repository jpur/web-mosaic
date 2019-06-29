package mosaic.util;

public final class Bounds {
    private int minX, minY, minZ, maxX, maxY, maxZ;

    public Bounds() {
        this(0, 0, 0, 0, 0, 0);
    }

    public Bounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Returns true if the bounds encapsulates the given point
     * @param x The x position of the point
     * @param y The y position of the point
     * @param z The z position of the ponit
     * @return True if the bounds encapsulates the given point
     */
    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * Returns the minimal distance between a given point and the bounds
     * @param x The x position of the point
     * @param y The y position of the point
     * @param z The z position of the point
     * @return The minimal distance to the point from the bounds
     */
    public int distance(int x, int y, int z) {
        int nx = Math.max(Math.min(x, maxX), minX);
        int ny = Math.max(Math.min(y, maxY), minY);
        int nz = Math.max(Math.min(z, maxZ), minZ);
        return HelperUtils.distance(x, y, z, nx, ny, nz);
    }

    /**
     * Subdivides the bounds into 8 octants
     * @return The subdivisions of the bounds
     */
    public Bounds[] subdivide() {
        return new Bounds[] {
                getSubdivision(0, 0, 0), getSubdivision(1, 0, 0), getSubdivision(0, 0, 1), getSubdivision(1, 0, 1),
                getSubdivision(0, 1, 0), getSubdivision(1, 1, 0), getSubdivision(0, 1, 1), getSubdivision(1, 1, 1)
        };
    }

    /**
     * Expands the bounds to include the given point
     * @param x The x position of the point
     * @param y The y position of the point
     * @param z The z position of the point
     */
    public void encapsulate(int x, int y, int z) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        minZ = Math.min(minZ, z);
        maxX = Math.max(x, maxX);
        maxY = Math.max(y, maxY);
        maxZ = Math.max(z, maxZ);
    }

    @Override
    public String toString() {
        return String.format("Bounds{min(%d, %d, %d), max(%d, %d, %d)}", minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Helper method for computing the x,y,z subdivision
     * @param x Ranges between 0-1 (inclusive)
     * @param y Ranges between 0-1 (inclusive)
     * @param z Ranges between 0-1 (inclusive)
     * @return The subdivision at position x,y,z
     */
    private Bounds getSubdivision(int x, int y, int z) {
        final int xStep = (maxX - minX + 1) / 2;
        final int yStep = (maxY - minY + 1) / 2;
        final int zStep = (maxZ - minZ + 1) / 2;
        final int newMinX = minX + xStep * x;
        final int newMinY = minY + yStep * y;
        final int newMinZ = minZ + zStep * z;
        return new Bounds(newMinX, newMinY, newMinZ, newMinX + xStep, newMinY + yStep, newMinZ + zStep);
    }
}