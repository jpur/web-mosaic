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

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public int distance(int x, int y, int z) {
        int nx = Math.max(Math.min(x, maxX), minX);
        int ny = Math.max(Math.min(y, maxY), minY);
        int nz = Math.max(Math.min(z, maxZ), minZ);
        return Math.abs(x - nx) + Math.abs(y - ny) + Math.abs(z - nz);
    }

    public Bounds[] subdivide() {
        return new Bounds[] {
                getSubdivision(0, 0, 0), getSubdivision(1, 0, 0), getSubdivision(0, 0, 1), getSubdivision(1, 0, 1),
                getSubdivision(0, 1, 0), getSubdivision(1, 1, 0), getSubdivision(0, 1, 1), getSubdivision(1, 1, 1)
        };
    }

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