package mosaic.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.AbstractMap.SimpleEntry;

/**
 * An octree implementation that uses vector keys for positional information
 * @param <K> The vector type for specifying points in space
 * @param <T> The value type that each point holds
 */
public class VectorOctree<K extends Vector3i, T> implements Octree<K, T> {
    /**
     * The maximum depth the octree will be built to
     */
    private final int maxDepth = 10;

    /**
     * The root node of the octree
     */
    private final OctreeNode<K, T> root;

    /**
     * Constructs an octree from the given key-value pairs
     * @param points The position-value points to be contained in the octree
     */
    public VectorOctree(List<SimpleEntry<K, T>> points) {
        root = build(points);
    }

    /**
     * Retursn the nearest distance-wise k neighbors from the given key
     * @param key The key to search for neighbors from
     * @param k The maximum number of neighbors to be returned
     * @return The key-value pairs of the nearest distance-wise neighbors from the given key
     */
    @Override
    public List<SimpleEntry<K, T>> nearestNeighbor(K key, int k) {
        List<SimpleEntry<K, T>> neighbors = new ArrayList<>();

        // Initialize priority queue based on distance from the given key
        PriorityQueue<OctreeNode<K, T>> pq = new PriorityQueue<>(Comparator.comparingInt(in -> distance(key, in)));
        pq.add(root);
        while (!pq.isEmpty() && neighbors.size() < k) {
            // Get node closest to key
            OctreeNode<K, T> node = pq.poll();
            if (node.isLeaf) {
                // Leaf and closest node so it must be a neighbor, add to neighbor list
                neighbors.add(node.value);
            } else {
                // Non-leaf and closest node so it may contain close nodes, add its children to priority queue
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
            return Vector3i.distance(source, node.value.getKey());
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

        // Construct child subtrees
        List<OctreeNode<K, T>> children = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            children.add(build(childrenPoints.get(i), octants[i], depth + 1));
        }

        // Return root of subtrees
        return new OctreeNode<>(bounds, children);
    }
}
