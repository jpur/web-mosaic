package mosaic.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.AbstractMap.SimpleEntry;

public class VectorOctree<K extends Vector3i, T> implements Octree<K, T> {
    private static final class OctreeNode<K, T> {
        public final boolean isLeaf;
        public final Bounds bounds;
        public final List<OctreeNode<K, T>> children;
        public final SimpleEntry<K, T> value;

        public OctreeNode(Bounds bounds, SimpleEntry<K, T> value) {
            this.isLeaf = true;
            this.bounds = bounds;
            this.value = value;
            children = null;
        }

        public OctreeNode(Bounds bounds, List<OctreeNode<K, T>> children) {
            this.isLeaf = false;
            this.bounds = bounds;
            this.children = children;
            value = null;
        }
    }

    private final int maxDepth = 10;
    private final OctreeNode<K, T> root;

    public VectorOctree(List<SimpleEntry<K, T>> points) {
        root = build(points);
    }

    @Override
    public List<SimpleEntry<K, T>> nearestNeighbor(K key, int k) {
        List<SimpleEntry<K, T>> neighbors = new ArrayList<>();

        PriorityQueue<OctreeNode<K, T>> pq = new PriorityQueue<>(Comparator.comparingInt(in -> distance(key, in)));
        pq.add(root);
        while (!pq.isEmpty() && neighbors.size() < k) {
            OctreeNode<K, T> node = pq.poll();
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
    private int distance(K source, OctreeNode<K, T> node) {
        // Return color-distance if node is a leaf
        if (node.isLeaf) {
            return HelperUtils.distance(source, node.value.getKey());
        }

        // Return bounds-distance if node is not a leaf
        return node.bounds.distance(source);
    }

    /**
     * Builds an octree with the given points
     * @param points The points the octree will contain
     * @return The root node of the octree
     */
    private OctreeNode<K, T> build(List<SimpleEntry<K, T>> points) {
        // Find bounds encompassing all points
        Bounds bounds = new Bounds();
        for (SimpleEntry<K, T> p : points) {
            K key = p.getKey();
            bounds.encapsulate(key);
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
    private OctreeNode<K, T> build(List<SimpleEntry<K, T>> points, Bounds bounds, int depth) {
        // Return leaf node is number of points less than threshold
        if (points.size() < 10 || depth > maxDepth) {
            List<OctreeNode<K, T>> children = new ArrayList<>();
            for (SimpleEntry<K, T> p : points) {
                children.add(new OctreeNode<>(bounds, p));
            }

            return new OctreeNode<>(bounds, children);
        }

        // Subdivide bounds
        Bounds[] octants = bounds.subdivide();
        List<List<SimpleEntry<K, T>>> childrenPoints = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            childrenPoints.add(new ArrayList<>());
        }

        // Add points to subdivisions
        for (SimpleEntry<K, T> point : points) {
            K key = point.getKey();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i].contains(key)) {
                    childrenPoints.get(i).add(point);
                    break;
                }
            }
        }

        List<OctreeNode<K, T>> children = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            children.add(build(childrenPoints.get(i), octants[i], depth + 1));
        }

        return new OctreeNode<>(bounds, children);
    }
}
