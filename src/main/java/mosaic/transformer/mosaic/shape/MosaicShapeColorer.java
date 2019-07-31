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

        /**
         * Initializes parameters of the mosaic sub-task
         * @param source The image to use as a reference for tile replacement
         * @param target The image to output replaced tiles on
         * @param tileStart The start range of the tiles to replace
         * @param tileEnd The end range of the tiles to replace
         * @param offsetX The offset of each tile's x position
         * @param offsetY The offset of each tile's y position
         */
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
            // Calculate the number of tiles that fit onto one row with added offsets
            int tilesPerRow = (int)Math.ceil(((double)source.getWidth() - offsetX) / size);

            for (int i = tileStart; i < tileEnd; i++) {
                // Find the upper-left x and y coordinates of the current tile
                int x = i % tilesPerRow * size + offsetX;
                int y = i / tilesPerRow * size + offsetY;

                try {
                    ImageTile sect = new ImageTile(target, x, y);

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

    /**
     * Represents a tile (of size in the MosaicShapeColorer) of an image for retrieving and modifying the pixels of
     */
    private class ImageTile {
        private final BufferedImage image;

        private final int constrainedX;
        private final int constrainedY;

        private final int constrainedXSize;
        private final int constrainedYSize;

        private final int xOffset;
        private final int yOffset;

        /**
         * Initializes the image tile
         * @param image The image the tile corresponds to
         * @param x The top-left x position of the tile
         * @param y The top-left y position of the tile
         */
        public ImageTile(BufferedImage image, int x, int y) {
            this.image = image;

            // Compute constrained bounds for image to avoid out-of-bounds indexing
            constrainedX = HelperUtils.clamp(x, 0, image.getWidth());
            constrainedY = HelperUtils.clamp(y, 0, image.getHeight());
            constrainedXSize = HelperUtils.clamp(x + size, 0, image.getWidth()) - constrainedX;
            constrainedYSize = HelperUtils.clamp(y + size, 0, image.getHeight()) - constrainedY;
            xOffset = constrainedX - x;
            yOffset = constrainedY - y;
        }

        /**
         * Get the pixels of this tile
         * @param sizeArr A tile-sized array of pixels to copy this tile's pixels to (indices corresponding to pixels outside the bounds of the image are unchanged)
         * @return The pixel array that was copied to the given array
         */
        public int[] getPixels(int[] sizeArr) {
            int[] pixels = image.getRGB(constrainedX, constrainedY, constrainedXSize, constrainedYSize, null, 0, constrainedXSize);
            for (int ix = 0; ix < constrainedXSize; ix++) {
                for (int iy = 0; iy < constrainedYSize; iy++) {
                    sizeArr[(iy + yOffset) * size + xOffset + ix] = pixels[iy * constrainedXSize + ix];
                }
            }

            return pixels;
        }

        /**
         * Copies the pixels of the given tile-sized array to this tile
         * @param pixels The pixels to copy to this tile
         */
        public void setPixels(int[] pixels) {
            for (int ix = 0; ix < constrainedXSize; ix++) {
                for (int iy = 0; iy < constrainedYSize; iy++) {
                    int color = pixels[(yOffset + iy) * size + (xOffset + ix)];
                    image.setRGB(constrainedX + ix, constrainedY + iy, color);
                }
            }
        }
    }


    protected final int size;

    private final MosaicMatcher matcher;
    private final StoreClient<int[]> mosaicStore;

    private final Polygon polygon;

    /**
     * Initializes the shape colorer
     * @param matcher The matcher used for retrieving a sub-image to use for coloring a shape
     * @param mosaicStore The image store used for retrieving the pixels of a sub-image returned by a matcher
     * @param size The size of the tiles the mosaic is broken up into
     */
    public MosaicShapeColorer(MosaicMatcher matcher, StoreClient<int[]> mosaicStore, int size) {
        this.matcher = matcher;
        this.mosaicStore = mosaicStore;
        this.size = size;
        this.polygon = generatePolygon(size);
    }

    /**
     * Returns all sub-tasks of the mosaic required to complete it
     * @param source The image to create a mosaic of
     * @param target The output mosaic image (usually a copy of source)
     * @param maxTilesPerTask The maximum number of tiles an individual task should process
     * @return A list of sub-tasks for the mosaic
     */
    public abstract List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask);

    /**
     * Returns a polygon used to perform an intersection on the pixels of each tile mosaic (i.e. finding pixels contained within it)
     * @param size The size of the polygon
     * @return The polygon used for checking pixel intersection
     */
    protected abstract Polygon generatePolygon(int size);

    /**
     * Retrieves an appropriate pixel array to replace the given pixel array
     * @param arr The pixel array we want to replace
     * @param color The key used for searching for a replacement pixel array
     * @return The pixel array to replace the given pixel array
     * @throws IOException Thrown if no pixel array could be found
     */
    private int[] getRecolorTile(int[] arr, Color color) throws IOException {
        MosaicImageInfo imageInfo = HelperUtils.getRandom(matcher.getNearest(color, 2));
        return mosaicStore.get(imageInfo.getName());
    }

    /**
     * Replaces target pixels with source pixels where the source pixels are part of our desired shape
     * @param target The pixel array to copy to
     * @param source The pixel array to copy from
     */
    private void recolor(int[] target, int[] source) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (polygon.contains(x, y)) {
                    target[y * size + x] = source[y * size + x];
                }
            }
        }
    }
}
