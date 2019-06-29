package mosaic.transformer;

import mosaic.MosaicData;
import mosaic.util.ColorUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    private final MosaicData data;
    private final Shape shape;
    private final int size;

    public MosaicTransformer(MosaicData data, Shape shape, int size) {
        this.data = data;
        this.shape = shape;
        this.size = size;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage out = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height / size; y++) {
            for (int x = 0; x < width / size; x++) {
                int[] tile = recolor(image.getRGB(x * size, y * size, size, size, null, 0, size));
                draw(out, x * size, y * size, tile);
            }
        }

        return out;
    }

    private void draw(BufferedImage target, int x, int y, int[] tile) {
        target.setRGB(x, y, size, size, tile, 0, size);
    }

    private int[] recolor(int[] arr) {
        Color avgCol = ColorUtils.getAverageColor(arr);

        List<Color> nearest = data.getNearest(avgCol, 1);
        List<int[]> images = data.getImages(nearest.get(0));
        return images.get(0);
    }
}
