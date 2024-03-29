package mosaic.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * A data structure for efficiently searching a 3D space
 * @param <K> The key type that defines the position of values in the 3D space
 * @param <V> The value type associated with each key
 */
public interface Octree<K, V> {
    final class OctreeNode<K, V> {
        public final Bounds bounds;
        public final List<OctreeNode<K, V>> children;
        public final SimpleEntry<K, V> value;

        public OctreeNode(Bounds bounds, SimpleEntry<K, V> value) {
            this.bounds = bounds;
            this.children = null;
            this.value = value;
        }

        public OctreeNode(Bounds bounds, List<OctreeNode<K, V>> children) {
            this.bounds = bounds;
            this.value = null;
            this.children = children;
        }

        public boolean isLeaf() {
            return value != null;
        }
    }

    /**
     * Returns the nearest neighbors from the given key
     * @param key The key to search for neighbors from
     * @param k The maximum number of neighbors to be returned
     * @return Key-value pairs of the nearest neighbors
     */
    List<? extends SimpleEntry<K, V>> nearestNeighbor(K key, int k);
}
