package mosaic.data.store;

import java.io.IOException;

public interface StoreClient<T> {
    T getImage(String key) throws IOException;
}
