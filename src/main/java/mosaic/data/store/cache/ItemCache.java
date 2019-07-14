package mosaic.data.store.cache;

import java.io.IOException;

public interface ItemCache<T> {
    /**
     * Returns true if the given key is stored in the cache
     * @param key The key to search for
     * @return True if the key exists in the cache
     */
    boolean has(String key);

    /**
     * Gets the value associated with the given key
     * @param key The key to search for
     * @return The value the key is associated with
     * @throws IOException Thrown if the key does not exist
     */
    T get(String key) throws IOException;

    /**
     * Caches the given value with the associated key
     * @param key The key to associate the value with
     * @param value The value to cache
     * @throws IOException Thrown if the value could not be stored in the cache
     */
    void cache(String key, T value) throws IOException;

    /**
     * Removes the given key and its value from the cache
     * @param key The key to remove from the cache
     * @return The value the key was associated with
     * @throws IOException Thrown if the key does not exist in the cache
     */
    T decache(String key) throws IOException;
}
