package mosaic.data.store.cache;

import java.io.IOException;

public abstract class CachedStore<T> {
    public abstract boolean has(String key);
    public abstract T get(String key) throws IOException;
    public abstract void cache(String key, T value) throws IOException;
    public abstract T decache(String key) throws IOException;
}
