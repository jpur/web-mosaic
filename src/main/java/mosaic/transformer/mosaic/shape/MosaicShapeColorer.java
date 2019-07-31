package mosaic.transformer.mosaic.shape;

import mosaic.data.MosaicImageInfo;
import mosaic.data.store.StoreClient;
import mosaic.util.MosaicMatcher;
import mosaic.util.helper.ColorUtils;
import mosaic.util.helper.HelperUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class MosaicShapeColorer {
    /**
     * Run on worker threads; replaces pixels of a set number of tiles
     */
    public class MosaicTask implements Callable<Object> {
        private final BufferedImage source;
        private final BufferedImage target;
        private final int tileStart;
        private final int tileEnd;

        private int offsetX;
        private int offsetY;

        /**
         * Initializes parameters of the mosaic sub-task
         * @param source The image to use as a reference for tile replacement
         * @param target The image to output replaced tiles on
         * @param tileStart The start range of the tiles to replace
         * @param tileEnd The end range of the tiles to replace
         */
        public MosaicTask(BufferedImage source, BufferedImage target, int tileStart, int tileEnd) {
            this(source, target, tileStart, tileEnd, 0, 0);
        }

        public MosaicTask(BufferedImage source, BufferedImage target, int tileStart, int tileEnd, int offsetX, int offsetY) {
            this.source = source;
            this.target = target;
            this.tileStart = tileStart;
            this.tileEnd = tileEnd;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public Object call() {
            int tilesPerRow = (int)Math.ceil((double)source.getWidth() / size);
            for (int i = tileStart; i < tileEnd; i++) {
                // Find the upper-left x and y coordinates of the current tile
                int x = i % tilesPerRow * size + offsetX;
                int y = i / tilesPerRow * size + offsetY;

                try {
                    ImageSection sect = new ImageSection(target, x, y);

                    // Get subsection of image to replace
                    int[] sizedArr = new int[size * size];
                    int[] pixels = sect.getPixels(sizedArr);
                    Color avgColor = ColorUtils.getAverageColor(pixels);

                    // Recolor source image with appropriate sub-image
                    recolor(sizedArr, getRecolorTile(sizedArr, avgColor));

                    // Draw sub-image to corresponding subsection of mosaic
                    sect.setPixels(sizedArr);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return target;
        }
    }

    private class ImageSection {
        private final BufferedImage image;

        private final int x;
        private final int y;

        private final int constrainedX;
        private final int constrainedY;

        private final int constrainedXSize;
        private final int constrainedYSize;

        public ImageSection(BufferedImage image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;

            // Compute constrained bounds for image
            constrainedX = HelperUtils.clamp(x, 0, image.getWidth());
            constrainedY = HelperUtils.clamp(y, 0, image.getHeight());
            constrainedXSize = HelperUtils.clamp(x + size, 0, image.getWidth()) - constrainedX;
            constrainedYSize = HelperUtils.clamp(y + size, 0, image.getHeight()) - constrainedY;
        }

        public int[] getPixels(int[] sizeArr) {
            int xOffset = constrainedX - x;
            int yOffset = constrainedY - y;

            int[] pixels = image.getRGB(constrainedX, constrainedY, constrainedXSize, constrainedYSize, null, 0, constrainedXSize);
            for (int ix = 0; ix < constrainedXSize; ix++) {
                for (int iy = 0; iy < constrainedYSize; iy++) {
                    sizeArr[(iy + yOffset) * size + xOffset + ix] = pixels[iy * constrainedXSize + ix];
                }
            }

            return pixels;
        }

        public void setPixels(int[] pixels) {
            int xOffset = constrainedX - x;
            int yOffset = constrainedY - y;

            for (int ix = 0; ix < constrainedXSize; ix++) {
                for (int iy = 0; iy < constrainedYSize; iy++) {
                    int color = pixels[(yOffset + iy) * size + (xOffset + ix)];
                    image.setRGB(constrainedX + ix, constrainedY + iy, color);
                }
            }
        }
    }

    protected MosaicMatcher matcher;
    protected StoreClient<int[]> mosaicStore;

    protected Polygon polygon;
    protected int size;

    public MosaicShapeColorer(MosaicMatcher matcher, StoreClient<int[]> mosaicStore, int size) {
        this.matcher = matcher;
        this.mosaicStore = mosaicStore;
        this.size = size;
        this.polygon = generatePolygon(size);
    }

    public abstract List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask);

    public void recolor(int[] target, int[] source) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (polygon.contains(x, y)) {
                    target[y * size + x] = source[y * size + x];
                }
            }
        }
    }

    protected abstract Polygon generatePolygon(int size);

    protected int[] getRecolorTile(int[] arr, Color color) throws IOException {
        MosaicImageInfo imageInfo = HelperUtils.getRandom(matcher.getNearest(color, 2));
        return mosaicStore.get(imageInfo.getName());
    }
}
