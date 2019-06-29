package mosaic.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorOctree extends Octree<Color> {
    private static final class ColorOctreeNode {
        public final boolean isLeaf;
        public final Bounds bounds;
        public final List<Color> items;
        public final ColorOctreeNode[] children;

        public ColorOctreeNode(List<Color> items, Bounds bounds) {
            this.isLeaf = true;
            this.bounds = bounds;
            this.items = items;
            children = null;
        }

        public ColorOctreeNode(ColorOctreeNode[] children, Bounds bounds) {
            this.isLeaf = false;
            this.bounds = bounds;
            this.children = children;
            items = null;
        }
    }

    private final int maxDepth = 10;

    public ColorOctree(List<Color> points) {
        super(points);
    }

    @Override
    protected void build(List<Color> points) {
        // Find bounds encompassing all points
        Bounds bounds = new Bounds();
        for (Color p : points) {
            bounds.encapsulate(p.getRed(), p.getGreen(), p.getBlue());
        }

        build(points, bounds, 0);
    }

    private ColorOctreeNode build(List<Color> points, Bounds bounds, int depth) {
        // Return leaf node is number of points less than threshold
        if (points.size() < 10 || depth > maxDepth) {
            return new ColorOctreeNode(points, bounds);
        }

        // Subdivide bounds
        Bounds[] octants = bounds.subdivide();
        List<List<Color>> childrenPoints = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            childrenPoints.add(new ArrayList<>());
        }

        // Add points to subdivisions
        for (Color point : points) {
            for (int i = 0; i < octants.length; i++) {
                if (octants[i].contains(point.getRed(), point.getGreen(), point.getBlue())) {
                    childrenPoints.get(i).add(point);
                    break;
                }
            }
        }

        ColorOctreeNode[] children = new ColorOctreeNode[octants.length];
        for (int i = 0; i < children.length; i++) {
            children[i] = build(childrenPoints.get(i), octants[i], depth + 1);
        }

        return new ColorOctreeNode(children, bounds);
    }
}
