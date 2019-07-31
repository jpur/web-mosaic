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
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        List<MosaicShapeColorer.MosaicTask> tasks = shape.getMosaicTasks(image, out, Integer.MAX_VALUE);
        for (MosaicShapeColorer.MosaicTask task : tasks) {
            task.call();
        }

        return out;
    }
}
