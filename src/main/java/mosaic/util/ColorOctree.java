package mosaic.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ColorOctree<T> extends Octree<Color, List<T>> {
    private static final class ColorOctreeNode<T> {
        public final boolean isLeaf;
        public final Bounds bounds;
        public final List<ColorOctreeNode<T>> children;
        public final ColorCollection<T> value;

        public ColorOctreeNode(Bounds bounds, ColorCollection<T> value) {
            this.isLeaf = true;
            this.bounds = bounds;
            this.value = value;
            children = null;
        }

        public ColorOctreeNode(Bounds bounds, List<ColorOctreeNode<T>> children) {
            this.isLeaf = false;
            this.bounds = bounds;
            this.children = children;
            value = null;
        }
    }

    private final int maxDepth = 10;
    private final ColorOctreeNode<T> root;

    public ColorOctree(List<ColorCollection<T>> points) {
        super(points);
        root = build(points);
    }

    @Override
    public List<ColorCollection<T>> nearestNeighbor(Color color, int k) {
        List<ColorCollection<T>> neighbors = new ArrayList<>();

        PriorityQueue<ColorOctreeNode<T>> pq = new PriorityQueue<>(Comparator.comparingInt(a -> distance(color, a)));
        pq.add(root);
        while (!pq.isEmpty() && neighbors.size() < k) {
            ColorOctreeNode<T> node = pq.poll();
            if (node.isLeaf) {
                neighbors.add(node.value);
            } else {
                pq.addAll(node.children);
            }
        }

        return neighbors;
    }

    /**
     * Computes the distance between a color and node
     * @param source A color
     * @param node An octree node
     * @return The distance between the color and octree
     */
    private int distance(Color source, ColorOctreeNode<T> node) {
        // Return color-distance if node is a leaf
        if (node.isLeaf) {
            return ColorUtils.getDistance(source, node.value.getKey());
        }

        // Return bounds-distance if node is not a leaf
        return node.bounds.distance(source.getRed(), source.getGreen(), source.getBlue());
    }

    /**
     * Builds an octree with the given points
     * @param points The points the octree will contain
     * @return The root node of the octree
     */
    private ColorOctreeNode<T> build(List<ColorCollection<T>> points) {
        // Find bounds encompassing all points
        Bounds bounds = new Bounds();
        for (ColorCollection<T> p : points) {
            Color color = p.getKey();
            bounds.encapsulate(color.getRed(), color.getGreen(), color.getBlue());
        }

        return build(points, bounds, 0);
    }

    /**
     * Recursive method for building an octree with the given points
     * @param points The points the octree will contain
     * @param bounds The bounds of the octree that encompass the points
     * @param depth The current depth of the octree
     * @return The root node of the subtree
     */
    private ColorOctreeNode<T> build(List<ColorCollection<T>> points, Bounds bounds, int depth) {
        // Return leaf node is number of points less than threshold
        if (points.size() < 10 || depth > maxDepth) {
            List<ColorOctreeNode<T>> children = new ArrayList<>();
            for (ColorCollection<T> p : points) {
                children.add(new ColorOctreeNode<>(bounds, p));
            }

            return new ColorOctreeNode<>(bounds, children);
        }

        // Subdivide bounds
        Bounds[] octants = bounds.subdivide();
        List<List<ColorCollection<T>>> childrenPoints = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            childrenPoints.add(new ArrayList<>());
        }

        // Add points to subdivisions
        for (ColorCollection<T> point : points) {
            Color color = point.getKey();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i].contains(color.getRed(), color.getGreen(), color.getBlue())) {
                    childrenPoints.get(i).add(point);
                    break;
                }
            }
        }

        List<ColorOctreeNode<T>> children = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            children.add(build(childrenPoints.get(i), octants[i], depth + 1));
        }

        return new ColorOctreeNode<>(bounds, children);
    }
}
