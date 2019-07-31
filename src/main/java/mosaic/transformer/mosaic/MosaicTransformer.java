package mosaic.transformer.mosaic;

import mosaic.transformer.ImageTransformer;
import mosaic.transformer.mosaic.shape.MosaicShapeColorer;
import mosaic.util.helper.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.List;

public class MosaicTransformer implements ImageTransformer {
    protected final MosaicShapeColorer shape;

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param shape The shape of the sub-images
     */
    public MosaicTransformer(MosaicShapeColorer shape) {
        this.shape = shape;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        BufferedImage out = ImageUtils.copy(image);

        List<MosaicShapeColorer.MosaicTask> tasks = shape.getMosaicTasks(image, out, Integer.MAX_VALUE);
        for (MosaicShapeColorer.MosaicTask task : tasks) {
            task.call();
        }

        return out;
    }
}
