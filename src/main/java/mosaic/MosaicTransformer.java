package mosaic;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    private Shape shape;
    private int size;

    public MosaicTransformer(Shape shape, int size) {
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
                int[] tile = recolor(x, y, image.getRGB(x * size, y * size, size, size, null, 0, width));
                draw(out, x * size, y * size, tile);
            }
        }

        return out;
    }

    private void draw(BufferedImage target, int x, int y, int[] tile) {
        target.setRGB(x, y, size, size, tile, 0, target.getWidth());
    }

    private int[] recolor(int x, int y, int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        int avg = sum / arr.length;

        int[] out = new int[arr.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = avg;
        }

        return out;
    }
}
