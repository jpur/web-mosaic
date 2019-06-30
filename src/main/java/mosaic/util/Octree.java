package mosaic.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public interface Octree<K, V> {
    List<? extends SimpleEntry<K, V>> nearestNeighbor(K key, int k);
}
