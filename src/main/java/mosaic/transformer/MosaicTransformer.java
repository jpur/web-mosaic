package mosaic.transformer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    private Shape shape;
    private int size;

    private List<int[]>[][][] images;

    public MosaicTransformer(Shape shape, int size) {
        this.shape = shape;
        this.size = size;

        images = new ArrayList[256][256][256];
        for (int i = 0; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                for (int k = 0; k <= 255; k++) {
                    images[i][j][k] = new ArrayList<>();
                }
            }
        }


        File[] files = new File("src/main/resources/test").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    img = resize(img, size, size);
                    int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
                    Color avgCol = getAverageColour(rgb);
                    images[avgCol.getRed()][avgCol.getGreen()][avgCol.getBlue()].add(rgb);
                } catch (IOException e) {

                }
            }
        }
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
        Color avgCol = getAverageColour(arr);

        Color closest = kNN(images, avgCol, 1).get(0);
        return images[closest.getRed()][closest.getGreen()][closest.getBlue()].get(0);
    }

    private List<Color> kNN(List<int[]>[][][] images, Color origin, int k) {
        List<Color> out = new ArrayList<>();

        Set<Color> colors = new HashSet<>();
        colors.add(origin);
        while (k > 0 && colors.size() > 0) {
            for (Color color : colors) {
                List<int[]> replacements = images[color.getRed()][color.getGreen()][color.getBlue()];
                if (replacements.size() > 0) {
                    out.add(color);
                    if (--k == 0) return out;
                }
            }

            Set<Color> newColors = new HashSet<>();
            getNeighbors(origin, colors, newColors);
            colors = newColors;
        }

        return out;
    }

    private Color getAverageColour(int[] colours) {
        int r = 0, g = 0, b = 0;
        for (int i : colours) {
            Color col = new Color(i);
            r += col.getRed();
            g += col.getGreen();
            b += col.getBlue();
        }

        return new Color(r / colours.length, g / colours.length, b / colours.length);
    }

    private void getNeighbors(Color origin, Set<Color> fromSet, Set<Color> neighborSet) {
        for (Color from : fromSet) {
            List<Color> neighbors = new ArrayList<>();

            if (from.getRed() < 255) neighbors.add(new Color(from.getRed() + 1, from.getGreen(), from.getBlue()));
            if (from.getRed() > 0) neighbors.add(new Color(from.getRed() - 1, from.getGreen(), from.getBlue()));

            if (from.getGreen() < 255) neighbors.add(new Color(from.getRed(), from.getGreen() + 1, from.getBlue()));
            if (from.getGreen() > 0) neighbors.add(new Color(from.getRed(), from.getGreen() - 1, from.getBlue()));

            if (from.getBlue() < 255) neighbors.add(new Color(from.getRed(), from.getGreen(), from.getBlue() + 1));
            if (from.getBlue() > 0) neighbors.add(new Color(from.getRed(), from.getGreen(), from.getBlue() - 1));

            for (Color n : neighbors) {
                if (getColorDistance(n, origin) > getColorDistance(from, origin)) {
                    neighborSet.add(n);
                }
            }
        }
    }

    private int getColorDistance(Color a, Color b) {
        return Math.abs(a.getRed() - b.getRed()) + Math.abs(a.getGreen() - b.getGreen()) + Math.abs(a.getBlue() - b.getBlue());
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
