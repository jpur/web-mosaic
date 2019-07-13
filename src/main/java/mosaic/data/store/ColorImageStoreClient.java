package mosaic.data.store;

import mosaic.data.store.cache.InMemoryCache;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorImageStoreClient implements StoreClient<int[]> {
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
        System.out.println("don't have cached: " + key);

        File file = store.get(key);
        BufferedImage image = ImageIO.read(file);
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        cache.cache(key, rgb);
        return rgb;
    }
}
