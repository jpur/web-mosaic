package mosaic.data.store;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorImageStoreClient implements StoreClient<int[]> {
    private final ImageStore store;

    public ColorImageStoreClient(ImageStore store) {
        this.store = store;
    }

    @Override
    public int[] getImage(String key) throws IOException {
        // TODO: Caching
        File file = store.get(key);
        BufferedImage image = ImageIO.read(file);

        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }
}
