package mosaic.transformer;

import mosaic.MosaicData;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ThreadedMosaicTransformer extends MosaicTransformer {
    private class MosaicTask implements Callable<Object> {
        private final BufferedImage source;
        private final BufferedImage target;
        private final int tileStart;
        private final int tileEnd;

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
                int x = i % tilesPerRow * size;
                int y = i / tilesPerRow * size;

                int xSize = Math.min(size, source.getWidth() - x);
                int ySize = Math.min(size, source.getHeight() - y);

                // Get sub-image to replace subsection of image
                int[] tile = recolor(source.getRGB(x, y, xSize, ySize, null, 0, xSize));

                // Draw sub-image to corresponding subsection of mosaic
                draw(target, x, y, tile, xSize, ySize);
            }

            return null;
        }
    }

    private final int maxTilesPerTask = 100;
    private final ExecutorService executor;

    /**
     * Constructs a mosaic transformer with the given parameters
     * @param data The sub-image data the transformer will use to transform images
     * @param shape The shape of the sub-images
     * @param size The pixel size of the sub-images
     */
    public ThreadedMosaicTransformer(ExecutorService executor, MosaicData data, Shape shape, int size) {
        super(data, shape, size);
        this.executor = executor;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        List<MosaicTask> tasks = new ArrayList<>();
        int numTiles = (int)(Math.ceil((double)height / size) * Math.ceil((double)width / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(image, out, i, Math.min(i + maxTilesPerTask, numTiles)));
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
