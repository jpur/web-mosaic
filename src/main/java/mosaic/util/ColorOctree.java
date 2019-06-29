package mosaic.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.PriorityQueue;

public class ColorOctree extends Octree<Color, List<int[]>> {
    private static final class ColorOctreeNode {
        public final boolean isLeaf;
        public final Bounds bounds;
        public final List<ColorOctreeNode> children;
        public final ColorCollection value;

        public ColorOctreeNode(Bounds bounds, ColorCollection value) {
            this.isLeaf = true;
            this.bounds = bounds;
            this.value = value;
            children = null;
        }

        public ColorOctreeNode(Bounds bounds, List<ColorOctreeNode> children) {
            this.isLeaf = false;
            this.bounds = bounds;
            this.children = children;
            value = null;
        }
    }

    private final int maxDepth = 10;
    private final ColorOctreeNode root;

    public ColorOctree(List<ColorCollection> points) {
        super(points);
        root = build(points);
    }

    @Override
    public List<ColorCollection> nearestNeighbor(Color color, int k) {
        List<ColorCollection> neighbors = new ArrayList<>();

        PriorityQueue<ColorOctreeNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> distance(color, a)));
        pq.add(root);
        while (!pq.isEmpty() && neighbors.size() < k) {
            ColorOctreeNode node = pq.poll();
            if (node.isLeaf) {
                neighbors.add(node.value);
            } else {
                pq.addAll(node.children);
            }
        }

        return neighbors;
    }

    private int distance(Color source, ColorOctreeNode node) {
        if (node.isLeaf) {
            return ColorUtils.getDistance(source, node.value.getKey());
        }

        return node.bounds.distance(source.getRed(), source.getGreen(), source.getBlue());
    }

    private ColorOctreeNode build(List<ColorCollection> points) {
        // Find bounds encompassing all points
        Bounds bounds = new Bounds();
        for (SimpleEntry<Color, List<int[]>> p : points) {
            Color color = p.getKey();
            bounds.encapsulate(color.getRed(), color.getGreen(), color.getBlue());
        }

        return build(points, bounds, 0);
    }

    private ColorOctreeNode build(List<ColorCollection> points, Bounds bounds, int depth) {
        // Return leaf node is number of points less than threshold
        if (points.size() < 10 || depth > maxDepth) {
            List<ColorOctreeNode> children = new ArrayList<>();
            for (ColorCollection p : points) {
                children.add(new ColorOctreeNode(bounds, p));
            }

            return new ColorOctreeNode(bounds, children);
        }

        // Subdivide bounds
        Bounds[] octants = bounds.subdivide();
        List<List<ColorCollection>> childrenPoints = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            childrenPoints.add(new ArrayList<>());
        }

        // Add points to subdivisions
        for (ColorCollection point : points) {
            Color color = point.getKey();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i].contains(color.getRed(), color.getGreen(), color.getBlue())) {
                    childrenPoints.get(i).add(point);
                    break;
                }
            }
        }

        List<ColorOctreeNode> children = new ArrayList<>();
        for (int i = 0; i < octants.length; i++) {
            children.add(build(childrenPoints.get(i), octants[i], depth + 1));
        }

        return new ColorOctreeNode(bounds, children);
    }
}
