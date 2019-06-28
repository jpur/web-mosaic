package mosaic.transformer;

import java.awt.image.BufferedImage;

public interface ImageTransformer {
    BufferedImage transform(BufferedImage image);
}
