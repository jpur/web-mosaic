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
                int[] tile = recolor(x, y, image.getRGB(x * size, y * size, size, size, null, 0, size));
                draw(out, x * size, y * size, tile);
            }
        }

        return out;
    }

    private void draw(BufferedImage target, int x, int y, int[] tile) {
        target.setRGB(x, y, size, size, tile, 0, size);
    }

    private int[] recolor(int x, int y, int[] arr) {
        int r = 0, g = 0, b = 0;
        for (int i : arr) {
            Color col = new Color(i);
            r += col.getRed();
            g += col.getGreen();
            b += col.getBlue();
        }

        Color avgCol = new Color(r/arr.length, g/arr.length, b/arr.length);
        System.out.printf("(%d %d %d) %s %d %d ", r, g, b, avgCol.toString(), arr.length, size);

        int[] out = new int[arr.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = avgCol.getRGB();
        }

        return out;
    }
}
