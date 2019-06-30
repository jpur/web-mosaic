package mosaic.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public interface Octree<K, V> {
    final class OctreeNode<K, V> {
        public final boolean isLeaf;
        public final Bounds bounds;
        public final List<OctreeNode<K, V>> children;
        public final SimpleEntry<K, V> value;

        public OctreeNode(Bounds bounds, SimpleEntry<K, V> value) {
            this.isLeaf = true;
            this.bounds = bounds;
            this.children = null;
            this.value = value;
        }

        public OctreeNode(Bounds bounds, List<OctreeNode<K, V>> children) {
            this.isLeaf = false;
            this.bounds = bounds;
            this.value = null;
            this.children = children;
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
