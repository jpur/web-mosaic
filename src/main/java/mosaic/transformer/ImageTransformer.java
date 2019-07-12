package mosaic.transformer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageTransformer {
    /**
     * Transforms a given image
     * @param image The image to transform
     * @return The transformed image
     */
    BufferedImage transform(BufferedImage image) throws IOException;
}
