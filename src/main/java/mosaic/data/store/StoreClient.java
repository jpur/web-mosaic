package mosaic.data.store;

import java.io.IOException;

public interface StoreClient<T> {
    T get(String key) throws IOException;
}
