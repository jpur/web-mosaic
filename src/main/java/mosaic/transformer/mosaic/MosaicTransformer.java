package mosaic.transformer.mosaic;

import mosaic.transformer.ImageTransformer;
import mosaic.transformer.mosaic.shape.MosaicShapeColorer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MosaicTransformer implements ImageTransformer {
    protected final MosaicShapeColorer shape;

    private static final Random rand = new Random();

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param shape The shape of the sub-images
     */
    public MosaicTransformer(MosaicShapeColorer shape) {
        this.shape = shape;
    }

    @Override
    public BufferedImage transform(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        List<MosaicShapeColorer.MosaicTask> tasks = shape.getMosaicTasks(image, out, Integer.MAX_VALUE);
        for (MosaicShapeColorer.MosaicTask task : tasks) {
            task.call();
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
}
