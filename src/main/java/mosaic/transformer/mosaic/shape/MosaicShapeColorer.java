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

                // Find the size of the tile (possibly smaller than our given size when near the boundaries of the source image)
                int xSize = Math.min(size, source.getWidth() - (x % source.getWidth()));
                int ySize = Math.min(size, source.getHeight() - (y % source.getHeight()));

                try {
                    ImageSection sect = new ImageSection(target, x, y, size, size);

                    // Get sub-image to replace subsection of image
                    int[] pixels = sect.getPixels();
                    recolor(pixels);

                    // Draw sub-image to corresponding subsection of mosaic
                    sect.setPixels(pixels);
                } catch (Exception e) {
                    System.out.printf("%d %d %d %d\n", x, y, xSize, ySize);
                    e.printStackTrace();
                }

            }

            return target;
        }
    }

    private static class ImageSection {
        private final int x;
        private final int y;
        private final int xSize;
        private final int ySize;

        private final BufferedImage image;

        public ImageSection(BufferedImage image, int x, int y, int xSize, int ySize) {
            this.x = x;
            this.y = y;
            this.xSize = xSize;
            this.ySize = ySize;
            this.image = image;
        }

        public int[] getPixels() {
            int[] out = new int[xSize * ySize];
            int constrainedX = HelperUtils.clamp(x, 0, image.getWidth()-1);
            int constrainedXSize = HelperUtils.clamp(x + xSize, 0, image.getWidth()-1) - x;

            int constrainedY = HelperUtils.clamp(y, 0, image.getHeight()-1);
            int constrainedYSize = HelperUtils.clamp(y + ySize, 0, image.getHeight()-1) - y;

            int xOffset = xSize - constrainedXSize;
            int yOffset = ySize - constrainedYSize;

            int[] pixels = image.getRGB(constrainedX, constrainedY, constrainedXSize, constrainedYSize, null, 0, constrainedXSize);
            int pIdx = 0;
            for (int ix = 0; ix < constrainedXSize; ix++) {
                for (int iy = 0; iy < constrainedYSize; iy++) {
                    out[(iy+yOffset) * xSize + xOffset + ix] = pixels[pIdx++];
                }
            }

            return out;
        }

        public void setPixels(int[] pixels) {
            int constrainedX = HelperUtils.clamp(x, 0, image.getWidth());
            int constrainedXSize = HelperUtils.clamp(x + xSize, 0, image.getWidth() - 1) - x;

            int constrainedY = HelperUtils.clamp(y, 0, image.getHeight());
            int constrainedYSize = HelperUtils.clamp(y + ySize, 0, image.getHeight() - 1) - y;

            int xOffset = xSize - constrainedXSize;
            int yOffset = ySize - constrainedYSize;

            try {
                int pIdx = 0;
                for (int ix = 0; ix < constrainedXSize; ix++) {
                    for (int iy = 0; iy < constrainedYSize; iy++) {
                        int color = pixels[(yOffset + iy) * xSize + (xOffset + ix)];
                        image.setRGB(constrainedX + ix, constrainedY + iy, color);
                    }
                }
            } catch (Exception e) {
                //System.out.printf("%d %d %d %d | %d %d %d %d\n", x, y, xSize, ySize, constrainedX, constrainedY, constrainedXSize, constrainedYSize);
                e.printStackTrace();
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

    public void recolor(int[] target) throws IOException {
        int[] source = getRecolorTile(target);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (polygon.contains(x, y)) {
                    target[y * size + x] = source[y * size + x];
                }
            }
        }
    }

    public abstract List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask);

    protected abstract Polygon generatePolygon(int size);

    protected int[] getRecolorTile(int[] arr) throws IOException {
        Color avgCol = ColorUtils.getAverageColor(arr);
        MosaicImageInfo imageInfo = HelperUtils.getRandom(matcher.getNearest(avgCol, 2));

        return mosaicStore.get(imageInfo.getName());
    }
}
