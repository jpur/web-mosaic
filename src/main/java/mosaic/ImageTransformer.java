package mosaic;

import java.awt.image.BufferedImage;

public interface ImageTransformer {
    BufferedImage transform(BufferedImage image);
}
