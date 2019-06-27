package mosaic;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    private Shape shape;
    private int size;

    public MosaicTransformer(Shape shape, int size) {
        this.shape = shape;
        this.size = size;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        return image;
    }
}
