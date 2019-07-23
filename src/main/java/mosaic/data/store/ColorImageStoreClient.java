package mosaic.data.store;

import mosaic.data.store.cache.InMemoryCache;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A wrapper for accessing and caching pixel data from an image store
 */
public class ColorImageStoreClient implements StoreClient<int[]> {
    // The maximum size of the cache
    private static final int cacheSize = 10000;

    private final ImageStore store;
    private final InMemoryCache<int[]> cache;

    public ColorImageStoreClient(ImageStore store) {
        this.store = store;
        this.cache = new InMemoryCache<>(cacheSize);
    }

    @Override
    public int[] get(String key) throws IOException {
        if (cache.has(key)) return cache.get(key);

        // Retrieved key which doesn't exist in our cache, so store it there
        File file = store.get(key);
        BufferedImage image = ImageIO.read(file);
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        cache.cache(key, rgb);
        return rgb;
    }
}
