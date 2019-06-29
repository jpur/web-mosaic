package mosaic.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public abstract class Octree<K, V> {
    public Octree(List<? extends SimpleEntry<K, V>> points) {
    }

    public abstract List<? extends SimpleEntry<K, V>> nearestNeighbor(K key, int k);
}
