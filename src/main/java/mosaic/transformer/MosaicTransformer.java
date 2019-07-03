package mosaic.transformer;

import mosaic.MosaicData;
import mosaic.util.ColorUtils;
import mosaic.util.HelperUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    protected final MosaicData data;
    protected final Shape shape;
    protected final int size;

    private static final Random rand = new Random();

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param data The sub-image data the transformer will use to transform images
     * @param shape The shape of the sub-images
     * @param size The pixel size of the sub-images
     */
    public MosaicTransformer(MosaicData data, Shape shape, int size) {
        this.data = data;
        this.shape = shape;
        this.size = size;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height / size; y++) {
            for (int x = 0; x < width / size; x++) {
                // Get sub-image to replace subsection of image
                int[] tile = recolor(image.getRGB(x * size, y * size, size, size, null, 0, size));

                // Draw sub-image to corresponding subsection of mosaic
                draw(out, x * size, y * size, tile, size, size);
            }
        }

        return out;
    }

    protected void draw(BufferedImage target, int x, int y, int[] tile, int xSize, int ySize) {
        target.setRGB(x, y, xSize, ySize, tile, 0, xSize);
    }

    protected int[] recolor(int[] arr) {
        Color avgCol = ColorUtils.getAverageColor(arr);
        return HelperUtils.getRandom(data.getNearest(avgCol, 2));
    }
}
