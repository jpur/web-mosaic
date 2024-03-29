package mosaic.transformer.mosaic;

import mosaic.transformer.mosaic.shape.ShapeMosaicColorer;
import mosaic.util.helper.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ThreadedMosaicTransformer extends MosaicTransformer {
    // The maximum number of tiles a task should be responsible for
    private final int maxTilesPerTask = 100;
    private final ExecutorService executor;

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param shape The shape of the sub-images
     */
    public ThreadedMosaicTransformer(ExecutorService executor, ShapeMosaicColorer shape) {
        super(shape);
        this.executor = executor;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        BufferedImage out = ImageUtils.copy(image);

        // Split all tiles of the image into equally-sized tasks
        List<ShapeMosaicColorer.MosaicTask> tasks = shape.getMosaicTasks(image, out, maxTilesPerTask);

        // Pass tasks to executor
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
