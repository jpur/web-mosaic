package mosaic.transformer;

import mosaic.util.MosaicMatcher;
import mosaic.data.MosaicImageInfo;
import mosaic.data.store.StoreClient;
import mosaic.util.helper.ColorUtils;
import mosaic.util.helper.HelperUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    protected final MosaicMatcher data;
    protected final Shape shape;
    protected final int size;

    private static final Random rand = new Random();

    private final StoreClient<int[]> mosaicStore;

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param data The sub-image data the transformer will use to transform images
     * @param shape The shape of the sub-images
     * @param size The pixel size of the sub-images
     */
    public MosaicTransformer(MosaicMatcher data, StoreClient<int[]> mosaicStore, Shape shape, int size) {
        this.data = data;
        this.mosaicStore = mosaicStore;
        this.shape = shape;
        this.size = size;
    }

    @Override
    public BufferedImage transform(BufferedImage image) throws IOException {
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

    /**
     * Replaces the pixels of a given tile of a target image
     * @param target The image to modify
     * @param x The start x-coordinate of the target tile
     * @param y The start y-coordinate of the target tile
     * @param tile The source tile
     * @param xSize The width of the source tile
     * @param ySize The height of the source tile
     */
    protected void draw(BufferedImage target, int x, int y, int[] tile, int xSize, int ySize) {
        target.setRGB(x, y, xSize, ySize, tile, 0, xSize);
    }

    /**
     * Get a pixel array to replace the given pixel array
     * @param arr The pixel array to replace
     * @return The pixel array to replace arr
     * @throws IOException Thrown if a match couldn't be found
     */
    protected int[] recolor(int[] arr) throws IOException {
        // Get a random image of the same color to avoid noticeable repeating patterns
        Color avgCol = ColorUtils.getAverageColor(arr);
        MosaicImageInfo imageInfo = HelperUtils.getRandom(data.getNearest(avgCol, 2));
        return mosaicStore.get(imageInfo.getName());
    }
}
