package mosaic.data.store;

import java.io.IOException;

/**
 * A wrapper for an ImageStore
 * @param <T> The class that relevant image data will be stored in
 */
public interface StoreClient<T> {
    T get(String key) throws IOException;
}
