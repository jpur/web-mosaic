package mosaic.transformer;

import mosaic.util.MosaicMatcher;
import mosaic.data.store.StoreClient;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ThreadedMosaicTransformer extends MosaicTransformer {
    /**
     * Run on worker threads; replaces pixels of a set number of tiles
     */
    private class MosaicTask implements Callable<Object> {
        private final BufferedImage source;
        private final BufferedImage target;
        private final int tileStart;
        private final int tileEnd;

        /**
         * Initializes parameters of the mosaic sub-task
         * @param source The image to use as a reference for tile replacement
         * @param target The image to output replaced tiles on
         * @param tileStart The start range of the tiles to replace
         * @param tileEnd The end range of the tiles to replace
         */
        public MosaicTask(BufferedImage source, BufferedImage target, int tileStart, int tileEnd) {
            this.source = source;
            this.target = target;
            this.tileStart = tileStart;
            this.tileEnd = tileEnd;
        }

        @Override
        public Object call() {
            int tilesPerRow = (int)Math.ceil((double)source.getWidth() / size);
            for (int i = tileStart; i < tileEnd; i++) {
                // Find the upper-left x and y coordinates of the current tile
                int x = i % tilesPerRow * size;
                int y = i / tilesPerRow * size;

                // Find the size of the tile (possibly smaller than our given size when near the boundaries of the source image)
                int xSize = Math.min(size, source.getWidth() - x);
                int ySize = Math.min(size, source.getHeight() - y);

                try {
                    // Get sub-image to replace subsection of image
                    int[] tile = recolor(source.getRGB(x, y, xSize, ySize, null, 0, xSize));

                    // Draw sub-image to corresponding subsection of mosaic
                    draw(target, x, y, tile, xSize, ySize);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return target;
        }
    }

    // The maximum number of tiles a task should be responsible for
    private final int maxTilesPerTask = 100;
    private final ExecutorService executor;

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param data The sub-image data the transformer will use to transform images
     * @param shape The shape of the sub-images
     * @param size The pixel size of the sub-images
     */
    public ThreadedMosaicTransformer(ExecutorService executor, MosaicMatcher data, StoreClient<int[]> mosaicStore, Shape shape, int size) {
        super(data, mosaicStore, shape, size);
        this.executor = executor;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        // Split all tiles of the image into equally-sized tasks
        List<MosaicTask> tasks = new ArrayList<>();
        int numTiles = (int)(Math.ceil((double)height / size) * Math.ceil((double)width / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(image, out, i, Math.min(i + maxTilesPerTask, numTiles)));
        }

        // Pass tasks to executor
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
