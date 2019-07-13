package mosaic.data.store.cache;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryCache<T> extends CachedStore<T> {
    private final Map<String, T> map;
    private final int maxEntries;

    public InMemoryCache(int maxEntries) {
        Map<String, T> lru = new LinkedHashMap<String, T>() {
            @Override
            public boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                return size() > maxEntries;
            }
        };

        // Very inefficient, though a synchronized LinkedHashMap seems tricky to do
        this.map = Collections.synchronizedMap(lru);
        this.maxEntries = maxEntries;
    }

    @Override
    public boolean has(String key) {
        return map.containsKey(key);
    }

    @Override
    public T get(String key) throws IOException {
        if (!has(key)) throw new CacheNotFoundException();
        return map.get(key);
    }

    @Override
    public void cache(String key, T value) {
        map.put(key, value);
    }

    @Override
    public T decache(String key) throws IOException {
        if (!has(key)) throw new CacheNotFoundException();
        return map.remove(key);
    }
}
